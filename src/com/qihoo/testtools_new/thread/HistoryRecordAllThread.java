package com.qihoo.testtools_new.thread;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.utils.ApplicationsDataUtils;
import com.qihoo.testtools_new.utils.HistoryAppDataUtils;

public class HistoryRecordAllThread extends Thread {

	private ArrayList<HistoryBean> historyRecordAll;
	private Handler handler;

	public HistoryRecordAllThread(List<HistoryBean> historyDetailApplicationList, Handler mHandler) {
		this.historyRecordAll = (ArrayList<HistoryBean>) historyDetailApplicationList;
		this.handler = mHandler;
	}

	@Override
	public void run() {
		super.run();

		// ���ظ��ļ��е�����

		
		
		
		HistoryAppDataUtils.getFileDataAll(historyRecordAll);
		handler.sendEmptyMessage(0);

		// ApplicationsDataUtils
		// .getHistoryAppsDataList(TestToolNewApplication.mySelf);

//		// ���͹㲥�������ݺͽ���
//		Intent intent = new Intent();
////		intent.setAction("testtool.new.test.data.test.data");
//		TestToolNewApplication.mySelf.sendStickyBroadcast(intent);
	}

}
