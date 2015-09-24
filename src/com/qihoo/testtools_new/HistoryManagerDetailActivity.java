package com.qihoo.testtools_new;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.qihoo.testtools_new.adapter.HistoryManagerDetailAdapter;
import com.qihoo.testtools_new.thread.HistoryRecordThread;
import com.qihoo.testtools_new.view.MyChartView;
import com.qihoo.testtools_new.view.MyChartView.Mstyle;
import com.qihoo.testtools_new.view.TopBannerCursor;

public class HistoryManagerDetailActivity extends Activity {

	TopBannerCursor ivCursor;
	Button history_manager_detail_cpu, history_manager_detail_mem, history_manager_detail_tra,
			history_manager_detail_bat;
	ViewPager history_manager_detail__viewpager;
	TextView history_manager_back;

	HistoryManagerDetailAdapter adapter;

	ArrayList<View> data = new ArrayList<View>();
	int oldPosition;
	View v2;
	View v3;
	View v1;
	View v4;
	MyChartView tu;
	MyChartView line_mem;
	HashMap<Double, Double> map = new LinkedHashMap<Double, Double>();
	HashMap<Double, Double> mem_map = new LinkedHashMap<Double, Double>();
	String bat = "";
	String traf_wifi = "";
	String traf_3g = "";
	HistoryDetailReceiver receiver;

	private double cpu_max = 100.00;
	private double mem_max;
	String appname = "";
	String date = "";
	TextView tvBat;
	TextView tvTraf;
	String filename;
	int width;
	TextView history_recode_bat_duration,history_recode_traf_duration;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_manager_detail);

		Intent intent = getIntent();
		appname = intent.getStringExtra("appname");
		long intentDate = intent.getLongExtra("date", 0);
		filename = intent.getStringExtra("filename");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(intentDate);
		date = format.format(d);
		// 初始化视图
		initView();
		initData();
		// 初始化各种事件监听
		initListener();

		// 设置cursor的长度
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
//		ivCursor.setSize(width / 4, 4);

		// 注册广播
		receiver = new HistoryDetailReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_RECORD_DATA);
		registerReceiver(receiver, filter);

		HistoryRecordThread historyRecordThread = new HistoryRecordThread(filename);
		historyRecordThread.start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initView() {
		history_manager_back = (TextView) findViewById(R.id.history_manager_back);
		history_manager_detail_cpu = (Button) findViewById(R.id.history_manager_detail_cpu);
		history_manager_detail_mem = (Button) findViewById(R.id.history_manager_detail_mem);
		history_manager_detail_tra = (Button) findViewById(R.id.history_manager_detail_tra);
		history_manager_detail_bat = (Button) findViewById(R.id.history_manager_detail_bat);
		history_manager_detail__viewpager = (ViewPager) findViewById(R.id.history_manager_detail__viewpager);
		ivCursor = (TopBannerCursor) findViewById(R.id.ivCursor);
//		history_manager_detail_tra.setText("流量测试");
//		history_manager_detail_cpu.setText("CPU测试");
//		history_manager_detail_mem.setText("内存测试");
//		history_manager_detail_bat.setText("电量测试");
	}

	private void initData() {
		history_manager_back.setText("单次测试结果");

		LayoutInflater inflate = LayoutInflater.from(this);
		v1 = inflate.inflate(R.layout.history_detail1, null);
		v2 = inflate.inflate(R.layout.history_detail2, null);
		v3 = inflate.inflate(R.layout.history_detail3, null);
		v4 = inflate.inflate(R.layout.history_detail4, null);

		showView1Bat();
		showView2Line();
		showView3Line();
		showView4Traf();

//		data.add(v1);
//		data.add(v2);
//		data.add(v3);
//		data.add(v4);
		adapter = new HistoryManagerDetailAdapter(this, data);
		history_manager_detail__viewpager.setAdapter(adapter);
	}

	// 设置耗电量
	private void showView1Bat() {
		tvBat = (TextView) v1.findViewById(R.id.history_recode_bat_result);
		tvBat.setText("" + TestToolNewApplication.bat);
		TextView history_recode_bat_time = (TextView) v1.findViewById(R.id.history_recode_bat_time);
		history_recode_bat_time.setText("" + date);
		TextView history_recode_appname = (TextView) v1.findViewById(R.id.history_recode_bat_appname);
		history_recode_appname.setText("" + appname);
		
		history_recode_bat_duration = (TextView) v1.findViewById(R.id.history_recode_bat_duration);
	}

	// 设置流量
	private void showView4Traf() {
		tvTraf = (TextView) v4.findViewById(R.id.history_recode_traf_result);
		// tvTraf.setText("wifi: " + traf_wifi + " 3g: " + traf_3g);
		TextView history_recode_traf_time = (TextView) v4.findViewById(R.id.history_recode_traf_time);
		history_recode_traf_time.setText("" + date);

		TextView history_recode_traf_appname = (TextView) v4.findViewById(R.id.history_recode_traf_appname);
		history_recode_traf_appname.setText("" + appname);
		history_recode_traf_duration = (TextView) v4.findViewById(R.id.history_recode_traf_duration);
	}

	// 设置V2的折线图
	private void showView2Line() {

		tu = (MyChartView) v2.findViewById(R.id.menulist);
		tu.SetTuView(map, HistoryManagerDetailActivity.this.cpu_max, 1.0, "s", "%", false);

		// map.put(1.0, (double) 0);
		// map.put(3.0, 25.0);
		// map.put(4.0, 32.0);
		// map.put(5.0, 41.0);
		// map.put(6.0, 16.0);
		// map.put(7.0, 36.0);
		// map.put(8.0, 26.0);
		// map.put(9.0, 26.0);
		// map.put(10.0, 22.0);
		// map.put(11.0, 25.0);
		// map.put(12.0, 5.0);
		// map.put(13.0, 35.0);
		// map.put(14.0, 15.0);
		tu.setTotalvalue(HistoryManagerDetailActivity.this.cpu_max);
		double i = HistoryManagerDetailActivity.this.cpu_max / 10;
		tu.setPjvalue(i);
		tu.setMap(map);
		// tu.setXstr("");
		// tu.setYstr("");
		tu.setMargint(20);
		tu.setMarginb(50);
		tu.setMstyle(Mstyle.Line);
	}

	// 设置V3的折线图
	private void showView3Line() {

		line_mem = (MyChartView) v3.findViewById(R.id.history_recode_memery);
		line_mem.SetTuView(mem_map, HistoryManagerDetailActivity.this.mem_max, 10, "s", "MB", false);
		// mem_map.put(1.0, (double) 0);
		// mem_map.put(3.0, 25.0);
		// mem_map.put(4.0, 32.0);
		// mem_map.put(5.0, 41.0);
		// mem_map.put(6.0, 16.0);
		// mem_map.put(7.0, 36.0);
		// mem_map.put(8.0, 26.0);
		// mem_map.put(9.0, 26.0);
		// mem_map.put(10.0, 22.0);
		// mem_map.put(11.0, 25.0);
		// mem_map.put(12.0, 5.0);
		// mem_map.put(13.0, 35.0);
		// mem_map.put(14.0, 15.0);
		line_mem.setTotalvalue(HistoryManagerDetailActivity.this.mem_max);
		double i = HistoryManagerDetailActivity.this.mem_max / 8;
		line_mem.setPjvalue(i);
		line_mem.setMap(mem_map);
		// tu.setXstr("");
		// tu.setYstr("");
		line_mem.setMargint(20);
		line_mem.setMarginb(50);
		line_mem.setMstyle(Mstyle.Line);
	}

	private void initListener() {

		history_manager_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HistoryManagerDetailActivity.this.finish();

			}
		});

		// 当某一项被选中时，需要改变top_banner以及cursor
		history_manager_detail__viewpager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				topBannerchanged(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});

		history_manager_detail_cpu.setOnClickListener(bannerCilckListener);
		history_manager_detail_mem.setOnClickListener(bannerCilckListener);
		history_manager_detail_bat.setOnClickListener(bannerCilckListener);
		history_manager_detail_tra.setOnClickListener(bannerCilckListener);

	}

	OnClickListener bannerCilckListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			// 当点击某一项时，top_banner的变化和滑动viewpager是一样的，同时需要改变viewpager
			if (view == history_manager_detail_bat) {
				topBannerchanged(0);
				history_manager_detail__viewpager.setCurrentItem(0);
			} else if (view == history_manager_detail_cpu) {
				topBannerchanged(1);
				history_manager_detail__viewpager.setCurrentItem(1);
			} else if (view == history_manager_detail_mem) {
				topBannerchanged(2);
				history_manager_detail__viewpager.setCurrentItem(2);
			} else if (view == history_manager_detail_tra) {
				topBannerchanged(3);
				history_manager_detail__viewpager.setCurrentItem(3);
			}

		}
	};

	protected void topBannerchanged(final int position) {
		// 移动cursor
		int fromX = oldPosition * ivCursor.getWidth();
		int toX = position * ivCursor.getWidth();
		TranslateAnimation anim = new TranslateAnimation(fromX, toX, 0, 0);
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

				// 改变top_banner的图标
				// ibCallLog.setImageResource(R.drawable.top_banner_call_05);
				// ibContact.setImageResource(R.drawable.top_banner_contact_05);
				// ibMessage.setImageResource(R.drawable.top_banner_message_05);
				// ibSetting.setImageResource(R.drawable.top_banner_setting);
				history_manager_detail_cpu.setTextColor(Color.parseColor("#878787"));
				history_manager_detail_mem.setTextColor(Color.parseColor("#878787"));
				history_manager_detail_bat.setTextColor(Color.parseColor("#878787"));
				history_manager_detail_tra.setTextColor(Color.parseColor("#878787"));
				switch (position) {
				case 0: {
					history_manager_detail_bat.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;
				case 1: {
					history_manager_detail_cpu.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;
				case 2: {
					history_manager_detail_mem.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;
				case 3: {
					history_manager_detail_tra.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;
				}

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
			}
		});

		ivCursor.startAnimation(anim);
		oldPosition = position;

	}

	// 注册广播接收器
	class HistoryDetailReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_RECORD_DATA.equals(action)) {
				Log.i("info", "更新单次记录的折线图");
				// 更新数据和界面
				HashMap<Double, Double> data_map = TestToolNewApplication.map;
				HashMap<Double, Double> data_mem_map = TestToolNewApplication.mem_map;
				HistoryManagerDetailActivity.this.traf_3g = TestToolNewApplication.traf_3g;
				HistoryManagerDetailActivity.this.traf_wifi = TestToolNewApplication.traf_wifi;
				HistoryManagerDetailActivity.this.bat = TestToolNewApplication.bat;

				// 数据筛选
				HistoryManagerDetailActivity.this.map = dataFilter(data_map);
				HistoryManagerDetailActivity.this.mem_map = dataFilter(data_mem_map);
				// 取最大值
				// HistoryManagerDetailActivity.this.cpu_max =
				// dataMax(HistoryManagerDetailActivity.this.map);
				HistoryManagerDetailActivity.this.mem_max = dataMax(HistoryManagerDetailActivity.this.mem_map) * 2;
				long duration = (TestToolNewApplication.testEndTime - TestToolNewApplication.testStartTime)/1000;
				
				// showView1Bat();
				// tvBat.setText("" + TestToolNewApplication.bat);
				// showView2Line();
				// showView3Line();
				// showView4Traf();
				// tvTraf.setText("wifi: " + TestToolNewApplication.traf_wifi +
				// " 3g: " + TestToolNewApplication.traf_3g);

				int count = 0;
				ArrayList<View> dataView = new ArrayList<View>();

				if (TestToolNewApplication.isBATRecord) {
					history_manager_detail_bat.setTag(count);
					count++;
					

				}
				if (TestToolNewApplication.isCPURecord) {
					history_manager_detail_cpu.setTag(count);
					count++;
				}
				if (TestToolNewApplication.isMEMRecord) {
					history_manager_detail_mem.setTag(count);
					count++;
				}
				if (TestToolNewApplication.isTRAFRecord) {
					history_manager_detail_tra.setTag(count);
					count++;
				}

				if (count > 0) {
					// 指针
					ivCursor.setSize(width / count, 4);

					if (TestToolNewApplication.isBATRecord) {
						history_manager_detail_bat.setWidth(width / count);
						history_manager_detail_bat.setText("电量测试");
						tvBat.setText("" + TestToolNewApplication.bat);
						
						history_recode_bat_duration.setText(""+duration);
						dataView.add(v1);

					}
					if (TestToolNewApplication.isCPURecord) {

						history_manager_detail_cpu.setWidth(width / count);
						history_manager_detail_cpu.setText("CPU测试");
						showView2Line();
						dataView.add(v2);
					}
					if (TestToolNewApplication.isMEMRecord) {

						history_manager_detail_mem.setWidth(width / count);
						history_manager_detail_mem.setText("内存测试");
						showView3Line();
						dataView.add(v3);
					}
					if (TestToolNewApplication.isTRAFRecord) {

						history_manager_detail_tra.setWidth(width / count);
						history_manager_detail_tra.setText("流量测试");
						tvTraf.setText(
								"wifi: " + TestToolNewApplication.traf_wifi + " 3g: " + TestToolNewApplication.traf_3g);
						history_recode_traf_duration.setText(""+duration);
						dataView.add(v4);
					}
					adapter.setData(dataView);
					adapter.notifyDataSetChanged();
				}
				// 根据测试项进行选择显示
				// ArrayList<View> dataView = new ArrayList<View>();
				// dataView.add(v1);
				// dataView.add(v2);
				// dataView.add(v3);
				// adapter.setData(dataView);
				// adapter.notifyDataSetChanged();
				// history_manager_detail_tra.setWidth(0);
				// history_manager_detail_tra.setText("");
				// ivCursor.setSize(width / 3, 4);
				// history_manager_detail_bat.setWidth(width / 3);
				// history_manager_detail_cpu.setWidth(width / 3);
				// history_manager_detail_mem.setWidth(width / 3);
			}

		}

	}

	public HashMap<Double, Double> dataFilter(HashMap<Double, Double> data_map) {
		int count = data_map.size();

		if (count > 50) {
			int m = (int) Math.ceil(count / 50.0);
			LinkedHashMap<Double, Double> map1 = new LinkedHashMap<Double, Double>();

			// for (int i = 0; i < count; i = i + m) {
			// double x = i + 0.0;
			// double y = data_map.get(x);
			// map1.put(x, y);
			// }

			int n = 0;
			Iterator iter = data_map.keySet().iterator();
			while (iter.hasNext()) {

				double key = (Double) iter.next();
				double val = data_map.get(key);
				if (n % m == 0) {
					map1.put(key, val);
				}
				n++;

			}

			return map1;
			// Log.i("info", "===========");
		} else {
			return data_map;
		}

	}

	public double dataMax(HashMap<Double, Double> data_map) {
		double max = 0;
		Iterator iter = data_map.keySet().iterator();
		while (iter.hasNext()) {
			double key = (Double) iter.next();
			double val = data_map.get(key);
			if (max < val) {
				max = val;
			}

		}
		if (max == 0) {
			max = 10;
		}
		// if (data_map.size() < 2) {
		// max = 10;
		// }
		return max;
	}

}
