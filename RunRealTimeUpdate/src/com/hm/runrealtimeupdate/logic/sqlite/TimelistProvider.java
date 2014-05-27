package com.hm.runrealtimeupdate.logic.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * DB名:timelist
 * | key                 | raceid | number | point | split | lap  | currenttime |
 * | INTEGER PRIMARY KEY | TEXT   | TEXT   | TEXT  | TEXT  | TEXT | TEXT        |
 * @author Hayato Matsumuro
 *
 */
public class TimelistProvider extends ContentProvider {

	public static final Uri URI_DB = Uri.parse("content://com.hm.runrealtimeupdate.logic.sqlite.timelistprovider");
	
	public static final String STR_DB_NAME = "timelist";
	public static final String STR_DB_COLUMN_KEY = "key";
	public static final String STR_DB_COLUMN_RACEID = "raceid";
	public static final String STR_DB_COLUMN_NUMBER = "number";
	public static final String STR_DB_COLUMN_POINT = "point";
	public static final String STR_DB_COLUMN_SPLIT = "split";
	public static final String STR_DB_COLUMN_LAP = "lap";
	public static final String STR_DB_COLUMN_CURRENTTIME = "currenttime";
	
	private static final int VERSION = 1;
	
	private TimeListDatabaseHelper timeListDatabaseHelper = null;
	
	@Override
	public int delete(Uri arg0, String selection, String[] selectionArgs) {
		SQLiteDatabase db = timeListDatabaseHelper.getWritableDatabase();
		
		int numDeleted = db.delete(STR_DB_NAME, selection, selectionArgs);
		return numDeleted;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = timeListDatabaseHelper.getWritableDatabase();
		
		long newId = db.insert(STR_DB_NAME, null, values);
		
		Uri newUri = Uri.parse(uri+"/"+newId);
		
		return newUri;
	}

	@Override
	public boolean onCreate() {
		timeListDatabaseHelper = new TimeListDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = timeListDatabaseHelper.getReadableDatabase();
		
		Cursor c = db.query(STR_DB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = timeListDatabaseHelper.getWritableDatabase();
		
		int numUpdated = db.update(STR_DB_NAME, values, selection, selectionArgs);
		
		return numUpdated;
	}

	public class TimeListDatabaseHelper extends SQLiteOpenHelper{

		public TimeListDatabaseHelper(Context context) {
			
			// TODO 自動生成されたコンストラクター・スタブ
			super(context,STR_DB_NAME+".db",null,VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + STR_DB_NAME + "("
					+ STR_DB_COLUMN_KEY + " INTEGER PRIMARY KEY,"
					+ STR_DB_COLUMN_RACEID + " TEXT,"
					+ STR_DB_COLUMN_NUMBER + " TEXT,"
					+ STR_DB_COLUMN_POINT + " TEXT,"
					+ STR_DB_COLUMN_SPLIT + " TEXT,"
					+ STR_DB_COLUMN_LAP + " TEXT,"
					+ STR_DB_COLUMN_CURRENTTIME + " TEXT"
					+ ")";
			db.execSQL(sql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersio) {
			db.execSQL("DROP TABLE IF EXISTS " + STR_DB_NAME);
			onCreate(db);
			
		}
		
	}
}