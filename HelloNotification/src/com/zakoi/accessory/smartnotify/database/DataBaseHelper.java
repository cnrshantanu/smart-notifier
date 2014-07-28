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

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "test1.db";

	public static abstract class PackageEntry {
		private static final String KEY_ID = "id";
		public static final String TABLE_NAME = "app_package";
		public static final String COLUMN_NAME_PACKAGE_NAME = "package";
		public static final String COLUMN_NAME_APP = "name";
		public static final String COLUMN_NAME_ICON = "icon";
	}

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private String[] allColumns = { PackageEntry.COLUMN_NAME_PACKAGE_NAME,PackageEntry.COLUMN_NAME_APP,PackageEntry.COLUMN_NAME_ICON };
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ PackageEntry.TABLE_NAME + " (" + PackageEntry.KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + PackageEntry.COLUMN_NAME_PACKAGE_NAME
			+ TEXT_TYPE + COMMA_SEP + PackageEntry.COLUMN_NAME_APP + TEXT_TYPE
			+ COMMA_SEP + PackageEntry.COLUMN_NAME_ICON + TEXT_TYPE
			+ " )";

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
		
		Cursor cursor = db.query(PackageEntry.TABLE_NAME,
		        allColumns, PackageEntry.COLUMN_NAME_PACKAGE_NAME + " =? " + new String[] {String.valueOf(1)}, null,
		        null, null, null);
		if (cursor.moveToFirst()) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(PackageEntry.COLUMN_NAME_PACKAGE_NAME, p.getPackage());
		values.put(PackageEntry.COLUMN_NAME_APP, p.getAppName());
		values.put(PackageEntry.COLUMN_NAME_ICON, p.getIcon());

		long insert_id = db.insert(PackageEntry.TABLE_NAME, null, values);
		Log.d("db","insert id is "+ insert_id);
		db.close();

	}

	public void getPackage(int id) {

	}

	public List<PackageDataModel> getAllPackages() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<PackageDataModel> temp = new ArrayList<PackageDataModel>();
		Cursor cursor = db.query(PackageEntry.TABLE_NAME, allColumns,
				null, null, null, null, null);
		if(cursor.moveToFirst()){
			do {
				PackageDataModel pck =  new PackageDataModel();
				pck.setPackage(cursor.getString(0));
				pck.setSetAppName(cursor.getString(1));
				pck.setIcon(cursor.getString(2));
				temp.add(pck);
			} while (cursor.moveToNext());
		}
		
		return temp;
	}

	public void updatePackage(PackageDataModel p) {

	}

	public void deletePackage(PackageDataModel p) {

	}
	
	private PackageDataModel cursortoPackageData(Cursor c) {
		PackageDataModel temp = new PackageDataModel();
		//temp.setPackage(c.))
		return temp;
		
	}
}
