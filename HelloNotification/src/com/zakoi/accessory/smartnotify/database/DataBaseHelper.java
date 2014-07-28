package com.zakoi.accessory.smartnotify.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.zakoi.accessory.smartnotify.receiver.PackageDataModel;

public class DataBaseHelper extends SQLiteOpenHelper{
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PackageReader.db";

    public static abstract class PackageEntry implements BaseColumns {
		public static final String TABLE_NAME = "app_package";
        public static final String COLUMN_NAME_PACKAGE_NAME = "package";
        public static final String COLUMN_NAME_APP = "name";
        public static final String COLUMN_NAME_ICON = "icon";	
	}
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + PackageEntry.TABLE_NAME + " (" +
	    PackageEntry._ID + " INTEGER PRIMARY KEY," +
	    PackageEntry.COLUMN_NAME_PACKAGE_NAME + TEXT_TYPE + COMMA_SEP +
	    PackageEntry.COLUMN_NAME_APP + TEXT_TYPE + COMMA_SEP +
	    PackageEntry.COLUMN_NAME_ICON + TEXT_TYPE + COMMA_SEP +
	    " )";

	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + PackageEntry.TABLE_NAME;
    public DataBaseHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public void addPackage(PackageDataModel p) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(PackageEntry.COLUMN_NAME_PACKAGE_NAME, p.getPackage());
    	values.put(PackageEntry.COLUMN_NAME_APP, p.getAppName());
    	values.put(PackageEntry.COLUMN_NAME_ICON,p.getIcon());
    	
    	db.insert(PackageEntry.TABLE_NAME,null,values);
    	db.close();
    	
    }
    
    public void getPackage(int id) {
    	
    }
    
    public List<PackageDataModel> getAllPackages() {
    	
    }
    
    public void updatePackage(PackageDataModel p) {

    }

    public void deletePackage(PackageDataModel p) {
    	
    }

}
