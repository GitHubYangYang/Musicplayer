package com.example.musicplayer;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.ls.LSInput;

import com.example.musicplayer.adapter.ChildMusicListAdapter;
import com.example.musicplayer.util.Music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ChildMusicListActivity extends Activity{

	private List<Music> musics;
	private ImageButton back;
	private TextView titleName;
	private ListView musicList;
	private ChildMusicListAdapter adapter;
	private Timer refreshTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child_music_list);
		init();
		getIntentAndOperator();
		setListener();
		refreshListView();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == event.KEYCODE_BACK){
		    return false;
		}
		else
		    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		Log.i("mylog", "chiled activtiy destory");
		super.onDestroy();
	}
	private void init(){
		back = (ImageButton)findViewById(R.id.imagebutton_back_childactivity);
		titleName = (TextView)findViewById(R.id.textview_titlename_childactivity);
		musicList = (ListView)findViewById(R.id.listview_musiclsit_childactivity);
	}
	
	private void getIntentAndOperator(){
		Intent intent = getIntent();
		String titlename = intent.getStringExtra("titleName");
		titleName.setText(titlename);
		Bundle bd = intent.getExtras();
		musics = (List<Music>)bd.getSerializable("music");
		adapter = new ChildMusicListAdapter(this, musics);
		musicList.setAdapter(adapter);
		
	}
	
	private void refreshListView(){
		final Handler hand = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0x123){
					adapter.notifyDataSetChanged();
					if(refreshTimer != null){
						refreshTimer.cancel();
						refreshTimer = null;
					}
				}
					
			}
		};
		refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				hand.sendEmptyMessage(0x123);
			}
		}, 1000);
		
	}
	
	private void setListener(){
		back.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChildMusicListActivity.this, MainActivity.class);
				ChildMusicListActivity.this.startActivity(intent);
				ChildMusicListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				ChildMusicListActivity.this.finish();
			}
		});
		musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ChildMusicListActivity.this, MusicPlayActivity.class);
				intent.putExtra("whichView", 5); // 5 is a signal ,means the intent from child activity
				intent.putExtra("clickItem", arg2);
				Bundle bd = new Bundle();
				bd.putSerializable("music", (Serializable)musics);
				intent.putExtras(bd);
				ChildMusicListActivity.this.startActivity(intent);
				ChildMusicListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				ChildMusicListActivity.this.finish();
			}
		});
	}
}
