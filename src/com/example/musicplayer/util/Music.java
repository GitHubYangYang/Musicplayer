package com.example.musicplayer.util;

import java.io.Serializable;


public class Music implements Serializable{
	private long id;
	private String diaplayName;
	private int duration;  //music time
	private long size;    //music size
	private String artist;   //singer
	private String url;   //music file path
	private String album;
	private long albimId;
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public long getAlbimId() {
		return albimId;
	}
	public void setAlbimId(long albimId) {
		this.albimId = albimId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDiaplayName() {
		return diaplayName;
	}
	public void setDiaplayName(String diaplayName) {
		this.diaplayName = diaplayName;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duratio) {
		this.duration = duratio;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
}
