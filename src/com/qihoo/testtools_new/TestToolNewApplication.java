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
	// 数据储存
	public static List<ApplicationBean> appsList;
	public static List<HistoryBean> historyAppsList;
	public static List<HistoryBean> historyDetailApplicationList;
	public static List<HistoryBean> historyRecodeDataList;
	
	
	
	
	//测试项
	public static boolean isCPURecord = false;
	public static boolean isMEMRecord = false;
	public static boolean isBATRecord = false;
	public static boolean isTRAFRecord = false;
	
	//测试时长
	public static long testStartTime = 0;
	public static long testEndTime = 0;
	
	public static String bat = "";
	public static String traf_wifi = "";
	public static String traf_3g = "";
	public static HashMap<Double, Double> map = new LinkedHashMap<Double, Double>();
	public static HashMap<Double, Double> mem_map = new LinkedHashMap<Double, Double>();

	public static HashMap<Double, Double> historyCpuAll = new LinkedHashMap<Double, Double>();
	public static HashMap<Double, Double> historyMemAll = new LinkedHashMap<Double, Double>();

	// 广播
	public static String INTENT_ACTION_BROADCAST_APPS_LIST = "com.qihoo.testtools.new.apps.list";
	public static String INTENT_ACTION_BROADCAST_FLOAT_VIEW_STOP = "com.qihoo.testtools.new.float.view.stop";
	public static String INTENT_ACTION_BROADCAST_HISTORY_RECORD_DATA = "com.qihoo.testtools.new.history.record.data";
	public static String INTENT_ACTION_BROADCAST_ALL_RECORD_DATA = "com.qihoo.testtools.new.all.record.data";
	public static String INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST = "com.qihoo.testtools.new.history.apps.list";
	public static String INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST = "com.qihoo.testtools.new.history.apps.detail.list";
	/** 存储的文件名 */
	public static final String fileName = "settingfile";
	/** 存储后的文件路径：/data/data/<package name>/shares_prefs + 文件名.xml */
	public static final String PATH = "/data/data/com.qihoo.testtools_new/shared_prefs/settingfile.xml";

	@Override
	public void onCreate() {
		super.onCreate();

		mySelf = this;

		appsList = new ArrayList<ApplicationBean>();
		historyAppsList = new ArrayList<HistoryBean>();
		historyDetailApplicationList = new ArrayList<HistoryBean>();
		historyRecodeDataList = new ArrayList<HistoryBean>();

		// 创建SharedPreferences文件，用来存储设置信息

		// 判断文件是否存在，如果不存在则创建
		File file = new File(PATH);
		if (!file.exists()) {
			SharedPreferences preferences = getSharedPreferences(fileName,
					Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putInt("time", 3);
			editor.putBoolean("isWindowOpen", true);
			editor.commit();
		}

		// 启动线程加载手机中所有的应用
		ListApplicationsThread appsThread = new ListApplicationsThread();
		appsThread.start();

		// 启动线程加载手机中的测试记录文件
		ListHistoryAppsThread historyAppsThread = new ListHistoryAppsThread();
		historyAppsThread.start();

	}

}
