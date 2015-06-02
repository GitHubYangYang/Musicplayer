package com.example.musicplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.musicplayer.MusicPlayActivity.PlayActivityBoradReceiver;
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.adapter.ArtistListAdapter;
import com.example.musicplayer.adapter.MainViewPagerAdapter;
import com.example.musicplayer.adapter.MusicListAdapter;
import com.example.musicplayer.adapter.MusicListAdapter.ListItemView;
import com.example.musicplayer.listener.MainTabBarClickListener;
import com.example.musicplayer.listener.MainViewPagerChangelistener;
import com.example.musicplayer.util.Album;
import com.example.musicplayer.util.Artist;
import com.example.musicplayer.util.MediaUtil;
import com.example.musicplayer.util.Music;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	// public parameter
	public static final String PLAY_MUSIC = "com.example.musicplayer.MusicPlayActivity.PLAY_MUSIC";
	public static final String EXIT = "com.example.musicpayer.EXIT_ACTION";
	
	private boolean isFirstStart = true;
	//this activity framework parameter
	private List<View> tabListViews;
	private ImageView tabSlippageImage;
	private TextView artist,album,song,aboutMe;
	private int offset = 0;
	private int bmpW;
	private ViewPager mViewPager;
	
	//the artist viewpager parameter
	private ListView artistList;
	private List<Artist> artists;
	
	//the album viewpager parameter
	private ListView albumList;
	private List<Album> albums;
	
	//the music viewpager parameter
	private List<Music> allMusic;
	private ListView songList;
	private List<Map<String,Object>> musicMap;
	//the aboutme viewpager parameter
	private LinearLayout meLayout;
	private Button loginAndRegist;
	private Button myBestLove;
	private Button recentPlay;
	private Button mySongList;
	//music list send infromation
	public static boolean isSendAllMusic = false;
	//popwindow use parameter
	private PopupWindow topWindow,bottomWindow;
	private View topView,bottomView;
	private Button bottomViewPlay,bottomViewDelecte,topViewCancle,topViewSelectAll;
	private TextView topViewInfo;
	private boolean isPopWindw = false;
	private List<Music> popWinSelectMusic;
	private boolean isSelectAll = false;
	
	//refresh music parameter   use broadcast
	private ScanSDReceiver scanReceiver;
	
	//adapter
	private MusicListAdapter adapter;
	
	//play navigation
	private boolean isShowNavigaion = false;
	private LinearLayout manLayout;
	private ImageView naAlbumImage;
	private TextView naPlayName;
	private TextView naArtist;
	private ImageView naPlayInfro;
	private View navigatonView;
	private Animation naAnimation;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initImageViewSlippage();
		initViewPager();
		initChildPager();
//		allMusic = getMusic();
//		allMusic = MediaUtil.getMusic(this);
		GetMusicTask getTask = new GetMusicTask(this);
		getTask.execute();
		setListener();
		initPopupWindow();
		registBroadcastReceiver();
//		addMusicList(allMusic);
//		addArtistlist(allMusic);
//		addAlbumList(allMusic);
		System.out.println("create mainactivity!!");
	}
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("start mainactivity!!");
		getIntenFromOtherActivity();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("restart mainactivity!!");
	}
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("pause mainactivity!!");
	}
	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("stop mainactivity!!");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		delectPlayNavigation();
		System.out.println("destory mainactivity!!");
		System.exit(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == event.KEYCODE_BACK){
			exitApplication();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	
	public void init(){
		song = (TextView)findViewById(R.id.tabBar_song);
		album = (TextView)findViewById(R.id.tabBar_album);
		artist = (TextView)findViewById(R.id.tabBar_artist);
		aboutMe = (TextView)findViewById(R.id.tabBar_my);
		mViewPager = (ViewPager)findViewById(R.id.viewpager_main);
		tabSlippageImage = (ImageView)findViewById(R.id.imageview_tabBar);
		manLayout = (LinearLayout)findViewById(R.id.main_layout);
		musicMap = new ArrayList<Map<String,Object>>();
	}
	private void initViewPager(){
		tabListViews = new ArrayList<View>();
		LayoutInflater myInfalter = getLayoutInflater();
		//add album list view
		tabListViews.add(myInfalter.inflate(R.layout.tabcontent_artist_viewpager_main, null));
		//add artist list view
		tabListViews.add(myInfalter.inflate(R.layout.tabcontent_album_viewpager_main, null));
		//add music list view
		tabListViews.add(myInfalter.inflate(R.layout.tabcontent_music_viewpager_main, null));
		//add user view
		tabListViews.add(myInfalter.inflate(R.layout.tabcontent_aboutme_viewpager_main, null));
		mViewPager.setAdapter(new MainViewPagerAdapter(tabListViews));
		mViewPager.setCurrentItem(0);		
	}
	private void initChildPager(){
		//get artist viewList
		artistList = (ListView)tabListViews.get(0).findViewById(R.id.mainviewpage_artist_list);
		//get album viewList
		albumList = (ListView)tabListViews.get(1).findViewById(R.id.mainviewpage_album_list);
		//get child music viewList
		songList = (ListView)tabListViews.get(2).findViewById(R.id.mainviewpage_music_list);
		//get about me pager parameter
		meLayout = (LinearLayout)tabListViews.get(3).findViewById(R.id.aboutme_layout);
		loginAndRegist = (Button)tabListViews.get(3).findViewById(R.id.login_aboutme);
		myBestLove = (Button)tabListViews.get(3).findViewById(R.id.bestlove_aboutme);
		recentPlay = (Button)tabListViews.get(3).findViewById(R.id.recentPlay_aboutme);
		mySongList = (Button)tabListViews.get(3).findViewById(R.id.myMusicList_aboutme);
	}
	private void setListener(){
		artist.setOnClickListener(new MainTabBarClickListener(0,mViewPager));
		album.setOnClickListener(new MainTabBarClickListener(1,mViewPager));
		song.setOnClickListener(new MainTabBarClickListener(2,mViewPager));
		aboutMe.setOnClickListener(new MainTabBarClickListener(3, mViewPager));
		mViewPager.setOnPageChangeListener(new MainViewPagerChangelistener(offset,bmpW,tabSlippageImage));
		songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(!isPopWindw){
					goToMusicPlayByListClick(2, arg2);
				}else{
					if(!isThisMusicInPopSelectMusic(allMusic.get(arg2))){
					    popWinSelectMusic.add(allMusic.get(arg2));
					}else{
						delectMusicInPopWinMusicList(allMusic.get(arg2));
					}
					adapter.checkImage(arg2);
					adapter.notifyDataSetChanged();
					topViewInfo.setText("添加"+popWinSelectMusic.size()+"项");
				}
				
			}
		
		});
		songList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				startPopupWindows(arg1);
				return true;
			}
		});
		loginAndRegist.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent inten = new Intent(MainActivity.this, LoginAndRegistActivity.class);
				MainActivity.this.startActivity(inten);
				MainActivity.this.overridePendingTransition(R.anim.in_from_top, R.anim.out_to_buttom);
			}
		});
		artistList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Artist ar = artists.get(arg2);
				List<Music> m = getMusicById(ar.getMusicIds());
				Bundle b = new Bundle();
				Intent intent = new Intent(MainActivity.this, ChildMusicListActivity.class);
				intent.putExtra("titleName", ar.getArtistName());
				b.putSerializable("music", (Serializable)m);
				intent.putExtras(b);
				MainActivity.this.startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
		albumList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Album al = albums.get(arg2);
				List<Music> m = getMusicById(al.getSongId());
				Bundle b = new Bundle();
				Intent intent = new Intent(MainActivity.this, ChildMusicListActivity.class);
				intent.putExtra("titleName", al.getName());
				b.putSerializable("music", (Serializable)m);
				intent.putExtras(b);
				MainActivity.this.startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
	}
	
	private void registBroadcastReceiver(){
		IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentfilter.addDataScheme("file");
		scanReceiver = new ScanSDReceiver();
		registerReceiver(scanReceiver, intentfilter);
		//regist other broadcastreceiver
		IntentFilter inf = new IntentFilter("com.example.musicplayer.MUSIC_CHANGE_NEXT");
		PlayInfoReceiver pir = new PlayInfoReceiver();
		registerReceiver(pir, inf);
		System.out.println("注册完毕");
	}
	
	private void sendBroadcastFreshMusic(){
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"+Environment.getExternalStorageDirectory().getAbsolutePath())));
		System.out.println("发送广播");
	}
	
	private void initImageViewSlippage(){	
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.img_tab_move).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW/4 - bmpW)/2;
		Matrix matrix =new Matrix();
		matrix.postTranslate(offset, 0);
		tabSlippageImage.setImageMatrix(matrix);
	}
	
	@SuppressLint("NewApi")
	private void initPopupWindow(){
		LayoutInflater flater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		topView = flater.inflate(R.layout.popupwindow_top, null);
		bottomView = flater.inflate(R.layout.popupwindow_bottom, null);
		topWindow = new PopupWindow(topView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		bottomWindow = new PopupWindow(bottomView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		//set top window
		topWindow.setOutsideTouchable(true);
		topWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		topWindow.update();
		topWindow.setTouchable(true);
//		topWindow.setFocusable(true);
		//set bottom window
		bottomWindow.setOutsideTouchable(true);
		bottomWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		bottomWindow.update();
		bottomWindow.setTouchable(true);
		//get popwindow parameter
		topViewCancle = (Button)topView.findViewById(R.id.topwindow_cancel);
		topViewSelectAll = (Button)topView.findViewById(R.id.topwindow_sellectall);
		topViewInfo = (TextView)topView.findViewById(R.id.topwindow_info);
		bottomViewPlay = (Button)bottomView.findViewById(R.id.bottomwindow_play);
		bottomViewDelecte = (Button)bottomView.findViewById(R.id.bottomwindow_delecte);
		Button refresh = (Button)bottomView.findViewById(R.id.bottomwindow_refresh);
		//set clickListener
		topViewCancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopPopupWindows();
			}
		});
		topViewSelectAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isSelectAll){
					topViewSelectAll.setText("全不选");
					isSelectAll = true;
					popWinSelectAllMusic();
					adapter.checkAllImage();
					adapter.notifyDataSetChanged();
				}else{
					topViewSelectAll.setText("全选");
					isSelectAll = false;
					popWinSelectMusic.clear();
					adapter.cancleAllImage();
					adapter.notifyDataSetChanged();
				}
				topViewInfo.setText("添加"+popWinSelectMusic.size()+"项");
			}
		});
		bottomViewPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToMusicPlayByPopWindow();
				stopPopupWindows();
			}
		});
		bottomViewDelecte.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*try {
					delectMusic(popWinSelectMusic);
					stopPopupWindows();
					sendBroadcastFreshMusic();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}*/
				isSendAllMusic = false;
				musicOperationDialog();
				
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendBroadcastFreshMusic();
				stopPopupWindows();
			}
		});
	}
	
	private void addPlayNavigation(){
		if(!isShowNavigaion){
			isShowNavigaion = true;
			LayoutInflater flater = getLayoutInflater();
			navigatonView = flater.inflate(R.layout.play_navigation, null);
			navigatonView.setOnClickListener(new View.OnClickListener() {
				
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
					intent.putExtra("whichView", 4);
					MainActivity.this.startActivity(intent);
					MainActivity.this.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
				}
			});
			naAlbumImage = (ImageView)navigatonView.findViewById(R.id.navigation_album_image);
			naPlayName = (TextView)navigatonView.findViewById(R.id.navigation_palyname);
			naArtist = (TextView)navigatonView.findViewById(R.id.navigation_artist);
			naPlayInfro = (ImageView)navigatonView.findViewById(R.id.navigation_play_image);
			naAnimation = AnimationUtils.loadAnimation(this, R.anim.image_whirl);
			LinearInterpolator lin = new LinearInterpolator();
			naAnimation.setInterpolator(lin);
			manLayout.addView(navigatonView);
		}
	}
	
	private void setplaynavigation(String musicname,String artist,boolean isplay){
		if(isShowNavigaion){
			naPlayName.setText(musicname);
			naArtist.setText(artist);
			if(isplay){
				if(naAnimation != null){
					naAlbumImage.clearAnimation();
					naAlbumImage.startAnimation(naAnimation);
					naPlayInfro.setImageResource(R.drawable.img_palylistshow_pause);
				}
			}else{
				naAlbumImage.clearAnimation();
				naPlayInfro.setImageResource(R.drawable.img_playlistshow_play);
			}
		}
	}
	
	private void delectPlayNavigation(){
		if(navigatonView != null)
			 naAlbumImage.clearAnimation();
		     manLayout.removeView(navigatonView);
	}
	
	private void getIntenFromOtherActivity(){
		if(isFirstStart){
			isFirstStart = false;
		}else{
			Intent intent = getIntent();
			int whichActivity = intent.getIntExtra("whichActivity", -1);
			switch (whichActivity) {
			case 1:   //intent from login
				
				break;

			case 2:  //intent from play activity
				String pName = intent.getStringExtra("playname");
				String artist = intent.getStringExtra("artist");
				boolean isplay = intent.getBooleanExtra("isplay", false);
				if(!isShowNavigaion)
					addPlayNavigation();
				setplaynavigation(pName,artist,isplay);
				break;
			case 3:  
				
				break;
			}
		}
	}
	
	private boolean isThisMusicInPopSelectMusic(Music m){
		if(popWinSelectMusic.size() != 0){
			for(int i = 0;i < popWinSelectMusic.size();i++){
				if(m.getId() == popWinSelectMusic.get(i).getId())
					return true;
			}
		}
		return false;
	}
	
	private void delectMusicInPopWinMusicList(Music m){
		if(popWinSelectMusic.size() != 0){
			for(int i = 0;i < popWinSelectMusic.size();i++){
				if(m.getId() == popWinSelectMusic.get(i).getId()){
					popWinSelectMusic.remove(i);
					break;
				}
			}
		}
	}
	
	private void startPopupWindows(View v){
		popWinSelectMusic = new ArrayList<Music>();
		isPopWindw = true;
		isSelectAll = false;
		if(!topWindow.isShowing()){
			topWindow.showAtLocation(v, Gravity.TOP, 10, 0);
			bottomWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		}
		adapter.enableCheckImage();
		adapter.notifyDataSetChanged();
	}
	
	private void stopPopupWindows(){
		if(popWinSelectMusic != null)
			popWinSelectMusic.clear();
		popWinSelectMusic = null;
		isPopWindw = false;
		topViewSelectAll.setText("全选");
		topViewInfo.setText("未添加项");
		if(topWindow.isShowing()){
			topWindow.dismiss();
			bottomWindow.dismiss();
		}
		adapter.disableCheckImage();
		adapter.notifyDataSetChanged();
	}
	
	private void popWinSelectAllMusic(){
		if(allMusic != null){
			for(int i = 0; i < allMusic.size() ;i++){
				if(!isThisMusicInPopSelectMusic(allMusic.get(i)))
					popWinSelectMusic.add(allMusic.get(i));
			}
		}
	}
		
	public void musicOperationDialog(){
		
		Dialog dialog = new AlertDialog.Builder(this)
		               .setMessage("确定要删除这"+popWinSelectMusic.size()+"项音乐吗？")
		               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								delectMusic(popWinSelectMusic);
								stopPopupWindows();
								sendBroadcastFreshMusic();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							dialog.dismiss();    
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
		dialog.show();
	}
	@SuppressLint("NewApi")
	public void goToMusicPlayByListClick(int whichview,int itemclick){
		//jump to music Play Activity
		Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
		if(isSendAllMusic == false){
		    Bundle bd =new Bundle();
		    bd.putSerializable("music", (Serializable)allMusic);
		    intent.putExtras(bd);
		    isSendAllMusic = true;
		    MusicPlayActivity.isSendAllMusic = false;
		}
		intent.putExtra("whichView", whichview);   //the music list view send 
		intent.putExtra("clickItem", itemclick);  //which item click
		MainActivity.this.startActivity(intent);
		MainActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		addPlayNavigation();
		setplaynavigation(allMusic.get(itemclick).getDiaplayName(),allMusic.get(itemclick).getArtist(),true);
	}
	
	@SuppressLint("NewApi")
	public void goToMusicPlayByPopWindow(){
		if(popWinSelectMusic.size() != 0){
			isSendAllMusic = false;
			MusicPlayActivity.isSendAllMusic = false;
			Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
		    Bundle bd =new Bundle();
		    bd.putSerializable("music", (Serializable)popWinSelectMusic);
		    intent.putExtras(bd);
		    intent.putExtra("whichView", 2);   //the music list view send 
		    intent.putExtra("clickItem", 0);  //which item click
		    MainActivity.this.startActivity(intent);
		    MainActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		    addPlayNavigation(); 
		    setplaynavigation(popWinSelectMusic.get(0).getDiaplayName(),popWinSelectMusic.get(0).getArtist(),true);
		}

	}
	
	public void addMusicList(List<Music> mymusic){
		if(musicMap != null && musicMap.size() != 0)
			musicMap.clear();
		for(int i = 1;i<= mymusic.size();i++){
			Map<String, Object>  mm = new HashMap<String, Object>();
			mm.put("title",i+" .");
			mm.put("artist", mymusic.get(i-1).getArtist());
			mm.put("playName", mymusic.get(i-1).getDiaplayName());
			musicMap.add(mm);
		}
//        SimpleAdapter adapter = new SimpleAdapter(this,musicMap,R.layout.listview_play_list,new String[]{"title",
//        		"playName","artist"},new int[]{R.id.listview_textview_title,R.id.listview_textview_palyname,R.id.listview_textview_artist});
        adapter = new MusicListAdapter(this, musicMap);
		songList.setAdapter(adapter);
		addArtistlist(mymusic);
		addAlbumList(mymusic);
   }
	
	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public void addArtistlist(List<Music> myMusic){
//		 artists = new ArrayList<Artist>();
//		 boolean[] isRange = new boolean[myMusic.size()];
//		 for(int i = 0;i < isRange.length ;i++)
//			 isRange[i] = false;
//		 for(int j = 0;j < myMusic.size();j++){
//			 if(!isRange[j]){
//				 Artist a = new Artist(new ArrayList<Long>());
//				 String artistname = myMusic.get(j).getArtist();
//				 a.setArtistName(artistname);
//				 a.addMusicId(myMusic.get(j).getId());
//				 for(int k = (j+1);k < myMusic.size();k++){
//					 if(!isRange[k]){
//					     if(artistname.equals(myMusic.get(k).getArtist())){
//					    	 a.addMusicId(myMusic.get(k).getId());
//					    	 isRange[k] = true;
//					     }
//					 }
//
//				 }
//				 artists.add(a);
//			 }
//		 }
		 new AsyncTask<List<Music>, Integer, List<Artist>>(){

			@Override
			protected List<Artist> doInBackground(List<Music>... params) {
				List<Artist> ar = new ArrayList<Artist>();
				List<Music> music = params[0];
				 boolean[] isRange = new boolean[music.size()];
				 for(int i = 0;i < isRange.length ;i++)
					 isRange[i] = false;
				 for(int j = 0;j < music.size();j++){
					 if(!isRange[j]){
						 Artist a = new Artist(new ArrayList<Long>());
						 String artistname = music.get(j).getArtist();
						 a.setArtistName(artistname);
						 a.addMusicId(music.get(j).getId());
						 for(int k = (j+1);k < music.size();k++){
							 if(!isRange[k]){
							     if(artistname.equals(music.get(k).getArtist())){
							    	 a.addMusicId(music.get(k).getId());
							    	 isRange[k] = true;
							     }
							 }

						 }
						 ar.add(a);
					 }
				 }
				return ar;
			}
			@Override
			protected void onPostExecute(List<Artist> result) {
				artists = result;
				ArtistListAdapter artistAdapter = new ArtistListAdapter(MainActivity.this, artists);
				artistList.setAdapter(artistAdapter);
			}
			 
		 }.execute(myMusic);
//		ArtistListAdapter artistAdapter = new ArtistListAdapter(MainActivity.this, artists);
//		artistList.setAdapter(artistAdapter);

	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public void addAlbumList(List<Music> myMusic){
//		 albums = new ArrayList<Album>();
//		 boolean[] isRange = new boolean[myMusic.size()];
//		 for(int i = 0;i < isRange.length ;i++)
//			 isRange[i] = false;
//		 for(int j = 0;j < myMusic.size();j++){
//			 if(!isRange[j]){
//				 long albumid = myMusic.get(j).getAlbimId();
//				 String albmtname = myMusic.get(j).getAlbum();
//				 Album a = new Album(albumid, albmtname, new ArrayList<Long>());
//				 a.addSongId(myMusic.get(j).getId());
//				 for(int k = (j+1);k < myMusic.size();k++){
//					 if(!isRange[k]){
//					     if(albumid == myMusic.get(k).getAlbimId()){
//					    	 a.addSongId(myMusic.get(k).getId());
//					    	 isRange[k] = true;
//					     }
//					 }
//
//				 }
//				 albums.add(a);
//			 }
//		 }
		 new AsyncTask<List<Music>, Integer, List<Album>>(){

			@Override
			protected List<Album> doInBackground(List<Music>... params) {
				List<Album> al = new ArrayList<Album>();
				List<Music> music = params[0];
				boolean[] isRange = new boolean[music.size()];
				 for(int i = 0;i < isRange.length ;i++)
					 isRange[i] = false;
				 for(int j = 0;j < music.size();j++){
					 if(!isRange[j]){
						 long albumid = music.get(j).getAlbimId();
						 String albmtname = music.get(j).getAlbum();
						 Album a = new Album(albumid, albmtname, new ArrayList<Long>());
						 a.addSongId(music.get(j).getId());
						 for(int k = (j+1);k < music.size();k++){
							 if(!isRange[k]){
							     if(albumid == music.get(k).getAlbimId()){
							    	 a.addSongId(music.get(k).getId());
							    	 isRange[k] = true;
							     }
							 }

						 }
						 al.add(a);
					 }
				 }
				return al;
			}
			@Override
			protected void onPostExecute(List<Album> result) {
				albums = result;
				AlbumListAdapter albumAdapter = new AlbumListAdapter(MainActivity.this, albums);
				albumList.setAdapter(albumAdapter);
			}
			 
		 }.execute(myMusic);
//		 AlbumListAdapter albumAdapter = new AlbumListAdapter(this, albums);
//		 albumList.setAdapter(albumAdapter);
	}
	
	public List<Music> getMusic(){
    	List<Music> music = new ArrayList<Music>();
    	Cursor cur = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
    			null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    	while(cur.moveToNext()){
    		String dn = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
    		if(dn.endsWith("mp3")){
    		    Music m = new Music();
    		    m.setDiaplayName(dn);
    		    m.setArtist(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
    		    m.setDuration(cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
    		    m.setSize(cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
    		    m.setId(cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
    		    m.setUrl(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
    		    music.add(m);
    		}
    	}
    	return music;
    }
	
	public List<Music> getMusicById(List<Long> ids){
		List<Music> musics = new ArrayList<Music>();
		boolean[] ischecked = new boolean[allMusic.size()];
		for(int k = 0;k < ischecked.length;k++)
			ischecked[k] = false;
		for(int i = 0;i < ids.size(); i++){
			long id = ids.get(i);
			for(int j = 0;j < allMusic.size();j++){
				if(!ischecked[j]){
					if(id == allMusic.get(j).getId()){
						musics.add(allMusic.get(j));
						ischecked[j] = true;
					}
				}
			}
		}
		ischecked = null;
		return musics;
	}
	
	public void delectMusic(List<Music> music) throws FileNotFoundException{
		for(int i = 0;i < music.size();i++){
			String url = music.get(i).getUrl();
			File f = new File(url);
			if(f.exists() && !f.isDirectory()){
				f.delete();
			}
		}
	}
	
	public void exitApplication(){
		Dialog dialog = new AlertDialog.Builder(this)
		                .setMessage("你确定要退出吗？")
		                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
		                	@Override
							public void onClick(DialogInterface dialog, int which) {
							    Intent intent = new Intent(EXIT);
								MainActivity.this.sendBroadcast(intent);
								MainActivity.this.finish();
								dialog.dismiss(); 
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
										
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
		                                
	}
	
	public class ScanSDReceiver extends BroadcastReceiver{

		private AlertDialog.Builder bulider = null;
		private AlertDialog dialog = null;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){
				bulider = new AlertDialog.Builder(context);
				bulider.setMessage("更新音乐中请稍后。。。");
				dialog = bulider.create();
				dialog.show();
				System.out.println("开始刷新");
			}else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){

				System.out.println("刷新完成");
				if(allMusic != null && allMusic.size() != 0){
					allMusic.clear();
					allMusic = null;
				}
//				allMusic = getMusic();
//				allMusic = MediaUtil.getMusic(MainActivity.this);
//				addMusicList(allMusic);
				GetMusicTask task = new GetMusicTask(MainActivity.this);
				task.execute();
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		}
		
	}
	
	public class PlayInfoReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
				String name = intent.getStringExtra("musicname");
				String artist = intent.getStringExtra("artist");
				setplaynavigation(name, artist, true);
		}
		
	}
	
	@SuppressLint("NewApi")
	public class GetMusicTask extends AsyncTask<Void, Integer, List<Music>>{
		
		private Context con;
		
		public GetMusicTask(Context c){
			con = c;
		}
		
		@Override
		protected List<Music> doInBackground(Void... params) {
			List<Music> m = MediaUtil.getMusic(con);
			return m;
		}
		@Override
		protected void onPostExecute(List<Music> result) {
			allMusic = result;
			addMusicList(result);
		}
	}
}
