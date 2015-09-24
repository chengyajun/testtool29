package com.qihoo.testtools_new.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihoo.testtools_new.HistoryManagerActivity;
import com.qihoo.testtools_new.R;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.HistoryApplicationThread;

public class HistoryAdapter extends BaseAdapter {

	private Context mContext;
	private int mResId;
	private LayoutInflater inflater;
	private ArrayList<HistoryBean> historyList;

	// ���췽��
	public HistoryAdapter(Context context, int resId,
			ArrayList<HistoryBean> list) {
		this.mContext = context;
		this.mResId = resId;
		this.inflater = LayoutInflater.from(context);
		setdata(list);
	}

	private void setdata(List<HistoryBean> list) {
		if (list == null) {
			list = new ArrayList<HistoryBean>();
		}
		this.historyList = (ArrayList<HistoryBean>) list;

	}

	public void setDataChanged(List<HistoryBean> historyAppsList) {
		setdata(historyAppsList);
	}

	@Override
	public int getCount() {
		return historyList.size();
	}

	@Override
	public Object getItem(int position) {
		return historyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ��������ͼ
		if (convertView == null) {
			convertView = inflater.inflate(mResId, null);
		}

		// ��ȡ��ǰֵ
		final HistoryBean bean = historyList.get(position);

		TextView tvAppName = (TextView) convertView
				.findViewById(R.id.tv_history_name);
		tvAppName.setText("" + bean.getAppname());

		ImageView ivAppIcon = (ImageView) convertView
				.findViewById(R.id.iv_history_img);
		ivAppIcon.setImageDrawable(bean.getAppimage());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(bean.getDate());
		String date = format.format(d);
		TextView tvDate = (TextView) convertView.findViewById(R.id.tv_history_time);
		tvDate.setText(""+date);
		
		// �����ת�����Թ���ҳ��
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//�����̣߳����ظ�app�����в��Լ�¼
				HistoryApplicationThread historyAppThread = new HistoryApplicationThread(bean.getPackagename(),bean.getAppname());
				historyAppThread.start();
				
				//ʵ����ת
				Intent intent = new Intent();
				intent.setClass(mContext, HistoryManagerActivity.class);
				intent.putExtra("appname", bean.getAppname());
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

}
