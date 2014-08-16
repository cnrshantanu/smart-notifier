package com.zakoi.accessory.smartnotify.receiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.zakoi.accessory.smartnotify.database.DataBaseHelper;

public class PackageGrabber {

	private final String TAG = "PackageGrabber";
	private Context m_context;
	private AsyncTask m_getPackageTask;
	private DataBaseHelper m_packageDB;
	
	ArrayList<PackageDataModel> m_packageList;

	public PackageGrabber(Context context) {
		m_context = context;
		m_packageList = new ArrayList<PackageDataModel>();
		m_packageDB = new DataBaseHelper(context);
	}

	public ArrayList<PackageDataModel> getPackages() {
		
		return m_packageList;
	}

	private ArrayList<PackageDataModel> getInstalledApps(boolean getSysPackages) {
		ArrayList<PackageDataModel> res = new ArrayList<PackageDataModel>();
		List<PackageInfo> packs = m_context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			PackageDataModel newInfo = new PackageDataModel();
			newInfo.appname = p.applicationInfo.loadLabel(
					m_context.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			//newInfo.icon = p.applicationInfo.loadIcon(m_context
				//	.getPackageManager());
			res.add(newInfo);
			m_packageDB.addPackage(newInfo);
		}
		return res;
	}

	public void getAppsInBackground() {
		Log.d(TAG, "async at load");
		m_getPackageTask = new AsyncTask<Void, Void, ArrayList<PackageDataModel>>() {

			@Override
			protected ArrayList<PackageDataModel> doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				Log.d("async task", "async at start");
				ArrayList<PackageDataModel> temp_packageList = new ArrayList<PackageDataModel>();
				List<PackageInfo> packs = m_context.getPackageManager()
						.getInstalledPackages(0);
				for (int i = 0; i < packs.size(); i++) {
					PackageInfo p = packs.get(i);
					
					if ((p.versionName == null)) {
						continue;
					} 
					if(!(m_context.getPackageManager().getLaunchIntentForPackage(p.applicationInfo.packageName) != null) ){
		                continue;
					}
					PackageDataModel newInfo = new PackageDataModel();
					newInfo.appname = p.applicationInfo.loadLabel(
							m_context.getPackageManager()).toString();
					newInfo.pname = p.packageName;
					newInfo.versionName = p.versionName;
					newInfo.versionCode = p.versionCode;
					temp_packageList.add(newInfo);
				}
				Collections.sort(temp_packageList, new PackageComparator());
				return temp_packageList;
			}

			@Override
			protected void onPostExecute(
					final ArrayList<PackageDataModel> result) {
				m_packageList = result;
				final int max = m_packageList.size();
				for (int i = 0; i < max; i++) {
					int notifyStatus = m_packageDB.getPackageNotify(m_packageList.get(i).getPackage());
					if(notifyStatus == 1)
						m_packageList.get(i).setCanNotify(true);
					else
						m_packageList.get(i).setCanNotify(false);
					
					//m_packageDB.addPackage(m_packageList.get(i));
					
					//m_packageList.get(i).prettyPrint();
				}
				printPackagesFromDB();
			}
		}.execute(null, null, null);
	}
	
	public void addPackage(PackageDataModel l_package) {
		m_packageDB.addPackage(l_package);
	}
	public void printPackagesFromDB() {
		Log.i(TAG, "Reading all applications."); 
		List<PackageDataModel> package_list = m_packageDB.getAllPackages();
		
		for(PackageDataModel pck : package_list){
			Log.i(TAG," id : "+ pck.getId() + " package extracted :" + pck.getPackage() + " application name :  " + pck.getAppName() + " get icon : " + pck.getIcon() + " get Notify "+ pck.getCanNotify());
		}
	}
	
	public class PackageComparator implements Comparator<PackageDataModel> {

		@Override
		public int compare(PackageDataModel p1, PackageDataModel p2) {
			return p1.getAppName().compareToIgnoreCase(p2.getAppName());
			
		}
		
	};
}
