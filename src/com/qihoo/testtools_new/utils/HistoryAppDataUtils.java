package com.qihoo.testtools_new.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.csvreader.CsvReader;
import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.HistoryBean;

public class HistoryAppDataUtils {

	public static List<HistoryBean> getHistoryList(String packageName,String appname) {
		ArrayList<HistoryBean> historyList = new ArrayList<HistoryBean>();
		File root = new File("/mnt/sdcard/testtool");
		if (!root.exists() && !root.isDirectory()) {
			System.out.println("//不存在");
			return historyList;
		} else {
			File[] fs = root.listFiles();
			for (int i = 0; i < fs.length; i++) {
				String name = fs[i].getName();
				String[] stringarr = name.split("\\_");
				String packagename = stringarr[0];
				String str[] = stringarr[1].split("\\.");
				Long date = Long.parseLong(str[0]);

				// Log.i("info","packagename:"+packagename+"date:"+date);

				if (packageName.equals(packagename)) {// 加入列表中
					HistoryBean bean = new HistoryBean();
					bean.setDate(date);
					bean.setPackagename(packageName);
					bean.setAppname(appname);
					historyList.add(bean);
				}

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
			
			for (int i = 0; i < historyList.size(); i++) {
				HistoryBean bean = historyList.get(i);
				bean.setPosition(i+1);
			}

			String p = packageName;
			Log.i("info", "" + p);
			return historyList;
		}

	}

	// 根据文件名称找到文件进行解析
	public static void getFileData(String filename) {

		// 清除上次所有数据
		TestToolNewApplication.bat = "";
		TestToolNewApplication.traf_3g = "";
		TestToolNewApplication.traf_wifi = "";
		TestToolNewApplication.map.clear();
		TestToolNewApplication.mem_map.clear();
		
		TestToolNewApplication.isBATRecord = false;
		TestToolNewApplication.isCPURecord = false;
		TestToolNewApplication.isMEMRecord = false;
		TestToolNewApplication.isTRAFRecord = false;

		SharedPreferences preferences = TestToolNewApplication.mySelf
				.getSharedPreferences(TestToolNewApplication.fileName, 0);
		int mSecond = 3;//时间间隔默认是3
		double duration = 0.0;
		try {
			// 子线程中读取文件数据
			String path = "/mnt/sdcard/testtool/" + filename;

			CsvReader reader = new CsvReader(path, ',', Charset.forName("GBK"));
			List<Object[]> list = new ArrayList<Object[]>();
			while (reader.readRecord()) {
				list.add(reader.getValues());
				String[] strarr = reader.getValues();

				// 通过arr[0]判断数据的类型
				int type = Integer.parseInt(strarr[0]);
				switch (type) {
				case 1: {
					// 电量
					String data_bat = strarr[2];
					Log.i("info", "-----------data_bat--------"+data_bat);
					if (data_bat.equals("-1")) {
						TestToolNewApplication.bat = "充电中";
					} else {
						TestToolNewApplication.bat = data_bat;
					}
				}
					break;
				case 3: {
					// cpu，mem
					String cpu = strarr[1].split("%")[0];
					String mem = strarr[2].split("[a-zA-Z_]+")[0];
					Double data_cpu = Double.parseDouble(cpu);
					Double data_mem = Double.parseDouble(mem);
					TestToolNewApplication.map.put(duration, data_cpu);
					TestToolNewApplication.mem_map.put(duration, data_mem);
					duration = duration + mSecond;

				}
					break;
				case 4: {
					// wifi
					TestToolNewApplication.traf_wifi = strarr[2];
				}
					break;
				case 5: {
					// 3g
					TestToolNewApplication.traf_3g = strarr[2];
				}
					break;
				case 6:{
					if(strarr[2].equals("1")){
						TestToolNewApplication.isCPURecord = true;
					}else{
						TestToolNewApplication.isCPURecord = false;
					}
					
				}break;
				case 7:{
					if(strarr[2].equals("1")){
						TestToolNewApplication.isMEMRecord = true;
					}else{
						TestToolNewApplication.isMEMRecord = false;
					}
				}break;
				case 8:{
					if(strarr[2].equals("1")){
						TestToolNewApplication.isBATRecord = true;
					}else{
						TestToolNewApplication.isBATRecord = false;
					}
				}break;
				case 9:{
					if(strarr[2].equals("1")){
						TestToolNewApplication.isTRAFRecord = true;
					}else{
						TestToolNewApplication.isTRAFRecord = false;
					}
				}break;
				case 10:{
					mSecond = Integer.valueOf(strarr[2]);
				}break;
				case 11:{
					TestToolNewApplication.testStartTime = Long.valueOf(strarr[2]);
				}break;
				case 12:{
					TestToolNewApplication.testEndTime = Long.valueOf(strarr[2]);
				}break;
				
					
				}
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void getFileDataAll(ArrayList<HistoryBean> historyRecordAll) {

		TestToolNewApplication.historyCpuAll.clear();
		TestToolNewApplication.historyMemAll.clear();
		String path;
		double cpuCount = 0.0;
		double memCount = 0.0;
		double cpuAvg = 0;
		double memAvg = 0;
		int count = 0;

		try {
			
			
			String[] fileArr = new String[historyRecordAll.size()];
			for (int i = 0; i < historyRecordAll.size(); i++) {
				HistoryBean bean = historyRecordAll.get(i);

				fileArr[i] = bean.getPackagename() + "_" + bean.getDate() + ".csv";

				path = "/mnt/sdcard/testtool/" + fileArr[i];

				CsvReader reader = new CsvReader(path, ',',
						Charset.forName("GBK"));
				while (reader.readRecord()) {
					count++;
					String[] strarr = reader.getValues();
					if (strarr[0].equals("3")) {// cpu mem
						String cpu = strarr[1].split("%")[0];
						String mem = strarr[2].split("[a-zA-Z_]+")[0];
						Double data_cpu = Double.parseDouble(cpu);
						Double data_mem = Double.parseDouble(mem);
						cpuCount += data_cpu;
						memCount += data_mem;
					}

				}
				cpuAvg = cpuCount / count;
				memAvg = memCount / count;
				TestToolNewApplication.historyCpuAll.put(bean.getPosition()+0.0, cpuAvg);
				TestToolNewApplication.historyMemAll.put(bean.getPosition()+0.0, memAvg);
				// 清零
				cpuCount = 0;
				memCount = 0;
				cpuAvg = 0.0;
				memAvg = 0.0;
				count = 0;
				reader.close();

			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
