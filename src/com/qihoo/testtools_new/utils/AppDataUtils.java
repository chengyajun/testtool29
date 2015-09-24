package com.qihoo.testtools_new.utils;

import java.text.NumberFormat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.ApplicationBean;

public class AppDataUtils {

	public int position;
	public Context context;
	public ApplicationBean bean;

	GetMemory getmemory;
	GetCpu getCPU;
	GetTraffic getTraffic;
	boolean charging;
	
	private long traffic_wifi = 0;
	private long traffic_3g = 0;

	private BroadcastReceiver MyBatteryReceiver;

	public AppDataUtils(Context context, int position) {
		this.position = position;
		this.context = context;

		bean = TestToolNewApplication.appsList.get(position);

		// 获取pid
		GetPID getPID = new GetPID();
		int pid = getPID.getPid(context, bean.getPackagename());
		bean.setPID(pid);

		// mem
		getmemory = new GetMemory(context);

		// CPU
		getCPU = new GetCpu();
		// traffic
		getTraffic = new GetTraffic(context, bean.getPackagename());

	}

	public void gettestData() {
		// 获取测试数据，放到对象中去

		// 获取Mem
		int mem = getMemoryData();
		String memery = transSize(mem);

		// CPU
		String cpu = getCPUData();

		bean.setpSS_MEM(memery);
		bean.setProcessCpuRatio(cpu);

	}

	// 内存数据获取
	public int getMemoryData() {
		int pid = bean.getPID();
		if (pid != -1) {
			int pids[] = new int[1];
			pids[0] = pid;
			int pss = getmemory.getPss(pids);
			int privateDirty = getmemory.getPrivateDirty(pids);
			int sharedirty = getmemory.getShareDirty(pids);

			// util.setPSS_MEM(pss);
			// util.setPrivateDirty(privateDirty);
			// util.setShareDirty(sharedirty);
			return pss;
		} else {
			return 0;
		}
	}

	// 内存数据转换
	public String transSize(long originalSize) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		if (originalSize / 1024 == 0) {
			return originalSize + "KB";
		} else if (originalSize / 1048576 == 0) {
			return numberFormat.format((float) originalSize / 1024) + "MB";
		} else {
			return numberFormat.format((float) originalSize / 1048576) + "GB";
		}
	}

	// CPU数据获取
	public String getCPUData() {
		// getCPU.getCpuRatioInfo(util.getPID());

		String cpu = getCPU.getCpuRatioInfo(bean.getPID());
		return cpu;
	}

	// 数据流量
	public int getTraffic() {
		
		int pid = bean.getPID();
		
		if (pid != -1) {
			getTraffic.GetFluent();

			traffic_wifi = getTraffic.wifi.total;
			traffic_3g = getTraffic.mobile.total;
//			traffic_time = System.currentTimeMillis();
//			util.setTRAFFIC_3G(String.valueOf(traffic_3g));
//			util.setTRAFFIC_WIFI(String.valueOf(traffic_wifi));
			
			String traf_wifi = transTraffic(traffic_wifi);
			String traf_3g = transTraffic(traffic_3g);
			bean.setTRAFFIC_3G(traf_3g);
			bean.setTRAFFIC_WIFI(traf_wifi);
			//0为网速，1为总量
//			if (configutil.getFLOW_TYPE() == 1) {
//				util.setTRAFFIC_3G(String.valueOf(traffic_3g));
//				util.setTRAFFIC_WIFI(String.valueOf(traffic_wifi));
//			} else {
//				if (time_tmp == 0) {
//
//					util.setTRAFFIC_WIFI(String
//							.valueOf((traffic_wifi - traffic_temp_wifi) * 1000
//									/ configutil.getDELAY()));
//					util.setTRAFFIC_3G(String
//							.valueOf((traffic_3g - traffic_temp_3g) * 1000
//									/ configutil.getDELAY()));
//
//				} else {
//					util.setTRAFFIC_WIFI(String
//							.valueOf((traffic_wifi - traffic_temp_wifi) * 1000
//									/ (traffic_time - time_tmp)));
//			
//
//					util.setTRAFFIC_3G(String
//							.valueOf((traffic_3g - traffic_temp_3g) * 1000
//									/ (traffic_time - time_tmp)));
//				}
//				traffic_temp_wifi = traffic_wifi;
//				traffic_temp_3g = traffic_3g;
//				time_tmp = traffic_time;
//			}

			// util.setTRAFFIC_3G(String.valueOf(getTraffic.mobile.total));
			// util.setTRAFFIC_WIFI(String.valueOf(getTraffic.wifi.total));
			return 0;
		}
		return -1;
	}
	
	public String transTraffic(long originalSize) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		if (originalSize / 1024 == 0) {
			return originalSize + "KB";
		} else if (originalSize / 1048576 == 0) {
			return numberFormat.format((float) originalSize / 1024) + "MB";
		} else {
			return numberFormat.format((float) originalSize / 1048576) + "GB";
		}
	}
	
	

	// 获取电量
	public void GetBatteryStatus() {

		MyBatteryReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, final Intent intent) {
				int voltage = intent.getIntExtra("voltage", -1);
				int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
						-1);

				charging = (status == BatteryManager.BATTERY_PLUGGED_AC || status == BatteryManager.BATTERY_PLUGGED_USB);

				// util.setVoltage(voltage);
				if (!charging) {
					
					if (bean.getStartVoltage() == -1) {// 记录开始电量
						bean.setStartVoltage(voltage);
					} else {
						bean.setEndVoltage(voltage);
					}
				}

			}
		};
		IntentFilter batteryFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(MyBatteryReceiver, batteryFilter);
	}

	public void stopGetBatteryStatus() {
		int bat = bean.getStartVoltage() - bean.getEndVoltage();
//		Log.i("info", bean.getStartVoltage()+"-"+ bean.getStartVoltage()+"="+bat);
//		Toast.makeText(context, bean.getStartVoltage()+"-"+ bean.getStartVoltage()+"="+bat, 20000).show();
		context.unregisterReceiver(MyBatteryReceiver);
	}
	
}
