/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.sonymobile.smartextension.hellonotification;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.zakoi.accessory.smartnotify.database.DataBaseHelper;
import com.zakoi.accessory.smartnotify.receiver.PackageDataModel;

/**
 * The sample extension service handles extension registration and inserts data
 * into the notification database.
 */
public class HelloNotificationExtensionService extends ExtensionService {

	/** Extension specific id for the source. */
	public static final String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_SAMPLE_NOTIFICATION";

	public static final String LOG_TAG = "HelloNotification";
	private DataBaseHelper m_packageDB;

	public HelloNotificationExtensionService() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_packageDB = new DataBaseHelper(this);
		Log.d(LOG_TAG, "onCreate: HelloNotificationExtensionService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy: HelloNotificationExtensionService");
	}

	@Override
	protected void onViewEvent(Intent intent) {
		String action = intent
				.getStringExtra(Notification.Intents.EXTRA_ACTION);
		String hostAppPackageName = intent
				.getStringExtra(Registration.Intents.EXTRA_AHA_PACKAGE_NAME);
		boolean advancedFeaturesSupported = DeviceInfoHelper
				.isSmartWatch2ApiAndScreenDetected(this, hostAppPackageName);

		// Determine what item a user tapped in the options menu and take
		// appropriate action.
		int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID,
				-1);
		if (Notification.SourceColumns.ACTION_1.equals(action)) {
			mutefor5mins(eventId);
		} else if (Notification.SourceColumns.ACTION_2.equals(action)) {
			mutePackage(eventId);
		} else if (Notification.SourceColumns.ACTION_3.equals(action)) {
			new ClearEventsTask().execute();
		}
	}

	@Override
	protected void onRefreshRequest() {
		// Do nothing. Only relevant for polling extensions.
	}

	/**
	 * Shows a toast on the phone with the information associated with an event.
	 * 
	 * @param eventId
	 *            The event id
	 */
	public void mutefor5mins(int eventId) {
		Log.d(LOG_TAG, "mutefor 5 mins event id: " + eventId);
		Cursor cursor = null;
		try {
			String Appname = "";
			cursor = getContentResolver()
					.query(Notification.Event.URI, null,
							Notification.EventColumns._ID + " = " + eventId,
							null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int nameIndex = cursor
						.getColumnIndex(Notification.EventColumns.DISPLAY_NAME);
				Appname = cursor.getString(nameIndex);
				PackageDataModel temp = m_packageDB
						.getPackageModelfromAppName(Appname);
				temp.resetMute(1);
				m_packageDB.updatePackage(temp);
			}
		} catch (SQLException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (SecurityException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (IllegalArgumentException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void mutePackage(int eventId) {
		Log.d(LOG_TAG, "mutefor 5 mins event id: " + eventId);
		Cursor cursor = null;
		try {
			String Appname = "";
			cursor = getContentResolver()
					.query(Notification.Event.URI, null,
							Notification.EventColumns._ID + " = " + eventId,
							null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int nameIndex = cursor
						.getColumnIndex(Notification.EventColumns.DISPLAY_NAME);
				Appname = cursor.getString(nameIndex);
				PackageDataModel temp = m_packageDB
						.getPackageModelfromAppName(Appname);
				temp.setCanNotify(false);
				m_packageDB.updatePackage(temp);
			}
		} catch (SQLException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (SecurityException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (IllegalArgumentException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	protected RegistrationInformation getRegistrationInformation() {
		return new HelloNotificationRegistrationInformation(this);
	}

	@Override
	protected boolean keepRunningWhenConnected() {
		return false;
	}

	public class ClearEventsTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int nbrDeleted = 0;
			nbrDeleted = NotificationUtil
					.deleteAllEvents(HelloNotificationExtensionService.this);
			return nbrDeleted;
		}

		@Override
		protected void onPostExecute(Integer id) {
			if (id != NotificationUtil.INVALID_ID) {
				Toast.makeText(HelloNotificationExtensionService.this,
						R.string.clear_success, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(HelloNotificationExtensionService.this,
						R.string.clear_failure, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
