package com.example.musicplayer.control;

import java.io.File;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.musicplayer.MusicPlayActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.control.PlayControl.MusicServiceRecriver;
import com.example.musicplayer.service.MusicPlayService;
import com.example.musicplayer.service.MusicPlayService.MyBinder;

public class PlayBindeControl {
	private TextView musicName;
	private ImageButton play;
	private ImageButton prev;
	private ImageButton next;
	private ProgressBar time;
	//this class need get a activity
	private Activity showActivity;
	MusicPlayService.MyBinder binder;
	final Intent intent;
	public static final String PLAY_CONTROL_GET = "com.example.musicplayer.control.PLAY_CONTROL_GET";
	public static final String PLAY_CONTROL_SET = "com.example.musicplayer.control.PLAY_CONTROL_SET";
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (MusicPlayService.MyBinder)service;
		}
	};
	public PlayBindeControl(Context activtiy){
		showActivity = (Activity) activtiy;
		intent = new Intent();
		intent.setAction(MusicPlayActivity.PLAY_SERVICE);
		showActivity.bindService(intent, conn, Service.BIND_AUTO_CREATE);
		getControl();
	}
    private void getControl(){      
        prev = (ImageButton)showActivity.findViewById(R.id.button_play_prev);
        play = (ImageButton)showActivity.findViewById(R.id.button_play_play);
        next = (ImageButton)showActivity.findViewById(R.id.button_play_next);
        time = (ProgressBar)showActivity.findViewById(R.id.music_progress);
        musicName = (TextView)showActivity.findViewById(R.id.play_Music_Name);
    }

	public void playMusic(){
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		musicName.setText("play");
		binder.playMusic();
	}
	public void pauseMusic(){
		play.setImageResource(R.drawable.img_button_notification_play_play);
		musicName.setText("pause");
		binder.pauseMusic();
	}
	public void nextMusic(){
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		musicName.setText("next");
		binder.playNextMusic();
	}
	public void previouMusic(){
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		musicName.setText("previou");
		binder.playPreviouMusic();
	}
}
