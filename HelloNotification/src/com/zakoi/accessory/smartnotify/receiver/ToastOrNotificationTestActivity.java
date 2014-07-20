package com.zakoi.accessory.smartnotify.receiver;

import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.sonymobile.smartextension.hellonotification.HelloNotificationExtensionService;
import com.example.sonymobile.smartextension.hellonotification.HelloNotificationPreferenceActivity;
import com.example.sonymobile.smartextension.hellonotification.R;
import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.zakoi.accessory.smartnotify.receiver.MyAccessibilityService.Constants;
 
public class ToastOrNotificationTestActivity extends Activity {
 
    private static final String TAG = "ToastOrNotificationTestActivity";
    private static PackageGrabber m_packageGrabber;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        final IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_CATCH_NOTIFICATION);
        mIntentFilter.addAction(Constants.ACTION_CATCH_TOAST);
        registerReceiver(toastOrNotificationCatcherReceiver, mIntentFilter);
        Log.v(TAG, "Receiver registered.");
        m_packageGrabber = new PackageGrabber(getApplicationContext());
        m_packageGrabber.getPackages();
    }
             
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(toastOrNotificationCatcherReceiver);
    }
         
    public void addData(String name,String message) {
        Random rand = new Random();
        int index = rand.nextInt(5);
        long time = System.currentTimeMillis();
        long sourceId = NotificationUtil.getSourceId(this,
                HelloNotificationExtensionService.EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(HelloNotificationExtensionService.LOG_TAG, "Failed to insert data");
            return;
        }
        String profileImage = ExtensionUtils.getUriString(this,
                R.drawable.widget_default_userpic_bg);

        // Build the notification.
        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, name);
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

        NotificationUtil.addEvent(this, eventValues);
    }

    private final BroadcastReceiver toastOrNotificationCatcherReceiver = new BroadcastReceiver() {
         
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Received message");
            Log.v(TAG, "intent.getAction() :: " + intent.getAction());
            String app_name = intent.getStringExtra(Constants.EXTRA_PACKAGE);
            String message = intent.getStringExtra(Constants.EXTRA_MESSAGE);
            addData(app_name,message);
        }
    };
}
