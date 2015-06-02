package com.example.musicplayer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.musicplayer.control.PlayControl;
import com.example.musicplayer.util.Music;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayService extends Service{
	MusicOrderReceiver receiver;
	MediaPlayer mp;
	List<String> list = new ArrayList<String>();
	List<String> musicName = new ArrayList<String>();
	private int playConfig = 1;  // 1 :first play  2:play    3:pause
	int courent = 0;      //record where we play
	private boolean haveMusic =false;  // mark music have or not
	private MyBinder binder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		//create broadcast receiver
		receiver = new MusicOrderReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayControl.PLAY_CONTROL_SET);
		registerReceiver(receiver, filter);
		mp = new MediaPlayer();
		haveMusic = haveMusics();
		mp.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				courent++;
				if(courent >= list.size())
					courent = 0;
				addSourceAndPlay(list.get(courent));
				Intent send = new Intent(PlayControl.PLAY_CONTROL_GET);
			    send.putExtra("control", 8);  //8 is control message ,means this music is play complention paly next
			    send.putExtra("playName", musicName.get(courent));
			    sendBroadcast(send);
			}
		});
		//send broadcast to tell play control services is start
	    Intent send = new Intent(PlayControl.PLAY_CONTROL_GET);
	    send.putExtra("sessionId", mp.getAudioSessionId());
	    send.putExtra("control", 7);  //7 is control message ,means services is start
	    sendBroadcast(send);
	    System.out.println("music play Service is start!!!!!!!");
	    Log.i("mylog", "music play Service is start!!!!!!!");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mp != null && playConfig ==2)
			mp.stop();
		mp.release();
		mp = null;
		if(receiver != null)
			unregisterReceiver(receiver);
		receiver = null;
		list = null;
		musicName =null;
		binder =null;
		System.out.println("music play Service is destory!!!!!!!");
	}
	public class MyBinder extends Binder{
		public void playMusic(){
			if(haveMusic)
		        play();
		}
		public void pauseMusic(){
			if(haveMusic)
			    pause();
		}
		public void playNextMusic(){
			if(haveMusic)
			    next();
		}
		public void playPreviouMusic(){
			if(haveMusic)
			    previou();
		}
		
		public int getMediaPlaySessionId(){
			return getSessionId();
		}
	}
	public class MusicOrderReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int control = intent.getIntExtra("control", -1);
			//if(haveMusic){
			    switch (control) {
			    case 1://point play button
				//now is not play
				    play();
				    break;
			    case 2://point pause button
				    pause();
				    break;
			    case 3://point next button
				    next();
				    break;				
			    case 4://point prev 
				    previou();
				    break;
			    case 5:  //fill music list
			    	Bundle b = intent.getExtras();
			    	addOrReplaceMusic((List<Music>)b.getSerializable("music"));
			    	break;
			    case 6: // play appoint music
			    	int item = intent.getIntExtra("clickItem", -1);
			    	if(item != -1 && item < list.size())
			    		setCorentAndPaly(item);
			    	break;
			    }
			    Intent sendIntent = new Intent(PlayControl.PLAY_CONTROL_GET);
			    sendIntent.putExtra("control", control);
			    sendIntent.putExtra("playName", musicName.get(courent));
			    sendBroadcast(sendIntent);
			//}
			
		}
		
	}
	//judge music have or do not have
	private boolean  haveMusics(){
		if(list == null || list.size() == 0)
			return false;
		else
			return true;
	} 
	// the following methods are basic achieve
	public void play(){
		try {
			if(mp != null && playConfig == 1){
				addSourceAndPlay(list.get(courent));
		    }else if(mp != null && playConfig == 3){
		    	playConfig = 2;
		    	mp.start();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//play appoint music
	public void setCorentAndPaly(int cour){
		Log.i("mylog", "play apoint music : service");
		courent = cour;
		try {
			if(mp != null && playConfig == 1){
				addSourceAndPlay(list.get(courent));
		    }else if(mp != null && playConfig == 2){
		    	mp.pause();
		    	addSourceAndPlay(list.get(courent));
		    }else if(mp != null && playConfig == 3){
		    	addSourceAndPlay(list.get(courent)); 
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void pause(){
		try {
			if(mp != null && playConfig == 2){
				mp.pause();
				playConfig = 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void next(){
		try {
			if(mp != null && playConfig == 2)
				mp.stop();
			++courent;
			if(courent >= list.size()){
				courent = 0;
			}
			addSourceAndPlay(list.get(courent));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void previou(){
		try {
			if(mp != null && playConfig == 2)
				mp.stop();
			--courent;
			if(courent < 0){
				courent = list.size() - 1;
			}
			addSourceAndPlay(list.get(courent));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addOrReplaceMusic(List<Music> m){
		if(playConfig == 2)  //if now is palying
			mp.stop();
		list.clear();
		musicName.clear();
		for(int i = 0 ; i < m.size() ; i++){
			list.add(m.get(i).getUrl());
			musicName.add(m.get(i).getDiaplayName());
		}
		courent = 0;
		playConfig = 1;
		Log.i("mylog", "add music to list : service");
	}
	private void addSourceAndPlay(String musicPath){
		try {
		    mp.reset();
	        mp.setDataSource(musicPath);
	        mp.prepare();
	        playConfig = 2;
	        mp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@SuppressLint("NewApi")
	public int getSessionId(){
		if(mp != null)
			return mp.getAudioSessionId();
		return 0;
	}

}
