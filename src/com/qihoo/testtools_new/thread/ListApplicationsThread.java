package com.qihoo.testtools_new.thread;

import android.content.Context;
import android.content.Intent;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.utils.ApplicationsDataUtils;

public class ListApplicationsThread extends Thread {
	

	@Override
	public void run() {
		super.run();

		// 加载所有数据
		TestToolNewApplication.appsList = ApplicationsDataUtils

		.getApplicationsDataList(TestToolNewApplication.mySelf);

		// 发送广播更新数据和界面
		Intent intent = new Intent();
		intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_APPS_LIST);
		TestToolNewApplication.mySelf.sendStickyBroadcast(intent);
	}

}
