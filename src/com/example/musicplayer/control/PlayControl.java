package com.example.musicplayer.control;

import java.util.List;
import java.util.concurrent.Semaphore;

import com.example.musicplayer.MusicPlayActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.service.MusicPlayService;
import com.example.musicplayer.util.Music;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewDebug.IntToString;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayControl {
	private TextView musicName;
	private ImageButton play;
	private ImageButton prev;
	private ImageButton next;
	private ProgressBar time;
	// this class need get a activity
	private Activity showActivity;
	private Service playService;
	MusicServiceRecriver receiver;
	final Intent intent;
	// get main activity intent
	private Intent getIntent = null;
	// mark service is start
	private boolean serviceIsStart = false;
	public static final String PLAY_CONTROL_GET = "com.example.musicplayer.control.PLAY_CONTROL_GET";
	public static final String PLAY_CONTROL_SET = "com.example.musicplayer.control.PLAY_CONTROL_SET";

	// binder
	private MusicPlayActivity.ControlHook hook = null;

	// service connection
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	public PlayControl(Context activtiy, MusicPlayActivity.ControlHook myhook) {
		showActivity = (Activity) activtiy;
		intent = new Intent();
		intent.setAction(MusicPlayActivity.PLAY_SERVICE);
		showActivity.startService(intent);
		// showActivity.bindService(intent, conn, Service.BIND_AUTO_CREATE);
		getControl();
		initBoardRceiver();
		hook = myhook;
		Log.i("mylog", "play control is initial over!!!");
	}

	private void getControl() {
		prev = (ImageButton) showActivity.findViewById(R.id.button_play_prev);
		play = (ImageButton) showActivity.findViewById(R.id.button_play_play);
		next = (ImageButton) showActivity.findViewById(R.id.button_play_next);
		time = (ProgressBar) showActivity.findViewById(R.id.music_progress);
		musicName = (TextView) showActivity.findViewById(R.id.play_Music_Name);
	}

	public void initBoardRceiver() {
		receiver = new MusicServiceRecriver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PLAY_CONTROL_GET);
		showActivity.registerReceiver(receiver, filter);
	}

	public void setGetIntent(Intent i) {
		getIntent = i;
	}

	public void playMusic() {
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		// send play broadcast
		Intent i = new Intent(PLAY_CONTROL_SET);
		i.putExtra("control", 1); // 1 means now play music
		showActivity.sendBroadcast(i);
	}

	public void pauseMusic() {
		play.setImageResource(R.drawable.img_button_notification_play_play);
		// send pause broadcast
		Intent m = new Intent(PLAY_CONTROL_SET);
		m.putExtra("control", 2); // 2 means now stop play
		showActivity.sendBroadcast(m);
	}

	public void nextMusic() {
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		// send next broadcast
		Intent j = new Intent(PLAY_CONTROL_SET);
		j.putExtra("control", 3); // 3 means now play next music
		showActivity.sendBroadcast(j);
	}

	public void previouMusic() {
		play.setImageResource(R.drawable.img_button_notification_play_pause);
		// send previous broadcast
		Intent k = new Intent(PLAY_CONTROL_SET);
		k.putExtra("control", 4); // 4 means now play previous music
		showActivity.sendBroadcast(k);
	}

	public void sendMusicToService(Bundle b) {
		Log.i("mylog", "serviceIsStart:" + serviceIsStart);
		if (serviceIsStart) {
			Intent l = new Intent(PLAY_CONTROL_SET);
			l.putExtra("control", 5); // 5 means send music to service
			l.putExtras(b);
			showActivity.sendBroadcast(l);
			Log.i("mylog", "send music to service :play control");
		}

	}

	public void playAppointMusic(int item) {
		if (serviceIsStart) {
			play.setImageResource(R.drawable.img_button_notification_play_pause);
			Intent m = new Intent(PLAY_CONTROL_SET);
			m.putExtra("control", 6); // 6 means play appoint music
			m.putExtra("clickItem", item);
			showActivity.sendBroadcast(m);
			Log.i("mylog", "play apoint music : paly control");
		}
	}

	public void destoryService() {
		if(receiver != null){
			showActivity.unregisterReceiver(receiver);
			receiver = null;
		}		
		showActivity.stopService(intent);
	}

	public class MusicServiceRecriver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int control = intent.getIntExtra("control", -1);
			switch (control) {
			case 1: // get play rebroadcast
				String name = intent.getStringExtra("playName");
				if(name != null)
				    musicName.setText(name);
				break;

			case 2: // get pause rebroadcast
				// musicName.setText("stop");
				break;
			case 3:// get next rebroadcast
				String name1 = intent.getStringExtra("playName");
				if(name1 != null)
				    musicName.setText(name1);
				break;
			case 4:// get prev rebroadcast
				String name2 = intent.getStringExtra("playName");
				if(name2 != null)
				    musicName.setText(name2);
				break;
			case 6: // get play appoint music rebroadcast
				String name3 = intent.getStringExtra("playName");
				if(name3 != null)
				    musicName.setText(name3);
				break;
			case 7: // 7 means service is start
				serviceIsStart = true;
				int sid = intent.getIntExtra("sessionId", 0);
				if (sid != 0)
					Log.i("mylog", "sessionId:" + sid);
					 hook.setVisualizer(sid);
					if (getIntent != null) {// first start service use
						sendMusicToService(getIntent.getExtras());
						int it = getIntent.getIntExtra("clickItem", -1);
						if (it != -1)
							playAppointMusic(it);
					}

				break;
			case 8: // 8 play is completion and play next
				if (hook != null)
					hook.addCourentMusic();
				musicName.setText(intent.getStringExtra("playName"));
				break;
			}
		}

	}
}
