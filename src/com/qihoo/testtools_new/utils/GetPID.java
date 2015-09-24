package com.qihoo.testtools_new.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;


public class GetPID {

	public ActivityManager activitymanager;
//	private Util util = Util.getUtil();
	
	public int getPid(Context context, String process) {

		activitymanager = (ActivityManager) context
				.getSystemService(Service.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> appProcessList = activitymanager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo appProcessInfo : appProcessList) {
			int pid = appProcessInfo.pid;
			String processName = appProcessInfo.processName;
			if (processName.equals(process)) {
//				util.setPID(pid);
				return pid;
			}
		}
//		util.setPID(-1);
		return -1;
	}
}
