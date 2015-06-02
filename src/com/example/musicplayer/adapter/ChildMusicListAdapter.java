package com.example.musicplayer.adapter;

import java.util.List;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter.ListItemView;
import com.example.musicplayer.util.MediaUtil;
import com.example.musicplayer.util.Music;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildMusicListAdapter extends BaseAdapter{

	private Context context;
	private LayoutInflater inflater;
	private List<Music> musicList;
	private Bitmap[] maps;

	public Handler handr = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0x123){
				Bundle bd = msg.getData();
				int p = bd.getInt("position",-1);
				if(p != -1){
					maps[p] = bd.getParcelable("map");
				}
			}
		};
	};

	public class AlbumThread extends Thread{
		private Context context;
		private long sid,aid;
		private int position;
		public AlbumThread(Context con,long songid,long albumid,int position){
			context = con;
			sid = songid;
			aid = albumid;
			this.position = position;
		}
		@Override
		public void run() {
			Bitmap bp = MediaUtil.getArtWork(context, sid, aid, true);
			Message m = new Message();
			m.what = 0x123;
			Bundle b = new Bundle();
			b.putParcelable("map", bp);
			b.putInt("position", position);
			m.setData(b);
			handr.sendMessageAtFrontOfQueue(m);
		}
	}
	
	public final class MyListItem {
		public ImageView album;
		public TextView playname;
		public TextView artist;
	}

	public ChildMusicListAdapter(Context context, List<Music> music) {
		this.context = context;
		this.musicList = music;
		inflater = LayoutInflater.from(context);
		maps = new Bitmap[music.size()];
	}

	@Override
	public int getCount() {

		return musicList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int poisition, View contextView, ViewGroup viewGroup) {
		MyListItem listItem = null;
		if (contextView == null) { // create list item
			listItem = new MyListItem();
			contextView = inflater.inflate(R.layout.listview_childmusic_list, null);
			listItem.album =  (ImageView) contextView
					.findViewById(R.id.listview_album_childactivity);
			listItem.playname = (TextView) contextView
					.findViewById(R.id.listview_palyname_childactivity);
			listItem.artist = (TextView) contextView
					.findViewById(R.id.listview_artist_childactivity);
//			maps[poisition] = MediaUtil.getArtWork(context, musicList.get(poisition).getId(),
//					musicList.get(poisition).getAlbimId(), true);
			AlbumThread at = new AlbumThread(context, musicList.get(poisition).getId(),
					musicList.get(poisition).getAlbimId(), poisition);
			at.start();
			contextView.setTag(listItem);
		} else {
			listItem = (MyListItem) contextView.getTag();
		}
		// set values
		Bitmap bm = maps[poisition];
		if(bm != null)
			listItem.album.setImageBitmap(bm);
		else
			listItem.album.setImageResource(R.drawable.img_album_default);
		listItem.playname.setText(musicList.get(poisition).getDiaplayName());
		listItem.artist.setText(musicList.get(poisition).getArtist());
		
		return contextView;
	}

}
