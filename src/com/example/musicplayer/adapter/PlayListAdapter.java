package com.example.musicplayer.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MusicListAdapter.ListItemView;
import com.example.musicplayer.util.MediaUtil;
import com.example.musicplayer.util.Music;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Binder;
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

public class PlayListAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private List<Music> musicList;
	private Bitmap[] maps;
	private boolean[] hasChecked;
	private boolean isPlay;
	public Handler hand = new Handler(){
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
			hand.sendMessageAtFrontOfQueue(m);
		}
	}
	public final class ListItemView {
		public ImageView image;
		public ImageView album;
		public TextView playname;
		public TextView artist;
	}

	public PlayListAdapter(Context context, List<Music> music) {
		this.context = context;
		this.musicList = music;
		inflater = LayoutInflater.from(context);
		maps = new Bitmap[music.size()];
		hasChecked = new boolean[music.size()];
		isPlay = true;
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
	
	public void clickPointItem(int key){
		for(int i = 0;i < hasChecked.length ;i++){
			hasChecked[i] = false;
		}
		hasChecked[key] = true;
	}
	
	public void showPalyImage(){
		isPlay = true;
	}
	
	public void showStopImage(){
		isPlay = false;
	}
	
	@Override
	public View getView(int poisition, View contextView, ViewGroup viewGroup) {
		final int selectID = poisition;
		ListItemView listItem = null;
		if (contextView == null) { // create lsit item
			listItem = new ListItemView();
			contextView = inflater.inflate(R.layout.listview_play_album, null);
			listItem.image = (ImageView) contextView
					.findViewById(R.id.listview_album_play);
			listItem.album =  (ImageView) contextView
					.findViewById(R.id.listview_album);
			listItem.playname = (TextView) contextView
					.findViewById(R.id.listview_album_palyname);
			listItem.artist = (TextView) contextView
					.findViewById(R.id.listview_album_artist);
//			maps[poisition] = MediaUtil.getArtWork(context, musicList.get(poisition).getId(),
//					musicList.get(poisition).getAlbimId(), true);
			AlbumThread t = new AlbumThread(context, musicList.get(poisition).getId(), 
					musicList.get(poisition).getAlbimId(), poisition);
			t.start();
			contextView.setTag(listItem);
		} else {
			listItem = (ListItemView) contextView.getTag();
		}
		// set values
		Bitmap bm = maps[poisition];
		if(bm != null)
			listItem.album.setImageBitmap(bm);
		else
			listItem.album.setImageResource(R.drawable.img_album_default);
		if(hasChecked[poisition]){
			listItem.image.setVisibility(View.VISIBLE);
			if(isPlay)
				listItem.image.setImageResource(R.drawable.img_playlistshow_play);
			else
				listItem.image.setImageResource(R.drawable.img_palylistshow_pause);
		}else{
			listItem.image.setVisibility(View.GONE);
		}
		listItem.playname.setText(musicList.get(poisition).getDiaplayName());
		listItem.artist.setText(musicList.get(poisition).getArtist());
		
		return contextView;
	}

}
