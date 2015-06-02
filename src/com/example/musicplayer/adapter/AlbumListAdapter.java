package com.example.musicplayer.adapter;

import java.util.List;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.ArtistListAdapter.ListItem;
import com.example.musicplayer.util.Album;
import com.example.musicplayer.util.Artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlbumListAdapter extends BaseAdapter{
	private Context context;
	private List<Album> albums;
	private LayoutInflater inflater;
	
	public final class ListItem {
		public TextView albumname;
		public TextView musicnumber;
	}
	public AlbumListAdapter(Context context,List<Album> albums){
		this.context = context;
		this.albums = albums;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItem listItem = null;
		if (convertView == null) { // create list item
			listItem = new ListItem();
			convertView = inflater.inflate(R.layout.listview_album_list, null);
			listItem.albumname = (TextView) convertView
					.findViewById(R.id.listview_album_albumname);
			listItem.musicnumber = (TextView) convertView
					.findViewById(R.id.listview_album_musicnumber);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		listItem.albumname.setText(albums.get(position).getName());
		listItem.musicnumber.setText(albums.get(position).getSongId().size() + "Ê×¸èÇú");
		
		return convertView;
	}
}
