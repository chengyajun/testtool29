package com.qihoo.testtools_new.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihoo.testtools_new.AppsManagerActivity;
import com.qihoo.testtools_new.R;
import com.qihoo.testtools_new.bean.ApplicationBean;

public class AppsAdapter extends BaseAdapter {

	private Context mContext;
	private int mResId;
	private LayoutInflater inflater;
	private ArrayList<ApplicationBean> mAppsList;

	// 构造方法
	public AppsAdapter(Context context, int resId, ArrayList<ApplicationBean> list) {
		this.mContext = context;
		this.mResId = resId;
		this.inflater = LayoutInflater.from(context);
		setdata(list);
	}

	private void setdata(List<ApplicationBean> list) {
		if (list == null) {
			list = new ArrayList<ApplicationBean>();
		}
		this.mAppsList = (ArrayList<ApplicationBean>) list;

	}
	public void setDataChanged(List<ApplicationBean> appsList){
		setdata(appsList);
	}

	@Override
	public int getCount() {
		return mAppsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 可重用视图
		if (convertView == null) {
			convertView = inflater.inflate(mResId, null);
		}
		// 获取当前值
		ApplicationBean item = mAppsList.get(position);
		
		TextView tvAppName = (TextView) convertView
				.findViewById(R.id.tv_app_name);
		tvAppName.setText("" + item.getAppname());
		
		ImageView ivappImage = (ImageView) convertView.findViewById(R.id.iv_app_img);
		ivappImage.setImageDrawable(item.getAppimage());
		// 点击跳转到测试管理页面
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("position", position);
				intent.setClass(mContext, AppsManagerActivity.class);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

}
