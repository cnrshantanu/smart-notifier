package com.zakoi.accessory.smartnotify.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.zakoi.accessory.smartnotify.receiver.PackageDataModel;

public class DataBaseHelper extends SQLiteOpenHelper {

	private final String TAG = "DataBaseHelper";
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "test.db";//test2.db

	public static abstract class PackageEntry {
		private static final String KEY_ID = "id";
		public static final String TABLE_NAME = "app_package";
		public static final String COLUMN_NAME_PACKAGE_NAME = "package";
		public static final String COLUMN_NAME_APP = "name";
		public static final String COLUMN_NAME_ICON = "icon";
		public static final String COLUNM_NOTIFY	= "notify";
	}

	private static final String TEXT_TYPE = " TEXT";
	private static final String BOOLEAN_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private String[] allColumns = { PackageEntry.KEY_ID,
			PackageEntry.COLUMN_NAME_PACKAGE_NAME,
			PackageEntry.COLUMN_NAME_APP, PackageEntry.COLUMN_NAME_ICON, PackageEntry.COLUNM_NOTIFY };
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ PackageEntry.TABLE_NAME + " (" + PackageEntry.KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ PackageEntry.COLUMN_NAME_PACKAGE_NAME + TEXT_TYPE + COMMA_SEP
			+ PackageEntry.COLUMN_NAME_APP + TEXT_TYPE + COMMA_SEP
			+ PackageEntry.COLUMN_NAME_ICON + TEXT_TYPE +COMMA_SEP
			+ PackageEntry.COLUNM_NOTIFY + BOOLEAN_TYPE + " DEFAULT 0"+" )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ PackageEntry.TABLE_NAME;

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
	
	public void addPackage(PackageDataModel p) {
		SQLiteDatabase db = this.getWritableDatabase();

		long temp_id = getPackageId(p.getPackage());
		if (temp_id == -1) {
			ContentValues values = new ContentValues();
			values.put(PackageEntry.COLUMN_NAME_PACKAGE_NAME, p.getPackage());
			values.put(PackageEntry.COLUMN_NAME_APP, p.getAppName());
			values.put(PackageEntry.COLUMN_NAME_ICON, p.getIcon());
			if(p.getCanNotify())
				values.put(PackageEntry.COLUNM_NOTIFY,1);
			else
				values.put(PackageEntry.COLUNM_NOTIFY,0);
			db.insert(PackageEntry.TABLE_NAME, null, values);
			
			db.close();
		} else {
			Log.d(TAG, " Added package with notify " + p.getCanNotify());
			p.setId(temp_id);
			updatePackage(p);
		}

	}


	private long getPackageId(String package_name) {
		String query = "Select * FROM " + PackageEntry.TABLE_NAME + " WHERE "
				+ PackageEntry.COLUMN_NAME_PACKAGE_NAME + " =  \""
				+ package_name + "\"";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			return Long.parseLong(cursor.getString(0));
		} else
			return -1;
	}
	
	public int getPackageNotify(String package_name) {
		String query = "Select * FROM " + PackageEntry.TABLE_NAME + " WHERE "
				+ PackageEntry.COLUMN_NAME_PACKAGE_NAME + " =  \""
				+ package_name + "\"";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			return cursor.getInt(4);
		} else
			return -1;
	}
	
	public List<PackageDataModel> getAllPackages() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<PackageDataModel> temp = new ArrayList<PackageDataModel>();
		Cursor cursor = db.query(PackageEntry.TABLE_NAME, allColumns, null,
				null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				temp.add(cursortoPackageData(cursor));
			} while (cursor.moveToNext());
		}

		return temp;
	}

	public int updatePackage(PackageDataModel p) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PackageEntry.COLUMN_NAME_PACKAGE_NAME, p.getPackage());
		values.put(PackageEntry.COLUMN_NAME_APP, p.getAppName());
		values.put(PackageEntry.COLUMN_NAME_ICON, p.getIcon());
		if(p.getCanNotify())
			values.put(PackageEntry.COLUNM_NOTIFY,1);
		else
			values.put(PackageEntry.COLUNM_NOTIFY,0);

		return db.update(PackageEntry.TABLE_NAME, values, PackageEntry.KEY_ID
				+ " = ?", new String[] { String.valueOf(p.getId()) });
	}

	public void deletePackage(PackageDataModel p) {

	}

	private PackageDataModel cursortoPackageData(Cursor cursor) {
		PackageDataModel pck = new PackageDataModel();
		pck.setId(Long.parseLong(cursor.getString(0)));
		pck.setPackage(cursor.getString(1));
		pck.setSetAppName(cursor.getString(2));
		pck.setIcon(cursor.getString(3));
		if(cursor.getInt(4) == 1)
			pck.setCanNotify(true);
		else
			pck.setCanNotify(false);
		return pck;
	}
}
