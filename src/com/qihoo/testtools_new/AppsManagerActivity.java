package com.qihoo.testtools_new;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo.testtools_new.bean.ApplicationBean;
import com.qihoo.testtools_new.service.FloatingService;
import com.qihoo.testtools_new.thread.HistoryApplicationThread;
import com.qihoo.testtools_new.utils.GetAppLaunchTime;

public class AppsManagerActivity extends Activity {

	TextView app_manager_back, app_manager_appName, app_manager_count;
	ImageView app_manager_appImg;
	CheckBox app_manager_CPU_cb, app_manager_mem_cb, app_manager__bat_cb, app_manager_traffic_cb;
	Button app_manager_start_btn, app_manager_history_btn;
	CheckBox[] boxs = new CheckBox[4];

	ApplicationBean appBean;

	GetAppLaunchTime getAppLaunchTime;
	int position;

	boolean isAppStart = false;
	FloatViewStopBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_manager);

		
		TestToolNewApplication.isCPURecord = false;
		TestToolNewApplication.isMEMRecord = false; 
		TestToolNewApplication.isBATRecord = false; 
		TestToolNewApplication.isTRAFRecord = false; 
		
		
		
		
		position = getIntent().getIntExtra("position", 0);
		appBean = TestToolNewApplication.appsList.get(position);

		getAppLaunchTime = new GetAppLaunchTime();

		initView();
		initData();
		initListener();

		// 注册广播
		receiver = new FloatViewStopBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_FLOAT_VIEW_STOP);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initView() {
		app_manager_back = (TextView) findViewById(R.id.app_manager_back);
		app_manager_appName = (TextView) findViewById(R.id.app_manager_appName);
		app_manager_count = (TextView) findViewById(R.id.app_manager_count);
		app_manager_appImg = (ImageView) findViewById(R.id.app_manager_appImg);
		app_manager_CPU_cb = (CheckBox) findViewById(R.id.app_manager_CPU_cb);
		app_manager_mem_cb = (CheckBox) findViewById(R.id.app_manager_mem_cb);
		app_manager__bat_cb = (CheckBox) findViewById(R.id.app_manager__bat_cb);
		app_manager_traffic_cb = (CheckBox) findViewById(R.id.app_manager_traffic_cb);
		app_manager_start_btn = (Button) findViewById(R.id.app_manager_start_btn);
		app_manager_history_btn = (Button) findViewById(R.id.app_manager_history_btn);
		boxs[0] = app_manager_CPU_cb;
		boxs[1] = app_manager_mem_cb;
		boxs[2] = app_manager__bat_cb;
		boxs[3] = app_manager_traffic_cb;

	}

	private void initData() {
		app_manager_appImg.setImageDrawable(appBean.getAppimage());
		app_manager_appName.setText(appBean.getAppname() + "");

	}

	private void initListener() {

		app_manager_back.setOnClickListener(lis);
		app_manager_CPU_cb.setOnClickListener(lis);
		app_manager_mem_cb.setOnClickListener(lis);
		app_manager__bat_cb.setOnClickListener(lis);
		app_manager_traffic_cb.setOnClickListener(lis);
		app_manager_start_btn.setOnClickListener(lis);
		app_manager_history_btn.setOnClickListener(lis);

	}

	OnClickListener lis = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.app_manager_back: {// 点击返回
				AppsManagerActivity.this.finish();
			}
				break;
			case R.id.app_manager_CPU_cb: {// 点击CPUcheckbox

				checkCount();
			}
				break;
			case R.id.app_manager_mem_cb: {
				checkCount();
			}
				break;
			case R.id.app_manager_traffic_cb: {
				checkCount();
			}
				break;
			case R.id.app_manager__bat_cb: {
				checkCount();
			}
				break;
			case R.id.app_manager_start_btn: {// 点击启动测试，开始运行程序，进行检测

				if (isAppStart) {// 开启,则点击后关闭服务

					isAppStart = false;

					app_manager_start_btn.setText("开启测试");
					Intent intent = new Intent();
					intent.setClass(AppsManagerActivity.this, FloatingService.class);
					// intent.putExtra("position", position);
					stopService(intent);

				} else {// 如果是关闭，则点击后开启服务
					isAppStart = true;
					app_manager_start_btn.setText("停止测试");
					
					
					
					//记录测试选项时先清空记录
					TestToolNewApplication.isCPURecord = false;
					TestToolNewApplication.isMEMRecord = false; 
					TestToolNewApplication.isBATRecord = false; 
					TestToolNewApplication.isTRAFRecord = false; 
					
					//记录测试项
					//app_manager_CPU_cb, app_manager_mem_cb, app_manager__bat_cb, app_manager_traffic_cb
					if(app_manager_CPU_cb.isChecked()){
						TestToolNewApplication.isCPURecord = true;
					}
					if(app_manager_mem_cb.isChecked()){
						TestToolNewApplication.isMEMRecord = true;
					}
					if(app_manager__bat_cb.isChecked()){
						TestToolNewApplication.isBATRecord = true;
					}
					if(app_manager_traffic_cb.isChecked()){
						TestToolNewApplication.isTRAFRecord = true;
					}

					// 开启显示悬浮窗Service
					start_flow();

					// 启动程序
					startApp();

				}
				// // 开启显示悬浮窗Service
				// start_flow();
				//
				// // 启动程序
				// startApp();

			}
				break;
			case R.id.app_manager_history_btn: {// 点击查看记录，跳转到记录页

				// 启动线程，加载该app的所有测试记录
				HistoryApplicationThread historyAppThread = new HistoryApplicationThread(appBean.getPackagename(),
						appBean.getAppname());
				historyAppThread.start();

				// 实现跳转
				Intent intent = new Intent();
				intent.setClass(AppsManagerActivity.this, HistoryManagerActivity.class);
				intent.putExtra("appname", appBean.getAppname());
				AppsManagerActivity.this.startActivity(intent);
			}
				break;
			}

		}
	};

	protected void checkCount() {
		int count = 0;
		for (CheckBox box : boxs) {
			if (box.isChecked()) {// 选中

				count++;
			}
		}
		app_manager_count.setText("选择测试的内容：" + count);
		if (count == 0) {
			// 无法点击开始测试
			app_manager_start_btn
					.setBackgroundDrawable(getResources().getDrawable(R.drawable.app_manager_start_btn_no_03));
			app_manager_start_btn.setClickable(false);
		} else {
			// 无法点击开始测试
			app_manager_start_btn
					.setBackgroundDrawable(getResources().getDrawable(R.drawable.app_manager_start_btn_03));
			app_manager_start_btn.setClickable(true);

		}
		


	}

	private void startApp() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if ("com.qihoo.testtools_new".equals(appBean.getPackagename())) {
			// 如果是自己就不kill
			return;
		} else {
			am.killBackgroundProcesses(appBean.getPackagename());

			Intent launchIntent = new Intent();
			launchIntent.setComponent(new ComponentName(appBean.getPackagename(), appBean.getMainactivity()));

			// startActivity(launchIntent);

			// thisTime即是App的启动时间
			long thisTime = getAppLaunchTime.startActivityWithTime(launchIntent);
			Toast.makeText(AppsManagerActivity.this, "启动程序耗时" + thisTime + "毫秒", Toast.LENGTH_LONG).show();
		}
	}

	private void start_flow() {
		Intent intent = new Intent();
		intent.setClass(this, FloatingService.class);
		intent.putExtra("position", position);
		startService(intent);
	}

	class FloatViewStopBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TestToolNewApplication.INTENT_ACTION_BROADCAST_FLOAT_VIEW_STOP.equals(action)) {
				// 更新界面
				isAppStart = false;
				app_manager_start_btn.setText("开启测试");

			}
		}

	}

}
