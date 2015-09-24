package com.qihoo.testtools_new.bean;

import android.graphics.drawable.Drawable;

public class ApplicationBean {

	private int uid;
	private Drawable appimage;
	private String appname;
	private String packagename;
	private String mainactivity;
	private int PID;

	// 测试数据
	private String pSS_MEM;//内存
	private String processCpuRatio;//CPU
	
	private int startVoltage = -1;//开始电量
	private int endVoltage;//结束电量
	private String TRAFFIC_WIFI;//wifi
	private String TRAFFIC_3G;//3g

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Drawable getAppimage() {
		return appimage;
	}

	public void setAppimage(Drawable appimage) {
		this.appimage = appimage;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getMainactivity() {
		return mainactivity;
	}

	public void setMainactivity(String mainactivity) {
		this.mainactivity = mainactivity;
	}

	// 测试数据变量的set、get方法
	public String getpSS_MEM() {
		return pSS_MEM;
	}

	public void setpSS_MEM(String pSS_MEM) {
		this.pSS_MEM = pSS_MEM;
	}

	public String getProcessCpuRatio() {
		return processCpuRatio;
	}

	public void setProcessCpuRatio(String processCpuRatio) {
		this.processCpuRatio = processCpuRatio;
	}

//	public int getVoltage() {
//		return Voltage;
//	}
//
//	public void setVoltage(int voltage) {
//		Voltage = voltage;
//	}

	public String getTRAFFIC_WIFI() {
		return TRAFFIC_WIFI;
	}

	public void setTRAFFIC_WIFI(String tRAFFIC_WIFI) {
		TRAFFIC_WIFI = tRAFFIC_WIFI;
	}

	public String getTRAFFIC_3G() {
		return TRAFFIC_3G;
	}

	public void setTRAFFIC_3G(String tRAFFIC_3G) {
		TRAFFIC_3G = tRAFFIC_3G;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public int getStartVoltage() {
		return startVoltage;
	}

	public void setStartVoltage(int startVoltage) {
		this.startVoltage = startVoltage;
	}

	public int getEndVoltage() {
		return endVoltage;
	}

	public void setEndVoltage(int endVoltage) {
		this.endVoltage = endVoltage;
	}
	
	

}
