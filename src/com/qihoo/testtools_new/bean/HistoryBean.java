package com.qihoo.testtools_new.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class HistoryBean {

	private Drawable appimage;
	private String appname;
	private String packagename;
	private long date;
	private int position;
	
	public boolean isChoose;

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

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	
	
}
