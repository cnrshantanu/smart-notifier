package com.zakoi.accessory.smartnotify.receiver;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
//import android.app.Notification;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.sonymobile.smartextension.hellonotification.HelloNotificationExtensionService;
import com.example.sonymobile.smartextension.hellonotification.R;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;

/**
 * This service class catches Toast or Notification of applications
 * 
 * @author pankaj
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyAccessibilityService extends AccessibilityService {

	private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	private static final String TAG = "MyAccessibilityService";

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		Log.d("shan", "shan please");
		final int eventType = event.getEventType();
		if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			final String sourcePackageName = (String) event.getPackageName();
			Parcelable parcelable = event.getParcelableData();

			if (parcelable instanceof android.app.Notification) {
				// Statusbar Notification

				List<CharSequence> messages = event.getText();
				if (messages.size() > 0) {
					String temp_notificationMsg = "";
					for (int i = 0; i < messages.size(); i++) {
						temp_notificationMsg += messages.get(i).toString();
					}
					final String notificationMsg = temp_notificationMsg;
					Log.v(TAG, "Captured notification message ["
							+ notificationMsg + "] for source ["
							+ sourcePackageName + "]");
					Log.v(TAG, "Broadcasting for "
							+ Constants.ACTION_CATCH_NOTIFICATION);
					try {
						Intent mIntent = new Intent(
								Constants.ACTION_CATCH_NOTIFICATION);
						mIntent.putExtra(Constants.EXTRA_PACKAGE,
								sourcePackageName);
						mIntent.putExtra(Constants.EXTRA_MESSAGE,
								notificationMsg);
						MyAccessibilityService.this.getApplicationContext()
								.sendBroadcast(mIntent);
					} catch (Exception e) {
						Log.e(TAG, e.toString());
					}
				} else {
					Log.e(TAG,
							"Notification Message is empty. Can not broadcast");
				}
			}
		} else {
			Log.v(TAG, "Got un-handled Event");
		}
	}

	@Override
	public void onInterrupt() {

	}

	@Override
	public void onServiceConnected() {
		// Set the type of events that this service wants to listen to.
		// Others won't be passed to this service.
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;

		// If you only want this service to work with specific applications, set
		// their
		// package names here. Otherwise, when the service is activated, it will
		// listen
		// to events from all applications.
		// info.packageNames = new String[]
		// {"com.appone.totest.accessibility",
		// "com.apptwo.totest.accessibility"};

		// Set the type of feedback your service will provide.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
		} else {
			info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		}

		// Default services are invoked only if no package-specific ones are
		// present
		// for the type of AccessibilityEvent generated. This service *is*
		// application-specific, so the flag isn't necessary. If this was a
		// general-purpose service, it would be worth considering setting the
		// DEFAULT flag.

		// info.flags = AccessibilityServiceInfo.DEFAULT;

		info.notificationTimeout = 100;

		this.setServiceInfo(info);
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

	public static final class Constants {

		public static final String EXTRA_MESSAGE = "extra_message";
		public static final String EXTRA_PACKAGE = "extra_package";
		public static final String ACTION_CATCH_TOAST = "com.zakoi.accessibility.CATCH_TOAST";
		public static final String ACTION_CATCH_NOTIFICATION = "com.zakoi.accessibility.CATCH_NOTIFICATION";
	}

	/**
	 * Check if Accessibility Service is enabled.
	 * 
	 * @param mContext
	 * @return <code>true</code> if Accessibility Service is ON, otherwise
	 *         <code>false</code>
	 */
	public static boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		final String service = "com.zakoi.accessibility/com.zakoi.accessibility.MyAccessibilityService";

		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext
					.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (SettingNotFoundException e) {
			Log.e(TAG,
					"Error finding setting, default accessibility to not found: "
							+ e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
				':');

		if (accessibilityEnabled == 1) {
			Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext
					.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					Log.v(TAG, "-------------- > accessabilityService :: "
							+ accessabilityService);
					if (accessabilityService.equalsIgnoreCase(service)) {
						Log.v(TAG,
								"We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
		}

		return accessibilityFound;
	}
}