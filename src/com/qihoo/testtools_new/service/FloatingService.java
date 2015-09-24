package com.qihoo.testtools_new.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.opencsv.CSVWriter;
import com.qihoo.testtools_new.AppsManagerActivity;
import com.qihoo.testtools_new.HistoryManagerDetailActivity;
import com.qihoo.testtools_new.R;
import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.ApplicationBean;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.HistoryRecordThread;
import com.qihoo.testtools_new.utils.AppDataUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingService extends Service {

	// 进行所有数据的获取：耗电量，当前cpu占用率，当前应用程序占用内存，WiFi，3g.
	// 数据的显示和存储

	// boolean isShow = true;
	private View view;
	private WindowManager windowManager;
	private DisplayMetrics metric;
	private WindowManager.LayoutParams layoutParams;
	private TextView tv_memory, tv_traffic, tv_cpu;
	private Button btn_close;
	// private LinearLayout ll;
	public boolean viewAdded = false;
	private long action_down_time;
	private long action_up_time;
	private int statusBarHeight;
	final int FLAG_ACTIVITY_NEW_TASK = Intent.FLAG_ACTIVITY_NEW_TASK;
	private Handler handler = new Handler();

	SharedPreferences preferences;
	Editor editor;
	boolean isWindowShow;

	int position;
	ApplicationBean bean;

	AppDataUtils appDataUtils;
	String fileName;
	File file;
	private HandlerThread mHandlerThread;
	private MyHandler mMyHandler;
	CSVWriter writer;

	int timeSave;
	long date;
	boolean isNewHistoryApp = true;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	// 移动刷新view
	private void refreshView(int x, int y) {
		if (statusBarHeight == 0) {
			View rootView = view.getRootView();
			Rect r = new Rect();
			rootView.getWindowVisibleDisplayFrame(r);
			statusBarHeight = r.top;
		}
		layoutParams.x = x;
		layoutParams.y = y - statusBarHeight;// STATUS_HEIGHT;
		refresh();
	}

	private void initFloatView() {
		tv_memory = (TextView) view.findViewById(R.id.tv_memory);
		tv_traffic = (TextView) view.findViewById(R.id.tv_traffic);
		tv_cpu = (TextView) view.findViewById(R.id.tv_cpu);
		btn_close = (Button) view.findViewById(R.id.close);

		
		//点击退出            
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stop();
				
				
				// 启动线程
//				String fileName = bean.getPackagename() + "_"
//						+ bean.getDate() + ".csv";
				
//				HistoryRecordThread historyRecordThread = new HistoryRecordThread(
//						fileName);
//				historyRecordThread.start();
				
				//历史数据里增加数据 TestToolNewApplication.historyAppsList
				for (int i = 0; i < TestToolNewApplication.historyAppsList.size(); i++) {
					HistoryBean historyBean = TestToolNewApplication.historyAppsList.get(i);
					if(historyBean.getAppname().equals(bean.getAppname())){
						historyBean.setDate(date);
						isNewHistoryApp = false;
						break;
					}
				}
				if(isNewHistoryApp){
					
					HistoryBean newBean = new HistoryBean();
					newBean.setDate(date);
					newBean.setPackagename(bean.getPackagename());
					// 获取图标和appname
//					PackageManager pm = getPackageManager();
//
//					Drawable icon = pm.getApplicationIcon(bean.getPackagename());
//					String appName = (String) pm.getApplicationLabel(pm
//							.getApplicationInfo(packagename, 0));
					newBean.setAppimage(bean.getAppimage());
					newBean.setAppname(bean.getAppname());
					
					
					TestToolNewApplication.historyAppsList.add(newBean);
				}
				
				// list按照date降序排列
				Collections.sort(TestToolNewApplication.historyAppsList, new Comparator<HistoryBean>() {

					@Override
					public int compare(HistoryBean bean1, HistoryBean bean2) {
						// if(bean1.getDate()>bean2.getDate()){
						// return 1;
						// }
						return (int) (bean2.getDate() - bean1.getDate());

					}
				});
				
				
				Intent intent = new Intent();
				intent.setClass(FloatingService.this, HistoryManagerDetailActivity.class);
				intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("appname", bean.getAppname());
				intent.putExtra("date", date);
				intent.putExtra("filename", fileName);
				startActivity(intent);
			}
		});

	}

	private void stop() {
		stopSelf();

		// AppsManagerActivity.isAppStart = false;
		Intent intent = new Intent();
		intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_FLOAT_VIEW_STOP);
		sendBroadcast(intent);

		// 该app停止运行
		// 点击退出，运行的app停止，被系统kill掉 ，该功能未实现
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(bean.getPackagename());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		position = intent.getIntExtra("position", 0);
		bean = TestToolNewApplication.appsList.get(position);

		// 创建文件夹
		File file1 = new File("/mnt/sdcard/testtool");
		if (!file1.exists() && !file1.isDirectory()) {
			System.out.println("//不存在");
			file1.mkdir();
		} else {
			System.out.println("//目录存在");
		}

		// 创建csv文件
		// 创建线程，线程主要做读写csv文件数据，然后点击一次写一次数据

		date = System.currentTimeMillis();
		fileName = bean.getPackagename() +"_"+ date + ".csv";
		file = new File("/mnt/sdcard/testtool", fileName);
		// boolean b = file.exists();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("sssss----" + f);
		}

		// 创建handlerThread线程，主要用来3s主线程发消息到子线程中向csv文件存储数据
		mHandlerThread = new HandlerThread("handler_thread");
		mHandlerThread.start();

		mMyHandler = new MyHandler(mHandlerThread.getLooper());

		
		
		
		appDataUtils = new AppDataUtils(this, position);

		preferences = getSharedPreferences(TestToolNewApplication.fileName, 0);
		editor = preferences.edit();
		timeSave = preferences.getInt("time", 3);
		isWindowShow = preferences.getBoolean("isWindowOpen", true);

		
		mMyHandler.sendEmptyMessage(4);
		
		
		Log.i("info", "-------" + isWindowShow);
		if (isWindowShow) {
			view = LayoutInflater.from(this).inflate(R.layout.floatingview,
					null);
			windowManager = (WindowManager) this
					.getSystemService(WINDOW_SERVICE);

			// type
			metric = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(metric);
			/*
			 * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
			 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
			 * PixelFormat.TRANSPARENT：悬浮窗透明
			 */
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
					LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
			layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
			initFloatView();

			// windowManager.addView(view, layoutParams);

			view.setOnTouchListener(new OnTouchListener() {
				float[] temp = new float[] { 0f, 0f };

				public boolean onTouch(View v, MotionEvent event) {
					layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
					int eventaction = event.getAction();
					switch (eventaction) {
					case MotionEvent.ACTION_DOWN:
						temp[0] = event.getX();
						temp[1] = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						refreshView((int) (event.getRawX() - temp[0]),
								(int) (event.getRawY() - temp[1]));
						break;
					case MotionEvent.ACTION_UP:

						if ((event.getRawX() - temp[0]) > (metric.widthPixels / 2)) {
							refreshView(metric.widthPixels,
									(int) (event.getRawY() - temp[1]));
						} else {
							refreshView(0, (int) (event.getRawY() - temp[1]));
						}
						break;
					}
					return false;
				}
			});

		}

		if (isWindowShow) {
			refresh();

		}

		// 获取电量
		appDataUtils.GetBatteryStatus();

		// 获取流量
		appDataUtils.getTraffic();

		// 更新界面
		handler.post(refreshData);

		return super.onStartCommand(intent, flags, startId);
	}

	private void refresh() {
		if (viewAdded) {
			windowManager.updateViewLayout(view, layoutParams);
		} else {
			windowManager.addView(view, layoutParams);
			viewAdded = true;
		}
	}

	public void onDestroy() {
		// logToast();
		// util.setServiceRunning(false);
		// util.setFLAG(0);

		// 获取耗电量
		appDataUtils.stopGetBatteryStatus();
		mMyHandler.sendEmptyMessage(1);

		// 获取流量
		appDataUtils.getTraffic();
		mMyHandler.sendEmptyMessage(2);
		mMyHandler.sendEmptyMessage(5);
		Toast.makeText(
				this,
				"wifi:" + bean.getTRAFFIC_WIFI() + "----3g"
						+ bean.getTRAFFIC_3G(), 10000).show();

		removeView();
		// unregisterReceiver(MyBatteryReceiver);
		handler.removeCallbacks(refreshData);
		try {
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private void removeView() {
		if (viewAdded) {
			windowManager.removeView(view);
			viewAdded = false;
		}
	}

	/**
	 * 实现数据的更新和存储
	 * 
	 * 每次先获取数据，并进行存储 更新界面显示
	 */

	int count = 0;
	long historyTime = System.currentTimeMillis();
	private Runnable refreshData = new Runnable() {
		@Override
		public void run() {

			
			// 获取数据
			appDataUtils.gettestData();

			if (isWindowShow) {

				RefreshUI();
			}

			if(count == 0){
				Log.i("info", "---cpu mem--"+System.currentTimeMillis());
				mMyHandler.sendEmptyMessage(3);
				historyTime = System.currentTimeMillis();
			}
			// 存储数据，发消息到工作线程中存储数据
			long duration = System.currentTimeMillis() - historyTime;
			if (((timeSave*1000-500)<duration)&&(duration<(timeSave*1000+500))) {
//				Log.i("info", "---cpu mem--"+System.currentTimeMillis());
				mMyHandler.sendEmptyMessage(3);
				historyTime = System.currentTimeMillis();
			}

			handler.postDelayed(refreshData, 1000);
			count++;
			System.gc();
			// }
		}
	};

	private void RefreshUI_specail(String spc) {
		// tv_memory.setText(spc);
		// tv_cpu.setVisibility(View.GONE);
	}

	private void RefreshUI() {
		// // 判断是否充电中
		// if (util.isCharging()) {
		// battery = "充电中";
		// // log_battery = 0;
		// } else {
		// battery = "voltage" + util.getVoltage() + "mv";
		// // log_battery = util.getVoltage();
		// }
		//
		tv_cpu.setVisibility(View.VISIBLE);
		// // ll.setVisibility(View.VISIBLE);
		tv_memory.setText("memory" + bean.getpSS_MEM() + "\r");
		//
		// traffic_wifi = Long.parseLong(util.getTRAFFIC_WIFI());
		// traffic_3g = Long.parseLong(util.getTRAFFIC_3G());
		//
		// if (configutil.getFLOW_TYPE() == 1) {
		// // 显示总流量数
		// tv_traffic.setText("wifi:");
		// } else {
		// tv_traffic.setText("wifi" + util.transSpeed(traffic_wifi) + "\r"
		// + "3G" + util.transSpeed(traffic_3g));
		// }
		//
		tv_cpu.setText("cpu:" + bean.getProcessCpuRatio());
		// tv_battery.setText(battery);

	}

	private class MyHandler extends Handler {

		public MyHandler(Looper looper) {
			super(looper);
			try {
				writer = new CSVWriter(new FileWriter(file), ',');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			try {
				// 存储数据 1：耗电量 2.流量 3.cpu mem 4:测试开始记录设置参数以及测试开始时间5：结束时间
				int what = msg.what;
				switch (what) {
				case 1: {
					Log.i("info", "===耗电量===");

					int bat = bean.getStartVoltage() - bean.getEndVoltage();

					// 记录数据
					List<String[]> alList = new ArrayList<String[]>();
					List<String> list = new ArrayList<String>();
					list.add("" + 1);
					list.add("" + 0);
					list.add("" + bat);
					alList.add(list.toArray(new String[list.size()]));
					writer.writeAll(alList);
					writer.flush();

				}
					break;
				case 2: {
					Log.i("info", "===流量===");

					// "wifi:" + bean.getTRAFFIC_WIFI() + "----3g" +
					// bean.getTRAFFIC_3G()

					// 记录数据 4:wifi 5:3g
					List<String[]> alList = new ArrayList<String[]>();
					List<String> list = new ArrayList<String>();
					list.add("" + 4);
					list.add("" + 0);
					list.add("" + bean.getTRAFFIC_WIFI());
					alList.add(list.toArray(new String[list.size()]));
					writer.writeAll(alList);
					writer.flush();

					// 记录数据 4:wifi 5:3g
					List<String[]> alList1 = new ArrayList<String[]>();
					List<String> list1 = new ArrayList<String>();
					list1.add("" + 5);
					list1.add("" + 0);
					list1.add("" + bean.getTRAFFIC_3G());
					alList1.add(list1.toArray(new String[list1.size()]));
					writer.writeAll(alList1);
					writer.flush();

				}
					break;
				case 3: {
					Log.i("info", "=== cpu  mem===");
					// 3 cpu mem
					List<String[]> alList = new ArrayList<String[]>();
					List<String> list = new ArrayList<String>();
					list.add("" + 3);
					list.add("" + bean.getProcessCpuRatio());
					list.add("" + bean.getpSS_MEM());
					alList.add(list.toArray(new String[list.size()]));
					writer.writeAll(alList);
					writer.flush();

				}
					break;
				case 4:{
					
					
					// 记录cpu选项
					List<String[]> alList = new ArrayList<String[]>();
					List<String> list = new ArrayList<String>();
					list.add("" + 6);
					list.add("" + 0);
					if(TestToolNewApplication.isCPURecord){
						list.add("" + 1);
					}else{
						list.add("" + 0);
					}
					
					alList.add(list.toArray(new String[list.size()]));
					writer.writeAll(alList);
					writer.flush();
					
					
					
					// 记录mem选项
					List<String[]> alList1 = new ArrayList<String[]>();
					List<String> list1 = new ArrayList<String>();
					list1.add("" + 7);
					list1.add("" + 0);
					if(TestToolNewApplication.isMEMRecord){
						list1.add("" + 1);
					}else{
						list1.add("" + 0);
					}
					
					alList1.add(list1.toArray(new String[list1.size()]));
					writer.writeAll(alList1);
					writer.flush();
					
					
					
					// 记录bat选项
					List<String[]> alList2 = new ArrayList<String[]>();
					List<String> list2 = new ArrayList<String>();
					list2.add("" + 8);
					list2.add("" + 0);
					if(TestToolNewApplication.isBATRecord){
						list2.add("" + 1);
					}else{
						list2.add("" + 0);
					}
					
					alList2.add(list2.toArray(new String[list2.size()]));
					writer.writeAll(alList2);
					writer.flush();
					
					
					// 记录traf选项
					List<String[]> alList3 = new ArrayList<String[]>();
					List<String> list3 = new ArrayList<String>();
					list3.add("" + 9);
					list3.add("" + 0);
					if(TestToolNewApplication.isTRAFRecord){
						list3.add("" + 1);
					}else{
						list3.add("" + 0);
					}
					
					alList3.add(list3.toArray(new String[list3.size()]));
					writer.writeAll(alList3);
					writer.flush();
					
					
					//时间间隔
					List<String[]> alList4 = new ArrayList<String[]>();
					List<String> list4 = new ArrayList<String>();
					list4.add("" + 10);
					list4.add("" + 0);
					list4.add("" + timeSave);
					
					alList4.add(list4.toArray(new String[list4.size()]));
					writer.writeAll(alList4);
					writer.flush();
					
					//测试开始时间
					List<String[]> alList5 = new ArrayList<String[]>();
					List<String> list5 = new ArrayList<String>();
					list5.add("" + 11);
					list5.add("" + 0);
					list5.add("" + System.currentTimeMillis());
					
					alList5.add(list5.toArray(new String[list5.size()]));
					writer.writeAll(alList5);
					writer.flush();
					Log.i("info", "---测试开始--"+System.currentTimeMillis());
					
					
					
					
					
					
				}break;
				case 5:{
					//测试结束时间
					List<String[]> alList = new ArrayList<String[]>();
					List<String> list = new ArrayList<String>();
					list.add("" + 12);
					list.add("" + 0);
					list.add("" + System.currentTimeMillis());
					
					alList.add(list.toArray(new String[list.size()]));
					writer.writeAll(alList);
					writer.flush();
					Log.i("info", "---测试结束--"+System.currentTimeMillis());
					
				}break;
				
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
