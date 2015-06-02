package com.example.musicplayer.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MainViewPagerAdapter extends PagerAdapter{
	private List<View> mListView;
	public MainViewPagerAdapter(List<View> listview){
		mListView = listview;
	}
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(mListView.get(position));
	}
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(mListView.get(position), 0);
		return mListView.get(position);
	}
	@Override
	public int getCount() {
		return mListView.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

}
