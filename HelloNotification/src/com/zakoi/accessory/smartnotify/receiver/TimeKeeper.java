package com.zakoi.accessory.smartnotify.receiver;

import java.util.Calendar;

import android.util.Log;

public class TimeKeeper {
	
	public int year,month,day,hour,minute = -1;
	public final String TAG = "TimeKeeper";
	public static int SLEEP_DURATION = 5;
	
	public void setTime() {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR);
		minute = c.get(Calendar.MINUTE);
		
	}
	
	public Boolean is5MinuteOver() {
		
		Calendar c = Calendar.getInstance();
		int temp_year = c.get(Calendar.YEAR) - year;
		int temp_month = c.get(Calendar.MONTH) - month;
		int temp_day = c.get(Calendar.DAY_OF_MONTH) - day;
		int temp_hour = c.get(Calendar.HOUR);
		int temp_minute = c.get(Calendar.MINUTE);
		
		int start_time = (hour * 60) + minute;
		
		Log.d(TAG," start time hour :"+hour + " minute "+ minute);
		
		int time_now  = (temp_hour * 60) + temp_minute;
		if(temp_year < 0 || temp_month < 0 || temp_day < 0)
			time_now += (24*60);
		if(time_now - start_time >= SLEEP_DURATION) {
			return true;
		}
		return false;

	}
}
