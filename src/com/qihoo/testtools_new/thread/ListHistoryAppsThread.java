package com.qihoo.testtools_new.thread;

import android.content.Context;
import android.content.Intent;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.utils.ApplicationsDataUtils;

public class ListHistoryAppsThread extends Thread {

	@Override
	public void run() {
		super.run();

		// ������������

		TestToolNewApplication.historyAppsList = ApplicationsDataUtils
				.getHistoryAppsDataList(TestToolNewApplication.mySelf);

		// ���͹㲥�������ݺͽ���
		Intent intent = new Intent();
		intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST);
		TestToolNewApplication.mySelf.sendStickyBroadcast(intent);
	}

}
