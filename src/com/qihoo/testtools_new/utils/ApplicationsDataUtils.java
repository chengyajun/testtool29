package com.qihoo.testtools_new.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.ApplicationBean;
import com.qihoo.testtools_new.bean.HistoryBean;

public class ApplicationsDataUtils {

	// 获取系统中所有应用的方法
	// public ArrayList<HashMap<String, Object>> getItems(Context context) {
	//
	// PackageManager pckMan = context.getPackageManager();
	// ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String,
	// Object>>();
	//
	// Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	// mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// List<ResolveInfo> resolveInfos = pckMan.queryIntentActivities(
	// mainIntent, 0);
	//
	// // 根据name进行排序
	// Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(
	// pckMan));
	//
	// for (ResolveInfo reInfo : resolveInfos) {
	//
	// if ((reInfo.activityInfo.applicationInfo.flags &
	// ApplicationInfo.FLAG_SYSTEM) == 0
	// && (reInfo.activityInfo.applicationInfo.flags &
	// ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
	// HashMap<String, Object> item = new HashMap<String, Object>();
	//
	// item.put("uid", reInfo.activityInfo.applicationInfo.uid);
	// item.put("appimage",
	// reInfo.activityInfo.applicationInfo.loadIcon(pckMan));
	// item.put("appname", reInfo.activityInfo.applicationInfo
	// .loadLabel(pckMan).toString());
	// item.put("packagename", reInfo.activityInfo.packageName);
	//
	// item.put("mainactivity", reInfo.activityInfo.name);
	//
	// items.add(item);
	// }
	//
	// }
	//
	// return items;
	// }

	public static List<ApplicationBean> getApplicationsDataList(Context context) {

		ArrayList<ApplicationBean> appsList = new ArrayList<ApplicationBean>();
		PackageManager pckMan = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pckMan.queryIntentActivities(
				mainIntent, 0);

		// 根据name进行排序
		Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(
				pckMan));

		for (ResolveInfo reInfo : resolveInfos) {

			if ((reInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& (reInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {

				ApplicationBean bean = new ApplicationBean();
				bean.setUid(reInfo.activityInfo.applicationInfo.uid);
				bean.setAppimage(reInfo.activityInfo.applicationInfo
						.loadIcon(pckMan));
				bean.setAppname(reInfo.activityInfo.applicationInfo.loadLabel(
						pckMan).toString());
				bean.setPackagename(reInfo.activityInfo.packageName);
				bean.setMainactivity(reInfo.activityInfo.name);
				appsList.add(bean);
			}
		}
		return appsList;
	}

	// 读取所有手机内存中的测试记录文件
	public static List<HistoryBean> getHistoryAppsDataList(Context context) {
		ArrayList<HistoryBean> historyList = new ArrayList<HistoryBean>();
		HashMap<String, HistoryBean> map = new HashMap<String, HistoryBean>();

		File root = new File("/mnt/sdcard/testtool");

		if (!root.exists() && !root.isDirectory()) {
			System.out.println("//不存在");
			return historyList;
		} else {
			System.out.println("//目录存在");
			// 则遍历文件
			File[] fs = root.listFiles();
			for (int i = 0; i < fs.length; i++) {
				String name = fs[i].getName();
				String[] stringarr = name.split("\\_");
				String packagename = stringarr[0];
				String str[] = stringarr[1].split("\\.");
				Long date = Long.parseLong(str[0]);

				// Log.i("info","packagename:"+packagename+"date:"+date);

				if (map.containsKey(packagename)) {// 如果存在则比较时间大小
					HistoryBean bean = map.get(packagename);
					if (bean.getDate() < date) {
						bean.setDate(date);
					}
				} else {// 创建对象存储
					try {
						HistoryBean bean = new HistoryBean();
						bean.setDate(date);
						bean.setPackagename(packagename);
						// 获取图标和appname
						PackageManager pm = context.getPackageManager();

						Drawable icon = pm.getApplicationIcon(packagename);
						String appName = (String) pm.getApplicationLabel(pm
								.getApplicationInfo(packagename, 0));
						bean.setAppimage(icon);
						bean.setAppname(appName);
						map.put(packagename, bean);

					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// 将 hashmap转化为arraylist
			Collection<HistoryBean> collection = map.values();
			Iterator<HistoryBean> iterator = collection.iterator();
			while (iterator.hasNext()) {
				HistoryBean value = (HistoryBean) iterator.next();
				historyList.add(value);
			}

			// list按照date降序排列
			Collections.sort(historyList, new Comparator<HistoryBean>() {

				@Override
				public int compare(HistoryBean bean1, HistoryBean bean2) {
					// if(bean1.getDate()>bean2.getDate()){
					// return 1;
					// }
					return (int) (bean2.getDate() - bean1.getDate());

				}
			});
			return historyList;
		}

	}

}
