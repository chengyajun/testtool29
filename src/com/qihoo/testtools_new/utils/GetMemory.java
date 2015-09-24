package com.qihoo.testtools_new.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.os.Debug;
import android.text.format.Formatter;

public class GetMemory {

	public long availMem, totalMem;
	public ActivityManager activitymanager;
	public MemoryInfo outinfo;

	

	//程序中获取内存信息
	public GetMemory(Context context) {
		activitymanager = (ActivityManager) context
				.getSystemService(Service.ACTIVITY_SERVICE);
		outinfo = new MemoryInfo();
		activitymanager.getMemoryInfo(outinfo);
	}

	public String getAvailMem(Context context) {
		availMem = outinfo.availMem;

		String availMem_string = Formatter.formatFileSize(context, availMem);
		return availMem_string;
	}

	//方法获得实际内存占用
	public int getPss(int[] pid) {
		Debug.MemoryInfo[] memoryinfo = activitymanager
				.getProcessMemoryInfo(pid);
		int pss = memoryinfo[0].getTotalPss();
		return pss;
	}

	public int getPrivateDirty(int[] pid) {
		Debug.MemoryInfo[] memoryinfo = activitymanager
				.getProcessMemoryInfo(pid);
		int PrivateDirty = memoryinfo[0].getTotalPrivateDirty();
		return PrivateDirty;
	}

	public int getShareDirty(int[] pid) {
		Debug.MemoryInfo[] memoryinfo = activitymanager
				.getProcessMemoryInfo(pid);
		int ShareDirty = memoryinfo[0].getTotalSharedDirty();
		return ShareDirty;
	}
	/*
	public int getMingbin(int[] pid) {
		Debug.MemoryInfo[] memoryinfo = activitymanager
				.getProcessMemoryInfo(pid);
		int mingbin = memoryinfo[0].dalvikPss + memoryinfo[0].nativePss
				+ memoryinfo[0].otherPss;
		return mingbin;
	}*/
}
