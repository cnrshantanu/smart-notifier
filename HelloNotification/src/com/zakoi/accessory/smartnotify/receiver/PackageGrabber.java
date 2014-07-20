package com.zakoi.accessory.smartnotify.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class PackageGrabber {
	private String appname = "";
	private String pname = "";
	private String versionName = "";
	private int versionCode = 0;
	private Drawable icon;
	private String TAG = "Package Grabber";
	private Context m_context;
	    
	private void prettyPrint() {
        Log.v(TAG,appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
	}
	
    public PackageGrabber(Context context){
    	m_context = context;
    }
	
	public ArrayList<PackageGrabber> getPackages() {
	    ArrayList<PackageGrabber> apps = getInstalledApps(false); /* false = no system packages */
	    final int max = apps.size();
	    for (int i=0; i<max; i++) {
	        apps.get(i).prettyPrint();
	    }
	    return apps;
	}

	private ArrayList<PackageGrabber> getInstalledApps(boolean getSysPackages) {
	    ArrayList<PackageGrabber> res = new ArrayList<PackageGrabber>();        
	    List<PackageInfo> packs = m_context.getPackageManager().getInstalledPackages(0);
	    for(int i=0;i<packs.size();i++) {
	        PackageInfo p = packs.get(i);
	        if ((!getSysPackages) && (p.versionName == null)) {
	            continue ;
	        }
	        PackageGrabber newInfo = new PackageGrabber(m_context);
	        newInfo.appname = p.applicationInfo.loadLabel(m_context.getPackageManager()).toString();
	        newInfo.pname = p.packageName;
	        newInfo.versionName = p.versionName;
	        newInfo.versionCode = p.versionCode;
	        newInfo.icon = p.applicationInfo.loadIcon(m_context.getPackageManager());
	        res.add(newInfo);
	    }
	    return res; 
	}
}
