package com.zakoi.accessory.smartnotify.receiver;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PackageDataModel {

	String appname = "";
	String pname = "";
	String versionName = "";
	int versionCode = 0;
	Drawable icon;
	String TAG = "Package Model";

	public void prettyPrint() {
		Log.v(TAG, appname + "\t" + pname + "\t" + versionName + "\t"
				+ versionCode);
	}

}
