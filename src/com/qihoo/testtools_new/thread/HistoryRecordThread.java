package com.qihoo.testtools_new.thread;

import android.content.Context;
import android.content.Intent;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.utils.ApplicationsDataUtils;
import com.qihoo.testtools_new.utils.HistoryAppDataUtils;

public class HistoryRecordThread extends Thread {
	
	private String filename;

	public HistoryRecordThread(String fileName) {
		this.filename = fileName;
	}

	@Override
	public void run() {
		super.run();

		// ���ظ��ļ��е�����
		
		HistoryAppDataUtils.getFileData(filename);

//		ApplicationsDataUtils
//				.getHistoryAppsDataList(TestToolNewApplication.mySelf);

		// ���͹㲥�������ݺͽ���
		Intent intent = new Intent();
		intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_RECORD_DATA);
		TestToolNewApplication.mySelf.sendStickyBroadcast(intent);
	}

}
