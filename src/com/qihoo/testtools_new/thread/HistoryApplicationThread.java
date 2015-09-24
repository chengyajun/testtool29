package com.qihoo.testtools_new.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.utils.ApplicationsDataUtils;
import com.qihoo.testtools_new.utils.HistoryAppDataUtils;

public class HistoryApplicationThread extends Thread {

	private String packageName;
	private String appname;

	public HistoryApplicationThread(String packagename,String appname) {
		this.packageName = packagename;
		this.appname = appname;
	}

	@Override
	public void run() {
		super.run();

		// 加载该app的所有数据

//		 TestToolNewApplication.historyApplicationList = ApplicationsDataUtils
//		 .getHistoryAppsDataList(TestToolNewApplication.mySelf);
		
		
//		Log.i("info",packageName);
		
		TestToolNewApplication.historyDetailApplicationList = HistoryAppDataUtils.getHistoryList(packageName,appname);

		// 发送广播更新数据和界面
		Intent intent = new Intent();
		intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST);
		TestToolNewApplication.mySelf.sendStickyBroadcast(intent);
	}

}
