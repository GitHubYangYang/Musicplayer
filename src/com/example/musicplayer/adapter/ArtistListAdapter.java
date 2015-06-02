package com.example.musicplayer.adapter;

import java.util.List;

import org.w3c.dom.ls.LSInput;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter.ListItemView;
import com.example.musicplayer.util.Artist;
import com.example.musicplayer.util.MediaUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistListAdapter extends BaseAdapter{
	
	private Context context;
	private List<Artist> artists;
	private LayoutInflater inflater;
	
	public final class ListItem {
		public TextView artistname;
		public TextView musicnumber;
	}
	public ArtistListAdapter(Context context,List<Artist> artists){
		this.context = context;
		this.artists = artists;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return artists.size();
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
		if (convertView == null) { // create lsit item
			listItem = new ListItem();
			convertView = inflater.inflate(R.layout.listview_artist_list, null);
			listItem.artistname = (TextView) convertView
					.findViewById(R.id.listview_artist_artistname);
			listItem.musicnumber = (TextView) convertView
					.findViewById(R.id.listview_artist_musicnumber);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		listItem.artistname.setText(artists.get(position).getArtistName());
		listItem.musicnumber.setText(artists.get(position).getMusicIds().size() + "Ê×¸èÇú");
		
		return convertView;
	}

}
