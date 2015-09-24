package com.qihoo.testtools_new;

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
import android.os.Handler;
import android.os.Message;
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
import com.qihoo.testtools_new.thread.HistoryRecordAllThread;
import com.qihoo.testtools_new.view.MyChartView;
import com.qihoo.testtools_new.view.MyChartView.Mstyle;
import com.qihoo.testtools_new.view.TopBannerCursor;

public class HistoryManagerDetailAllActivity extends Activity {

	TopBannerCursor ivCursor;
	Button history_manager_detail_all_result, history_manager_detail_all_cpu,
			history_manager_detail_all_mem;
	ViewPager history_manager_detail__viewpager;
	TextView history_recode_all_back;
	TextView history_recode_all_count;

	HistoryManagerDetailAdapter adapter;

	ArrayList<View> data = new ArrayList<View>();
	int oldPosition;
	View v1;
	View v2;
	View v3;

	MyChartView tu;
	MyChartView line_mem;
	HashMap<Double, Double> cpu_map = new LinkedHashMap<Double, Double>();
	HashMap<Double, Double> mem_map = new LinkedHashMap<Double, Double>();
	// String bat = "";
	// String traf_wifi = "";
	// String traf_3g = "";
	// HistoryDetailAllReceiver receiver;

	private double cpu_max=100.00;
	private double mem_max;
	Handler mHandler;
	String appname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_manager_detail_all);

		Intent intent = getIntent();
		appname = intent.getStringExtra("appname");

		// ��ʼ����ͼ
		initView();
		initData();
		// ��ʼ�������¼�����
		initListener();

		// ����cursor�ĳ���
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // ��Ļ��ȣ����أ�
		ivCursor.setSize(width / 3, 4);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// ��������

				Log.i("info", "�������м�¼������ͼ");
				// �������ݺͽ���
				HashMap<Double, Double> cpu_all = TestToolNewApplication.historyCpuAll;
				HashMap<Double, Double> mem_all = TestToolNewApplication.historyMemAll;

				// ����ɸѡ
				HistoryManagerDetailAllActivity.this.cpu_map = dataFilter(cpu_all);
				HistoryManagerDetailAllActivity.this.mem_map = dataFilter(mem_all);
				// ȡ���ֵ
//				HistoryManagerDetailAllActivity.this.cpu_max = dataMax(HistoryManagerDetailAllActivity.this.cpu_map);
				HistoryManagerDetailAllActivity.this.mem_max = dataMax(HistoryManagerDetailAllActivity.this.mem_map)*2;

				Log.i("info", "==============cpu_max====" + cpu_max);
				Log.i("info", "==============mem_max====" + mem_max);
				history_recode_all_count.setText("" + cpu_map.size());

				showView2Line();
				showView3Line();
			}
		};
		// �����̼߳�������
		HistoryRecordAllThread historyRecordThread = new HistoryRecordAllThread(
				TestToolNewApplication.historyRecodeDataList, mHandler);
		historyRecordThread.start();

		// // // ע��㲥
		// receiver = new HistoryDetailAllReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction("testtool.new.test.data.test.data");
		// registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// unregisterReceiver(receiver);
	}

	private void initView() {
		history_recode_all_back = (TextView) findViewById(R.id.history_recode_all_back);
		history_manager_detail_all_result = (Button) findViewById(R.id.history_manager_detail_all_result);
		history_manager_detail_all_cpu = (Button) findViewById(R.id.history_manager_detail_all_cpu);
		history_manager_detail_all_mem = (Button) findViewById(R.id.history_manager_detail_all_mem);
		history_manager_detail__viewpager = (ViewPager) findViewById(R.id.history_manager_detail__viewpager);
		ivCursor = (TopBannerCursor) findViewById(R.id.ivCursor);
	}

	private void initData() {

		LayoutInflater inflate = LayoutInflater.from(this);
		v1 = inflate.inflate(R.layout.history_detail5, null);
		v2 = inflate.inflate(R.layout.history_detail2, null);
		v3 = inflate.inflate(R.layout.history_detail3, null);

		showView1();
		showView2Line();
		showView3Line();
		// showView4Traf();

		data.add(v1);
		data.add(v2);
		data.add(v3);
		// data.add(v3);

		adapter = new HistoryManagerDetailAdapter(this, data);
		history_manager_detail__viewpager.setAdapter(adapter);
	}

	// ���úĵ���
	// private void showView1Bat() {
	// TextView tvBat = (TextView) v1
	// .findViewById(R.id.history_recode_bat_result);
	// tvBat.setText("" + bat);
	// }

	// ��������
	// private void showView4Traf() {
	// TextView tvTraf = (TextView) v4
	// .findViewById(R.id.history_recode_traf_result);
	// tvTraf.setText("wifi: " + traf_wifi + "  3g: " + traf_3g);
	// }

	private void showView1() {
		TextView history_recode_all_appname = (TextView) v1
				.findViewById(R.id.history_recode_all_appname);
		history_recode_all_appname.setText("" + appname);
		history_recode_all_count = (TextView) v1
				.findViewById(R.id.history_recode_all_count);
		history_recode_all_count.setText("0");
	}

	// ����V2������ͼ
	private void showView2Line() {

		// tu = (MyChartView) v2.findViewById(R.id.menulist);
		// tu.SetTuView(cpu_map, HistoryManagerDetailAllActivity.this.cpu_max,
		// 1.0, "x", "y", false);
		//
		// // map.put(1.0, (double) 0);
		// // map.put(3.0, 25.0);
		// // map.put(4.0, 32.0);
		// // map.put(5.0, 41.0);
		// // map.put(6.0, 16.0);
		// // map.put(7.0, 36.0);
		// // map.put(8.0, 26.0);
		// // map.put(9.0, 26.0);
		// // map.put(10.0, 22.0);
		// // map.put(11.0, 25.0);
		// // map.put(12.0, 5.0);
		// // map.put(13.0, 35.0);
		// // map.put(14.0, 15.0);
		// // map = dataFilter(map);
		// // HistoryManagerDetailAllActivity.this.cpu_max= dataMax(map);
		// tu.setTotalvalue(HistoryManagerDetailAllActivity.this.cpu_max);
		// double i = HistoryManagerDetailAllActivity.this.cpu_max / 8;
		// tu.setPjvalue(i);
		// tu.setMap(cpu_map);
		// // tu.setXstr("");
		// // tu.setYstr("");
		// tu.setMargint(20);
		// tu.setMarginb(50);
		// tu.setMstyle(Mstyle.Line);

		tu = (MyChartView) v2.findViewById(R.id.menulist);
		tu.SetTuView(cpu_map, HistoryManagerDetailAllActivity.this.cpu_max, 10,
				"", "%", false);
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
		tu.setTotalvalue(HistoryManagerDetailAllActivity.this.cpu_max);
		double i = HistoryManagerDetailAllActivity.this.cpu_max / 10;
		tu.setPjvalue(i);
		tu.setMap(cpu_map);
		// tu.setXstr("");
		// tu.setYstr("");
		tu.setMargint(20);
		tu.setMarginb(50);
		tu.setMstyle(Mstyle.Line);
	}

	// ����V3������ͼ
	private void showView3Line() {

		line_mem = (MyChartView) v3.findViewById(R.id.history_recode_memery);
		line_mem.SetTuView(mem_map,
				HistoryManagerDetailAllActivity.this.mem_max, 10, "", "MB",
				false);
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
		line_mem.setTotalvalue(HistoryManagerDetailAllActivity.this.mem_max);
		double i = HistoryManagerDetailAllActivity.this.mem_max / 8;
		line_mem.setPjvalue(i);
		line_mem.setMap(mem_map);
		// tu.setXstr("");
		// tu.setYstr("");
		line_mem.setMargint(20);
		line_mem.setMarginb(50);
		line_mem.setMstyle(Mstyle.Line);
	}

	private void initListener() {

		history_recode_all_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HistoryManagerDetailAllActivity.this.finish();
			}
		});
		// ��ĳһ�ѡ��ʱ����Ҫ�ı�top_banner�Լ�cursor
		history_manager_detail__viewpager
				.addOnPageChangeListener(new OnPageChangeListener() {

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

		history_manager_detail_all_cpu.setOnClickListener(bannerCilckListener);
		history_manager_detail_all_mem.setOnClickListener(bannerCilckListener);
		history_manager_detail_all_result
				.setOnClickListener(bannerCilckListener);
	}

	OnClickListener bannerCilckListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			// �����ĳһ��ʱ��top_banner�ı仯�ͻ���viewpager��һ���ģ�ͬʱ��Ҫ�ı�viewpager
			if (view == history_manager_detail_all_cpu) {
				topBannerchanged(1);
				history_manager_detail__viewpager.setCurrentItem(1);
			} else if (view == history_manager_detail_all_mem) {
				topBannerchanged(2);
				history_manager_detail__viewpager.setCurrentItem(2);
			} else if (view == history_manager_detail_all_result) {
				topBannerchanged(0);
				history_manager_detail__viewpager.setCurrentItem(0);
			}

		}
	};

	protected void topBannerchanged(final int position) {
		// �ƶ�cursor
		int fromX = oldPosition * ivCursor.getWidth();
		int toX = position * ivCursor.getWidth();
		TranslateAnimation anim = new TranslateAnimation(fromX, toX, 0, 0);
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

				// �ı�top_banner��ͼ��
				// ibCallLog.setImageResource(R.drawable.top_banner_call_05);
				// ibContact.setImageResource(R.drawable.top_banner_contact_05);
				// ibMessage.setImageResource(R.drawable.top_banner_message_05);
				// ibSetting.setImageResource(R.drawable.top_banner_setting);
				history_manager_detail_all_cpu.setTextColor(Color.parseColor("#878787"));
				history_manager_detail_all_mem.setTextColor(Color.parseColor("#878787"));
				switch (position) {
				case 0: {
					history_manager_detail_all_result.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;

				case 1: {
					history_manager_detail_all_cpu.setTextColor(Color.parseColor("#3a97ee"));
				}
					break;
				case 2: {
					history_manager_detail_all_mem.setTextColor(Color.parseColor("#3a97ee"));
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

	// ע��㲥������
	// class HistoryDetailAllReceiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if ("testtool.new.test.data.test.data".equals(action)) {
	// Log.i("info", "�������м�¼������ͼ"+action);
	// // �������ݺͽ���
	// HashMap<Double, Double> cpu_all = TestToolNewApplication.historyCpuAll;
	// HashMap<Double, Double> mem_all = TestToolNewApplication.historyMemAll;
	//
	// // ����ɸѡ
	// HistoryManagerDetailAllActivity.this.cpu_map = dataFilter(cpu_all);
	// HistoryManagerDetailAllActivity.this.mem_map = dataFilter(mem_all);
	// // ȡ���ֵ
	// HistoryManagerDetailAllActivity.this.cpu_max =
	// dataMax(HistoryManagerDetailAllActivity.this.cpu_map);
	// HistoryManagerDetailAllActivity.this.mem_max =
	// dataMax(HistoryManagerDetailAllActivity.this.mem_map);
	//
	// Log.i("info", "==============cpuall====" + cpu_map.size());
	// Log.i("info", "==============cpumax====" + cpu_max);
	// showView3Line();
	// showView2Line();
	//
	// }
	//
	// }
	//
	// }

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
//		if (data_map.size() < 2) {
//			max = 10;
//		}
		return max;
	}

}
