package com.zakoi.accessory.smartnotify.receiver;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.example.sonymobile.smartextension.hellonotification.HelloNotificationExtensionService;
import com.example.sonymobile.smartextension.hellonotification.R;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.zakoi.accessory.smartnotify.database.DataBaseHelper;
import com.zakoi.accessory.smartnotify.receiver.MyAccessibilityService.Constants;

public class NotificationReceiver extends Activity {

	private static final String TAG = "Notification Receiver";
	private static PackageGrabber m_packageGrabber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final IntentFilter mIntentFilter = new IntentFilter(
				Constants.ACTION_CATCH_NOTIFICATION);
		mIntentFilter.addAction(Constants.ACTION_CATCH_TOAST);
		registerReceiver(toastOrNotificationCatcherReceiver, mIntentFilter);
		Log.v(TAG, "Receiver registered.");
		//m_packageGrabber = new PackageGrabber(this);
		//m_packageGrabber.getAppsInBackground();
		DataBaseHelper db = new DataBaseHelper(this);
		
		Log.d("Insert: ", "Inserting ..");
		PackageDataModel p = new PackageDataModel();
		p.setIcon("cow_icon");
		p.setPackage("com.shan");
		p.setSetAppName("come_on");
		db.addPackage(p);
		
		
		Log.d("Reading: ", "Reading all contacts.."); 
		List<PackageDataModel> package_list = db.getAllPackages();
		
		for(PackageDataModel pck : package_list){
			Log.d("database","package extracted :" + pck.getAppName() + "   " + pck.getPackage() + "  " + pck.getIcon());
		}
		// m_packageGrabber = new PackageGrabber(getApplicationContext());
		// m_packageGrabber.getPackages();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(toastOrNotificationCatcherReceiver);
	}

	public void addData(String package_name, String message) {
		long time = System.currentTimeMillis();
		long sourceId = NotificationUtil.getSourceId(this,
				HelloNotificationExtensionService.EXTENSION_SPECIFIC_ID);
		if (sourceId == NotificationUtil.INVALID_ID) {
			Log.e(HelloNotificationExtensionService.LOG_TAG,
					"Failed to insert data");
			return;
		}

		try {

			String profileImage;
			ApplicationInfo appInfo;
			String app_name;
			appInfo = this.getPackageManager().getApplicationInfo(package_name,
					0);
			if (appInfo.icon != 0) {
				Uri icon_uri = Uri.parse("android.resource://" + package_name
						+ "/" + appInfo.icon);
				profileImage = icon_uri.toString();
			} else
				profileImage = ExtensionUtils.getUriString(this,
						R.drawable.widget_default_userpic_bg);
			app_name = appInfo.loadLabel(getPackageManager()).toString();
			// Build the notification.
			ContentValues eventValues = new ContentValues();
			eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
			eventValues.put(Notification.EventColumns.DISPLAY_NAME, app_name);
			eventValues.put(Notification.EventColumns.MESSAGE, message);
			eventValues.put(Notification.EventColumns.PERSONAL, 1);
			eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI,
					profileImage);
			eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
			eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);
			NotificationUtil.addEvent(this, eventValues);
		} catch (Exception e) {
			Log.d(TAG, "could not load details");
		}
	}

	private final BroadcastReceiver toastOrNotificationCatcherReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(TAG, "Received message");
			Log.v(TAG, "intent.getAction() :: " + intent.getAction());
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			boolean isScreenOn = pm.isScreenOn();
			
			if (isScreenOn)
				return;
			
			String app_name = intent.getStringExtra(Constants.EXTRA_PACKAGE);
			String message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
			addData(app_name, message);
		}
	};
}
