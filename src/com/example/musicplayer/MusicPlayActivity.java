package com.example.musicplayer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import com.example.musicplayer.adapter.MainViewPagerAdapter;
import com.example.musicplayer.adapter.PlayListAdapter;
import com.example.musicplayer.control.PlayBindeControl;
import com.example.musicplayer.control.PlayControl;
import com.example.musicplayer.util.MediaUtil;
import com.example.musicplayer.util.Music;
import com.example.musicplayer.view.VisualizerView;
import com.example.musicplayer.view.VisualizerViewTwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MusicPlayActivity extends Activity implements OnClickListener{
	private TextView musicName;
	private ImageButton play;
	private ImageButton prev;
	private ImageButton next;
	private ImageButton backMainActivity;
	private ListView  musicList;
	//music progress parameter
	private TextView playingTimeText;
	private TextView allTimeText;
	private ProgressBar time;
	private Timer progressTimer;
	private Handler timeHandler;
	private int timeCount;
	private boolean stopgo = false;
	private boolean beginagine = false;
	
	//view pager parameter
	private ViewPager viewPager;
	private List<View> pagerViews;
	private ImageView pointOne;
	private ImageView pointTwo;
	
	//play list pager parameter
	private PlayListAdapter adapter;
	private int courentPlayMusic;
	private boolean havePoint = false;
	private Timer freshListTimer;
	//animation pager parameter
	private Visualizer myVisualizer;
	private VisualizerView myVisualizerView;
	private static final float VISUALIZER_HEIGHT_DIP = 160f;
	private LinearLayout pagerTwoLayout;
	//music seek bar
	
	private boolean isPlay=false;
	private PlayControl playControl;             //we can choice using broadcast to control the music play   or binding a service 
	//private PlayBindeControl playControl;
	public static final String 	PLAY_SERVICE = "com.example.musicplayer.service.PLAY_SERVICE";
	//final Intent intent = new Intent();
	//get the main activity send intent and control signal
	private Intent getMainintent ;
	private int whichView = 2;//sign music from which child view pager
	//mark have or not send music to service  and fill music to list view
	public static boolean isSendAllMusic = false;
	
	//music play list
	List<Music> ml;
	
	//exit broadcastreceiver
	private PlayActivityBoradReceiver receiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initView();
        initViewPager();
        initChildViewAtViewPager();
        registExitBoradReceiver();
        initPrograssBar();
    }
    @Override
    protected void onStart() {
    	super.onStart();
    	System.out.println("start musicpalyactivity");
//    	getMainActivitySendIntent();
    	
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	System.out.println("resume musicpalyactivity");
    	getMainActivitySendIntent();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	System.out.println("pause musicpalyactivity");
    }
    @Override
    protected void onRestart() {
    	super.onRestart();
    	System.out.println("restart musicpalyactivity");
    }
    @Override
    protected void onStop() {
    	super.onStop();
    	System.out.println("stop musicpalyactivity");
    }
    @SuppressLint("NewApi")
	@Override
    protected void onDestroy() {
    	playControl.destoryService();
    	if(progressTimer != null)
    		progressTimer.cancel();
    	if(myVisualizer != null)
    		myVisualizer.release();
    	if(receiver != null){
    		unregisterReceiver(receiver);
    		receiver = null;
    	}
    	super.onDestroy();
    	System.out.println("destory musicpalyactivity");
    }
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	setIntent(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == event.KEYCODE_BACK){
            return false;   //forbidden back key  		
    	}else{
    		return super.onKeyDown(keyCode, event);
    	}
    }
    private void initView(){
    	playControl = new PlayControl(this,new ControlHook());
        //musicList = (ListView)findViewById(R.id.music_List);
        prev = (ImageButton)findViewById(R.id.button_play_prev);
        play = (ImageButton)findViewById(R.id.button_play_play);
        next = (ImageButton)findViewById(R.id.button_play_next);
        backMainActivity = (ImageButton)findViewById(R.id.back_To_ListActivity);
        time = (ProgressBar)findViewById(R.id.music_progress);
        musicName = (TextView)findViewById(R.id.play_Music_Name);
		Animation an = AnimationUtils.loadAnimation(this, R.anim.text_movie);
		LinearInterpolator lin = new LinearInterpolator();
		an.setInterpolator(lin);
		if(an != null){
			musicName.clearAnimation();
			musicName.startAnimation(an);
		}
        playingTimeText = (TextView)findViewById(R.id.textview_playingtime_playactivity);
        allTimeText = (TextView)findViewById(R.id.textview_alltime_playactivity);
        //playControl = new PlayBindeControl(this);
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this); 
        backMainActivity.setOnClickListener(this);
        System.out.println("Create musicpalyactivity!!!!!!");
    }
    //init view pager
    private void initViewPager(){
    	viewPager = (ViewPager)findViewById(R.id.viewpager_musicplay);
    	pointOne = (ImageView)findViewById(R.id.pointimage_one);
    	pointTwo = (ImageView)findViewById(R.id.pointimage_two);
    	pagerViews = new ArrayList<View>();
    	LayoutInflater infalter = getLayoutInflater();
    	//load music list pager
    	pagerViews.add(infalter.inflate(R.layout.tabcontent_one_viewpager_musicplay, null));
    	//load Animation pager
    	pagerViews.add(infalter.inflate(R.layout.tabcontent_two_viewpager_musicplay, null));
    	viewPager.setAdapter(new MainViewPagerAdapter(pagerViews));
    	viewPager.setCurrentItem(0);
    	viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
					if(arg0 == 0){
						pointOne.setImageResource(R.drawable.point_select);
						pointTwo.setImageResource(R.drawable.point_release);
					}else{
						pointOne.setImageResource(R.drawable.point_release);
						pointTwo.setImageResource(R.drawable.point_select);
					}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {		
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {			
			}
		});
    }
    private void initChildViewAtViewPager(){
    	//get child view widget(music list view)
    	musicList = (ListView)pagerViews.get(0).findViewById(R.id.music_List_viewpager_musicplay);
    	musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(courentPlayMusic == arg2){
					if(!havePoint){
					    playControl.playMusic();
					    adapter.showStopImage();
					    adapter.clickPointItem(arg2);
					    havePoint = true;
					    isPlay = true;
					    resetPrograssBarValue(false,false);
				   }else{
					    playControl.pauseMusic();
					    adapter.showPalyImage();
					    havePoint = false;
					    isPlay = false;
					    resetPrograssBarValue(true,false);
				   }
				    
				}else{
					 playControl.playAppointMusic(arg2);
  				     courentPlayMusic = arg2;
					 adapter.showStopImage();
					 adapter.clickPointItem(arg2);
					 setMusicAllTime(courentPlayMusic);
					 resetPrograssBarValue(false,true);
					 havePoint = true;
					 isPlay = true;
				}
				adapter.notifyDataSetChanged();
			}
		});
    	pagerTwoLayout = (LinearLayout)pagerViews.get(1).findViewById(R.id.linearlayout_pagertwo_musicplay);
    }
    //get intent from main activity   and do something
    private void getMainActivitySendIntent(){
    	getMainintent = getIntent();
    	whichView = getMainintent.getIntExtra("whichView", -1);
    	switch (whichView) {
		case 0:
			
			break;
		case 1:
			
			break;
		case 2:   //the intent come to music list view pager
			if(isSendAllMusic == false){
				Bundle bd = getMainintent.getExtras();
				ml = (List<Music>)bd.getSerializable("music");
				addMusicList(ml);
				playControl.setGetIntent(getMainintent);
				playControl.sendMusicToService(bd);
				isSendAllMusic = true;
			}
			int item = getMainintent.getIntExtra("clickItem", 0);
			playControl.playAppointMusic(item);
			havePoint = true;
			courentPlayMusic = item;
			adapter.clickPointItem(item);
			adapter.showStopImage();
			adapter.notifyDataSetChanged();
			setMusicAllTime(courentPlayMusic);
			resetPrograssBarValue(false,true);
			isPlay = true;
//			getMainintent = null;
			break;
		case 3:
			
			break;
		case 4:   //intent from play navigation
			
			break;
		case 5:  //intent from ChildMusicListActivity
			Bundle b = getMainintent.getExtras();
			ml = (List<Music>)b.getSerializable("music");
			addMusicList(ml);
			playControl.setGetIntent(getMainintent);
			playControl.sendMusicToService(b);
			isSendAllMusic = false;
			MainActivity.isSendAllMusic = false;
			int items = getMainintent.getIntExtra("clickItem", 0);
			playControl.playAppointMusic(items);
			havePoint = true;
			courentPlayMusic = items;
			adapter.clickPointItem(items);
			adapter.showStopImage();
			adapter.notifyDataSetChanged();
			setMusicAllTime(courentPlayMusic);
			resetPrograssBarValue(false,true);
			isPlay = true;
			break;
		}
    }
    
	public void addMusicList(List<Music> mymusic){
		//add music to play list
		/*musicMap.clear();
		for(int i = 1;i<= mymusic.size();i++){
			Map<String, Object>  mm = new HashMap<String, Object>();
			mm.put("title",i+" .");
			mm.put("artist", mymusic.get(i-1).getArtist());
			mm.put("playName", mymusic.get(i-1).getDiaplayName());
			musicMap.add(mm);
		}
        SimpleAdapter adapter = new SimpleAdapter(this,musicMap,R.layout.listview_play_list,new String[]{"title",
        		"playName","artist"},new int[]{R.id.listview_textview_title,R.id.listview_textview_palyname,R.id.listview_textview_artist});
        		*/
		adapter = new PlayListAdapter(this, mymusic);
        musicList.setAdapter(adapter);
        freshListTimer = new Timer();
        freshListTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				timeHandler.sendEmptyMessage(0x234);
			}
		}, 3000);
    }
	//music spectrum
	@SuppressLint("NewApi")
	public void setVisualizerView(int audiosession){
		if(audiosession != 0){
			Log.i("mylog", "in ****"+audiosession);
		    myVisualizerView = new VisualizerView(this);
		    myVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int)(VISUALIZER_HEIGHT_DIP*getResources().getDisplayMetrics().density)));
		    pagerTwoLayout.addView(myVisualizerView);
		    Log.i("mylog", "add view");
		    myVisualizer = new Visualizer(audiosession);
		    Log.i("mylog", "init visualizer:" + myVisualizer.toString());
//		    myVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		    myVisualizer.setCaptureSize(256);
		    myVisualizer.setDataCaptureListener(new OnDataCaptureListener() {
			
		    	@Override
		    	public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
		    			int samplingRate) {
		    		myVisualizerView.updateVisualizer(waveform);
		    	}
			
		    	@Override
		    	public void onFftDataCapture(Visualizer visualizer, byte[] fft,
		    			int samplingRate) {
		    		myVisualizerView.updateVisualizer(fft);
		    	}
		    }, Visualizer.getMaxCaptureRate()/2, true, false);
		    myVisualizer.setEnabled(true);
		    setVolumeControlStream(AudioManager.STREAM_SYSTEM);
		}

	}
	
	private void registExitBoradReceiver(){
		receiver = new PlayActivityBoradReceiver();
		IntentFilter filter = new IntentFilter();
    	filter.addAction(MainActivity.EXIT);
    	registerReceiver(receiver, filter);
	}
	
	private void addCourentPlayMusic(){
		courentPlayMusic++;
		if(courentPlayMusic >= ml.size()){
			courentPlayMusic = 0;
		}
		adapter.clickPointItem(courentPlayMusic);
		adapter.showStopImage();
		adapter.notifyDataSetChanged();
		isPlay = true;
		havePoint = true;
		setMusicAllTime(courentPlayMusic);
		resetPrograssBarValue(false, true);
		Log.i("mylog", "go to next music");
		Intent i = new Intent("com.example.musicplayer.MUSIC_CHANGE_NEXT");
		i.putExtra("musicname", ml.get(courentPlayMusic).getDiaplayName());
		i.putExtra("artist", ml.get(courentPlayMusic).getArtist());
		sendBroadcast(i);
	}
//set prograssbar
	private void setMusicAllTime(int courentplay){
		String alltime = MediaUtil.formatTime(ml.get(courentplay).getDuration());
		time.setMax(ml.get(courentPlayMusic).getDuration() / 1000);
		allTimeText.setText(alltime);
	}
	
	private void setPrograss(int count){
		String s = count / 60 + "";
		String m = count % 60 + "";
		if(s.length() < 2)
			s = "0" + s;
		if(m.length() < 2)
			m = "0" + m;
		playingTimeText.setText(s + ":" + m);
		time.setProgress(count);
	}
	
	private void initPrograssBar(){
		time.setProgress(0);
		stopgo = true;
		beginagine = true;
		timeHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0x123){
					setPrograssBar();
				}else if(msg.what == 0x234){  //fresh music list
					adapter.notifyDataSetChanged();
					if(freshListTimer != null){
						freshListTimer.cancel();
						freshListTimer = null;
					}
					
				}
			}
		};
		progressTimer = new Timer();
		progressTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				timeHandler.sendEmptyMessage(0x123 );
			}
		}, 0, 1000);
	}
	
	private void setPrograssBar(){
		if(!stopgo){
			if(beginagine){
				timeCount = 0;
				stopgo = false;
				beginagine = false;
				setPrograss(timeCount);
			}else{
				timeCount++;
				setPrograss(timeCount);
			}
		}
	}
	
	private void resetPrograssBarValue(boolean stop,boolean agine){
		stopgo = stop;
		beginagine = agine;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		//music operation
		switch (v.getId()) {
		case R.id.button_play_play:
			if(isPlay == false){
				isPlay = true;
				havePoint = true;
				playControl.playMusic();
				adapter.clickPointItem(courentPlayMusic);
				adapter.showStopImage();
				adapter.notifyDataSetChanged();
				resetPrograssBarValue(false,false);
			}
			else{
				isPlay = false;
				havePoint = false;
				playControl.pauseMusic();
				adapter.clickPointItem(courentPlayMusic);
				adapter.showPalyImage();
				adapter.notifyDataSetChanged();
				resetPrograssBarValue(true,false);
			}
			
			break;

		case R.id.button_play_next:
			playControl.nextMusic();
			if(++courentPlayMusic >= ml.size())
				courentPlayMusic = 0;
			adapter.clickPointItem(courentPlayMusic);
			adapter.showStopImage();
			adapter.notifyDataSetChanged();
			setMusicAllTime(courentPlayMusic);
			resetPrograssBarValue(false,true);
			isPlay = true;
			havePoint = true;
			break;
		case R.id.button_play_prev:
			playControl.previouMusic();
			if(courentPlayMusic-- <= 0)
				courentPlayMusic = ml.size() - 1;
			adapter.clickPointItem(courentPlayMusic);
			adapter.showStopImage();
			adapter.notifyDataSetChanged();
			setMusicAllTime(courentPlayMusic);
			resetPrograssBarValue(false,true);
			isPlay = true;
			havePoint = true;
			break;
		case R.id.back_To_ListActivity:
			if(ml != null){
				Intent i =new Intent(MusicPlayActivity.this, MainActivity.class);
				i.putExtra("whichActivity", 2);
				i.putExtra("playname", ml.get(courentPlayMusic).getDiaplayName());
				i.putExtra("artist", ml.get(courentPlayMusic).getArtist());
				i.putExtra("isplay", isPlay);
				MusicPlayActivity.this.startActivity(i);
				MusicPlayActivity.this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
			break;
		}
		
	}
	
	public class PlayActivityBoradReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
		
	}
	
	public class ControlHook{
		public void setVisualizer(int id){
			setVisualizerView(id);
		}
		public void addCourentMusic(){
			addCourentPlayMusic();
		}
	}

}
