package com.zakoi.accessory.smartnotify.receiver;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PackageDataModel {

	String appname = "";
	String pname = "";
	String versionName = "";
	String icon_uri = "";
	int versionCode = 0;
	String TAG = "Package Model";

	public void prettyPrint() {
		Log.v(TAG, appname + "\t" + pname + "\t" + versionName + "\t"
				+ versionCode);
	}
	
	public String getPackage() {
		return pname;
	}
	
	public void setPackage(String name){
		pname = name;
	}
	
	public String getAppName() {
		return appname;
	}
	
	public void setSetAppName(String name){
		appname = name;
	}
	
	public String getIcon() {
		return icon_uri;
	}
	
	public void setIcon(String url){
		icon_uri = url;
	}
}
