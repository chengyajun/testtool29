package com.qihoo.testtools_new;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.qihoo.testtools_new.bean.ApplicationBean;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.ListApplicationsThread;
import com.qihoo.testtools_new.thread.ListHistoryAppsThread;

public class TestToolNewApplication extends Application {

	public static TestToolNewApplication mySelf;
	// ���ݴ���
	public static List<ApplicationBean> appsList;
	public static List<HistoryBean> historyAppsList;
	public static List<HistoryBean> historyDetailApplicationList;
	public static List<HistoryBean> historyRecodeDataList;
	
	
	
	
	//������
	public static boolean isCPURecord = false;
	public static boolean isMEMRecord = false;
	public static boolean isBATRecord = false;
	public static boolean isTRAFRecord = false;
	
	//����ʱ��
	public static long testStartTime = 0;
	public static long testEndTime = 0;
	
	public static String bat = "";
	public static String traf_wifi = "";
	public static String traf_3g = "";
	public static HashMap<Double, Double> map = new LinkedHashMap<Double, Double>();
	public static HashMap<Double, Double> mem_map = new LinkedHashMap<Double, Double>();

	public static HashMap<Double, Double> historyCpuAll = new LinkedHashMap<Double, Double>();
	public static HashMap<Double, Double> historyMemAll = new LinkedHashMap<Double, Double>();

	// �㲥
	public static String INTENT_ACTION_BROADCAST_APPS_LIST = "com.qihoo.testtools.new.apps.list";
	public static String INTENT_ACTION_BROADCAST_FLOAT_VIEW_STOP = "com.qihoo.testtools.new.float.view.stop";
	public static String INTENT_ACTION_BROADCAST_HISTORY_RECORD_DATA = "com.qihoo.testtools.new.history.record.data";
	public static String INTENT_ACTION_BROADCAST_ALL_RECORD_DATA = "com.qihoo.testtools.new.all.record.data";
	public static String INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST = "com.qihoo.testtools.new.history.apps.list";
	public static String INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST = "com.qihoo.testtools.new.history.apps.detail.list";
	/** �洢���ļ��� */
	public static final String fileName = "settingfile";
	/** �洢����ļ�·����/data/data/<package name>/shares_prefs + �ļ���.xml */
	public static final String PATH = "/data/data/com.qihoo.testtools_new/shared_prefs/settingfile.xml";

	@Override
	public void onCreate() {
		super.onCreate();

		mySelf = this;

		appsList = new ArrayList<ApplicationBean>();
		historyAppsList = new ArrayList<HistoryBean>();
		historyDetailApplicationList = new ArrayList<HistoryBean>();
		historyRecodeDataList = new ArrayList<HistoryBean>();

		// ����SharedPreferences�ļ��������洢������Ϣ

		// �ж��ļ��Ƿ���ڣ�����������򴴽�
		File file = new File(PATH);
		if (!file.exists()) {
			SharedPreferences preferences = getSharedPreferences(fileName,
					Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putInt("time", 3);
			editor.putBoolean("isWindowOpen", true);
			editor.commit();
		}

		// �����̼߳����ֻ������е�Ӧ��
		ListApplicationsThread appsThread = new ListApplicationsThread();
		appsThread.start();

		// �����̼߳����ֻ��еĲ��Լ�¼�ļ�
		ListHistoryAppsThread historyAppsThread = new ListHistoryAppsThread();
		historyAppsThread.start();

	}

}
