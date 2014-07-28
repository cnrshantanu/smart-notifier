package com.zakoi.accessory.smartnotify.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class PackageGrabber {

	private Context m_context;
	private AsyncTask m_getPackageTask;
	ArrayList<PackageDataModel> m_packageList;

	public PackageGrabber(Context context) {
		m_context = context;
		m_packageList = new ArrayList<PackageDataModel>();
	}

	public ArrayList<PackageDataModel> getPackages() {
		ArrayList<PackageDataModel> apps = getInstalledApps(false); /*
																	 * false =
																	 * no system
																	 * packages
																	 */
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			apps.get(i).prettyPrint();
		}
		return apps;
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
		}
		return res;
	}

	public void getAppsInBackground() {
		Log.d("async task", "async at load");
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
					PackageDataModel newInfo = new PackageDataModel();
					newInfo.appname = p.applicationInfo.loadLabel(
							m_context.getPackageManager()).toString();
					newInfo.pname = p.packageName;
					newInfo.versionName = p.versionName;
					newInfo.versionCode = p.versionCode;
					newInfo.icon = p.applicationInfo.loadIcon(m_context
							.getPackageManager());
					temp_packageList.add(newInfo);
				}
				return temp_packageList;
			}

			@Override
			protected void onPostExecute(
					final ArrayList<PackageDataModel> result) {
				m_packageList = result;
				final int max = m_packageList.size();
				for (int i = 0; i < max; i++) {
					m_packageList.get(i).prettyPrint();
				}
			}
		}.execute(null, null, null);
	}
}
