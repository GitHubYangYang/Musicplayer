package com.example.musicplayer.listener;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MainViewPagerChangelistener implements OnPageChangeListener{
	private int offset;
	private int bmpW;
	private int moveOneStep;
	private int moveTwoStep;
	private int moveThreeStep;
	private int currentIndex;
	private ImageView tabSlippageImage;
	public MainViewPagerChangelistener(int offset,int bmpW,ImageView curr){
		this.offset = offset;
		this.bmpW = bmpW;
		this.moveOneStep = offset * 2 + bmpW;
		this.moveTwoStep = moveOneStep * 2;
		this.moveThreeStep = moveOneStep * 3;
		currentIndex = 0;
		tabSlippageImage = curr;
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		Animation animation = null;
		switch (arg0) {
		case 0:
			if(currentIndex == 1){
				animation = new TranslateAnimation(moveOneStep,0,0,0);
			}else if(currentIndex ==2){
				animation = new TranslateAnimation(moveTwoStep, 0,0,0);
			}else if(currentIndex == 3){
				animation = new TranslateAnimation(moveThreeStep,0,0,0);
			}
			break;

		case 1:
			if(currentIndex == 0){
				animation = new TranslateAnimation(offset,moveOneStep,0,0);
			}else if(currentIndex ==2){
				animation = new TranslateAnimation(moveTwoStep, moveOneStep,0,0);
			}
			else if(currentIndex == 3)
			{
				animation = new TranslateAnimation(moveThreeStep, moveOneStep,0,0);
			}
			break;
		case 2:
			if(currentIndex == 0){
				animation = new TranslateAnimation(offset,moveTwoStep,0,0);
			}else if(currentIndex ==1){
				animation = new TranslateAnimation(moveOneStep, moveTwoStep,0,0);
			}else if(currentIndex == 3){
				animation = new TranslateAnimation(moveThreeStep, moveTwoStep,0,0);
			}
			break;
		case 3:
			if(currentIndex == 0){
				animation = new TranslateAnimation(offset,moveThreeStep,0,0);
			}else if(currentIndex ==1){
				animation = new TranslateAnimation(moveOneStep, moveThreeStep,0,0);
			}else if(currentIndex == 2){
				animation = new TranslateAnimation(moveTwoStep, moveThreeStep,0,0);
			}
			break;
		}
		currentIndex = arg0;
		animation.setFillAfter(true);
		animation.setDuration(400);
		tabSlippageImage.setAnimation(animation);
	}

}
