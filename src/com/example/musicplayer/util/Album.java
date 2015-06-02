package com.example.musicplayer.util;

import java.util.List;

public class Album {
	private long id;
	private String  name;
	private List<Long> songId;
	public Album(){
		id = 0;
		name = null;
		songId = null;
	}
	public Album(long id,String name,List<Long> songId){
		this.id = id;
		this.name = name;
		this.songId = songId;
	}
	public void addSongId(long id){
		songId.add(new Long(id));
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Long> getSongId() {
		return songId;
	}
	public void setSongId(List<Long> songId) {
		this.songId = songId;
	}
}
