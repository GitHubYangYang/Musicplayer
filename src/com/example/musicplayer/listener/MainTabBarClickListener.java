package com.example.musicplayer.listener;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class MainTabBarClickListener implements OnClickListener{
	
	private int index = 0;
	private ViewPager vp;
	public MainTabBarClickListener(int i,ViewPager v){
		index = i;
		vp = v;
	}
	@Override
	public void onClick(View v) {
		vp.setCurrentItem(index);
	}

}
