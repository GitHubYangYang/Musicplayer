package com.example.musicplayer.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter.BigDecimalLayoutForm;
import java.util.List;

import com.example.musicplayer.R;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

public class MediaUtil {

	private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
	
	public static List<Music> getMusic(Context context){
		List<Music> music = new ArrayList<Music>();
    	Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
    			null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    	while(cur.moveToNext()){
    		String dn = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
    		if(dn.endsWith("mp3")){
    		    Music m = new Music();
    		    m.setDiaplayName(dn);
    		    m.setArtist(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
    		    m.setDuration(cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
    		    m.setSize(cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
    		    m.setId(cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
    		    m.setUrl(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
    		    m.setAlbum(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
    		    m.setAlbimId(cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
    		    music.add(m);
    		}
    	}
    	return music;
	}
	
	public void delectMusic(List<Music> music) throws FileNotFoundException{
		for(int i = 0;i < music.size();i++){
			String url = music.get(i).getUrl();
			File f = new File(url);
			if(f.exists() && !f.isDirectory()){
				f.delete();
			}
		}
	}
	
	public static String formatTime(long time){
		long allSeconds = time/1000;
		String min = allSeconds/60 + "";
		String sec = allSeconds%60 +"";
		if(min.length() < 2)
			min = "0" + min;
		if(sec.length() < 2)
			sec = "0" + sec;
		return min + ":" + sec;
	}
	//get default music image
	public static Bitmap getDefaultArtWork(Context context){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.img_album_default), null, bfo);
	}
	//get music image from music album
	public static Bitmap getArtWorkFromFile(Context context,long songID,long albumId){
		Bitmap bm = null;
		if(songID < 0 && albumId <0){
			throw new IllegalArgumentException("you must specify an albumid or song id");
		}
		try {
		    BitmapFactory.Options bfo = new BitmapFactory.Options();
			FileDescriptor fd = null;
			if(albumId < 0){
				Uri uri = Uri.parse("content://media/external/audio/media/" + songID + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null)
					fd = pfd.getFileDescriptor();
			}else{
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null)
					fd = pfd.getFileDescriptor();
			}
			/*bfo.inSampleSize = 1;
			bfo.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, bfo);
			bfo.inSampleSize = 100;
			bfo.inJustDecodeBounds = false;*/
			bfo.inDither = false;
			bfo.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bm = BitmapFactory.decodeFileDescriptor(fd, null, bfo);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return bm;
	}
	
	public static Bitmap getArtWork(Context context,long songId,long albumId,boolean allowdefault){
		if(albumId < 0){
			if(songId >= 0){
				Bitmap bm = getArtWorkFromFile(context, songId, -1);
				if(bm != null)
					return bm;
			}
			if(allowdefault){
				return getDefaultArtWork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
		if(uri != null){
			InputStream in =null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in,null,new BitmapFactory.Options());
			} catch (FileNotFoundException e) {
				Bitmap bm = getArtWorkFromFile(context, songId, albumId);
				if(bm != null){
					if(bm.getConfig() == null){
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if(bm ==null && allowdefault)
							return getDefaultArtWork(context);
					}
				}else if(allowdefault){
					bm = getDefaultArtWork(context);
				}
				return bm;
			}finally{
				try {
					if(in != null)
						in.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return null;
		
	}
	
}
