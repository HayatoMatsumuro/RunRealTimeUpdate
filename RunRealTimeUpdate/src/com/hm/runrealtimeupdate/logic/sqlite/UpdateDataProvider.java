package com.hm.runrealtimeupdate.logic.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * DB名:updatedata
 * | key                 | raceid | number | name | section | point | split | lap  | currenttime | date                        |
 * | INTEGER PRIMARY KEY | TEXT   | TEXT   | TEXT | TEXT    | TEXT  | TEXT  | TEXT | TEXT        | TEXT( YYYY-MM-DD hh:mm:ss ) |
 * @author Hayato Matsumuro
 *
 */
public class UpdateDataProvider extends ContentProvider {

public static final Uri URI_DB = Uri.parse("content://com.hm.runrealtimeupdate.logic.sqlite.updatedataprovider");
	
	public static final String STR_DB_NAME = "updatedata";
	public static final String STR_DB_COLUMN_KEY = "key";
	public static final String STR_DB_COLUMN_RACEID = "raceid";
	public static final String STR_DB_COLUMN_NUMBER = "number";
	public static final String STR_DB_COLUMN_NAME = "name";
	public static final String STR_DB_COLUMN_SECTION = "section";
	public static final String STR_DB_COLUMN_POINT = "point";
	public static final String STR_DB_COLUMN_SPLIT = "split";
	public static final String STR_DB_COLUMN_LAP = "lap";
	public static final String STR_DB_COLUMN_CURRENTTIME = "currenttime";
	public static final String STR_DB_COLUMN_DATE = "date";
	
	private UpdateInfoDatabaseHelper updateinfoDatabaseHelper = null;
	
	private static final int VERSION = 2;
	
	@Override
	public int delete(Uri arg0, String selection, String[] selectionArgs) {
		SQLiteDatabase db = updateinfoDatabaseHelper.getWritableDatabase();
		
		int numDeleted = db.delete(STR_DB_NAME, selection, selectionArgs);
		return numDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
    public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = updateinfoDatabaseHelper.getWritableDatabase();
		
		long newId = db.insert(STR_DB_NAME, null, values);
		
		Uri newUri = Uri.parse(uri+"/"+newId);
		
		return newUri;
	}

	@Override
	public boolean onCreate() {
		updateinfoDatabaseHelper = new UpdateInfoDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = updateinfoDatabaseHelper.getReadableDatabase();
		
		Cursor c = db.query(STR_DB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = updateinfoDatabaseHelper.getWritableDatabase();
		
		int numUpdated = db.update(STR_DB_NAME, values, selection, selectionArgs);
		
		return numUpdated;
	}

	public class UpdateInfoDatabaseHelper extends SQLiteOpenHelper{

		public UpdateInfoDatabaseHelper(Context context) {
			
			super(context,STR_DB_NAME+".db",null,VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + STR_DB_NAME + "("
					+ STR_DB_COLUMN_KEY + " INTEGER PRIMARY KEY,"
					+ STR_DB_COLUMN_RACEID + " TEXT,"
					+ STR_DB_COLUMN_NUMBER + " TEXT,"
					+ STR_DB_COLUMN_NAME + " TEXT,"
					+ STR_DB_COLUMN_SECTION + " TEXT,"
					+ STR_DB_COLUMN_POINT + " TEXT,"
					+ STR_DB_COLUMN_SPLIT + " TEXT,"
					+ STR_DB_COLUMN_LAP + " TEXT,"
					+ STR_DB_COLUMN_CURRENTTIME + " TEXT,"
					+ STR_DB_COLUMN_DATE + " TEXT"
					+ ")";
			db.execSQL(sql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + STR_DB_NAME);
			onCreate(db);
			
		}
		
	}

}
