package com.qihoo.testtools_new.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class HistoryManagerDetailAdapter extends PagerAdapter {

	private Context mContext;
	private ArrayList<View> mViews;
	 private int mChildCount = 0;

	public HistoryManagerDetailAdapter(Context context, ArrayList<View> views) {
		this.mContext = context;
		this.mViews = views;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		container.addView(mViews.get(position));

		return mViews.get(position);

	}
	public void setData(ArrayList<View> views){
		this.mViews = views;
	}
	
	
}
