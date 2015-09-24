package com.qihoo.testtools_new;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class FragmentSettings extends Fragment {
	View rootView;
	RelativeLayout setting_delete_layout;
	ToggleButton setting_time_tb, setting_window_tb;
	static boolean isTimeChange = false;
	static boolean isWindowShow = true;
	SeekBar time_seekbar;
	LinearLayout setting_time_seekbar_layout;
	TextView setting_time_tv;
	int mProgress = 40;
	int mSecond = 3;

	SharedPreferences preferences;
	Editor editor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_settings, null);

		preferences = getActivity().getSharedPreferences(TestToolNewApplication.fileName, 0);
		editor = preferences.edit();
		isWindowShow = preferences.getBoolean("isWindowOpen", true);
		
		mSecond = preferences.getInt("time", 3);
		mProgress = (mSecond-1)*20;

		initView();
		initListener();
		return rootView;
	}

	private void initView() {
		setting_delete_layout = (RelativeLayout) rootView
				.findViewById(R.id.setting_delete_layout);
		setting_time_tb = (ToggleButton) rootView
				.findViewById(R.id.setting_time_tb);
		setting_window_tb = (ToggleButton) rootView
				.findViewById(R.id.setting_window_tb);
		time_seekbar = (android.widget.SeekBar) rootView
				.findViewById(R.id.setting_time_seekbar);
		setting_time_seekbar_layout = (LinearLayout) rootView
				.findViewById(R.id.setting_time_seekbar_layout);
		setting_time_tv = (TextView) rootView
				.findViewById(R.id.setting_time_tv);

		setting_time_tv.setText(mSecond + "s");

		if (!isTimeChange) {// 关闭
			isTimeChange = false;
			setting_time_tb.setChecked(false);
			setting_time_seekbar_layout.setVisibility(View.GONE);

		} else {// 显示
			isTimeChange = true;
			setting_time_tb.setChecked(true);
			time_seekbar.setProgress(mProgress);
			setting_time_seekbar_layout.setVisibility(View.VISIBLE);
		}

		if (isWindowShow) {
			setting_window_tb.setChecked(true);
		} else {
			setting_window_tb.setChecked(false);
		}

	}

	private void initListener() {

		setting_delete_layout.setOnClickListener(lis);
		setting_time_tb.setOnClickListener(lis);
		setting_window_tb.setOnClickListener(lis);
		time_seekbar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		time_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int progress = seekBar.getProgress();
				int max = seekBar.getMax();
				if (progress < max * 0.1) {
					mProgress = 0;
					mSecond = 1;
					seekBar.setProgress(0);
					setting_time_tv.setText("1s");
					editor.putInt("time", 1);
					editor.commit();

				} else if (progress < max * 0.3) {
					mProgress = 20;
					mSecond = 2;
					seekBar.setProgress(20);
					setting_time_tv.setText("2s");
					editor.putInt("time", 2);
					editor.commit();

				} else if (progress < max * 0.5) {
					mProgress = 40;
					mSecond = 3;
					seekBar.setProgress(40);
					setting_time_tv.setText("3s");
					editor.putInt("time", 3);
					editor.commit();

				} else if (progress < max * 0.7) {
					mProgress = 60;
					mSecond = 4;
					seekBar.setProgress(60);
					setting_time_tv.setText("4s");
					editor.putInt("time", 4);
					editor.commit();
				} else if (progress < max * 0.9) {
					mProgress = 80;
					mSecond = 5;
					seekBar.setProgress(80);
					setting_time_tv.setText("5s");
					editor.putInt("time", 5);
					editor.commit();
				} else {
					mProgress = 100;
					mSecond = 6;
					seekBar.setProgress(100);
					setting_time_tv.setText("6s");
					editor.putInt("time", 6);
					editor.commit();
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

	}

	OnClickListener lis = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_delete_layout: {
				Intent intent = new Intent();
				intent.setAction("com.qihoo.testtool_new.setting_delete");
				getActivity().sendBroadcast(intent);
			}
				break;
			case R.id.setting_time_tb: {
				if (isTimeChange) {// 显示，则关闭
					isTimeChange = false;
					setting_time_tb.setChecked(false);
					setting_time_seekbar_layout.setVisibility(View.GONE);

				} else {// 关闭，则开启
					isTimeChange = true;
					setting_time_tb.setChecked(true);
					time_seekbar.setProgress(mProgress);
					setting_time_seekbar_layout.setVisibility(View.VISIBLE);
				}

			}
				break;
			case R.id.setting_window_tb: {
				if (isWindowShow) {// 显示
					isWindowShow = false;

					editor.putBoolean("isWindowOpen", false);
					editor.commit();

					setting_window_tb.setChecked(false);
					Toast.makeText(getActivity(), "关闭", 3000).show();
				} else {
					isWindowShow = true;
					editor.putBoolean("isWindowOpen", true);
					editor.commit();

					setting_window_tb.setChecked(true);
					Toast.makeText(getActivity(), "开启", 3000).show();
				}
			}
				break;
			}

		}
	};
}
