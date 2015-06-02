package com.example.musicplayer.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.w3c.dom.ls.LSInput;

import com.example.musicplayer.LoginAndRegistActivity;
import com.example.musicplayer.R;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> musicList;
	private boolean[] haschecked;
	private boolean isShowCheckImage;

	public final class ListItemView {
		public ImageView image;
		public TextView number;
		public TextView playname;
		public TextView artist;
	}

	public MusicListAdapter(Context context, List<Map<String, Object>> music) {
		this.context = context;
		this.musicList = music;
		inflater = LayoutInflater.from(context);
		haschecked = new boolean[music.size()];
		isShowCheckImage = false;
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

	private void haschecked(int key) {
		haschecked[key] = !haschecked[key];
	}

	public void enableCheckImage() {
		isShowCheckImage = true;
	}
	
	public void disableCheckImage(){
		isShowCheckImage =false;
		cancleAllImage();
	}

	public void checkImage(int k){
		haschecked(k);
	}
	
	public void checkAllImage(){
		for(int i = 0;i < haschecked.length ;i++)
			haschecked[i] = true;
	}
	
	public void cancleAllImage(){
		for(int i = 0;i < haschecked.length ;i++)
			haschecked[i] = false;
	}

	@Override
	public View getView(int poisition, View contextView, ViewGroup viewGroup) {
		// System.out.println("***getview:"+poisition);
		final int selectID = poisition;
		ListItemView listItem = null;
		if (contextView == null) { // create lsit item
			listItem = new ListItemView();
			contextView = inflater.inflate(R.layout.listview_play_list, null);
			listItem.image = (ImageView) contextView
					.findViewById(R.id.listview_image);
			listItem.number = (TextView) contextView
					.findViewById(R.id.listview_textview_title);
			listItem.playname = (TextView) contextView
					.findViewById(R.id.listview_textview_palyname);
			listItem.artist = (TextView) contextView
					.findViewById(R.id.listview_textview_artist);
			contextView.setTag(listItem);
		} else {
			listItem = (ListItemView) contextView.getTag();
		}
		// set values
		if (isShowCheckImage) {
			listItem.image.setVisibility(View.VISIBLE);
			if (haschecked[selectID]) {
				listItem.image.setImageResource(R.drawable.img_lsit_checked);
			} else {
				listItem.image.setImageResource(R.drawable.img_lsit_unchecked);
			}
		}else{
			listItem.image.setVisibility(View.GONE);
		}

		listItem.number.setText((String) musicList.get(selectID).get("title"));
		listItem.playname.setText((String) musicList.get(selectID).get(
				"playName"));
		listItem.artist.setText((String) musicList.get(selectID).get("artist"));
		
		return contextView;
	}

}
