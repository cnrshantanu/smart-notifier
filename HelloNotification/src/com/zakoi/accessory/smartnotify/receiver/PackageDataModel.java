package com.zakoi.accessory.smartnotify.receiver;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PackageDataModel {

	
	long id;
	int mute = -1;
	String appname = "";
	String pname = "";
	String versionName = "";
	String icon_uri = "";
	int versionCode = 0;
	Boolean canNotify = false;
	String TAG = "Package Model";
	public TimeKeeper start_time;

	public PackageDataModel() {
		start_time = new TimeKeeper();
	}
	public void resetMute() {
		mute = -1;
	}
	
	public void resetMute(int mute_mode) {
		mute = mute_mode;
		if(mute_mode == 1)
			start_time.setTime();
	}
	
	public int getMuteMode() {
		return mute;
	}
	
	public Boolean getCanNotify() {
		return canNotify;
	}
	
	public void setCanNotify(Boolean value) {
		canNotify = value;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void prettyPrint() {
		Log.v(TAG, appname + "\t" + pname + "\t" + versionName + "\t"
				+ versionCode);
	}

	public String getPackage() {
		return pname;
	}

	public void setPackage(String name) {
		pname = name;
	}

	public String getAppName() {
		return appname;
	}

	public void setSetAppName(String name) {
		appname = name;
	}

	public String getIcon() {
		return icon_uri;
	}

	public void setIcon(String url) {
		icon_uri = url;
	}
}
