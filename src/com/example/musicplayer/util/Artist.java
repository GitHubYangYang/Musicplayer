package com.example.musicplayer.util;

import android.annotation.SuppressLint;
import java.util.List;

public class Artist {
	private String artistName;
	private List<Long> musicIds;
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public List<Long> getMusicIds() {
		return musicIds;
	}
	public void setMusicIds(List<Long> musicIds) {
		this.musicIds = musicIds;
	}
	public Artist(){
		artistName = null;
		musicIds = null;
	}
	public Artist(List<Long> mids){
		artistName = null;
		musicIds = mids;
	}
	
	public void addMusicId(long id){
		musicIds.add(new Long(id));
	}
}
