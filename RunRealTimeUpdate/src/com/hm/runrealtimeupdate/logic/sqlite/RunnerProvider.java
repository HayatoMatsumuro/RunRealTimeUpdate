package com.hm.runrealtimeupdate.logic.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * DB名:runner
 * | key                 | raceid | number | name | section |
 * | INTEGER PRIMARY KEY | TEXT   | TEXT   | TEXT | TEXT    |
 * @author Hayato Matsumuro
 *
 */
public class RunnerProvider extends ContentProvider {

	private RunnerDatabaseHelper runnerDatabaseHelper = null;
	
	public static final Uri URI_DB = Uri.parse("content://com.hm.runrealtimeupdate.logic.sqlite.runnerprovider");
	
	public static final String STR_DB_NAME = "runner";
	public static final String STR_DB_COLUMN_KEY = "key";
	public static final String STR_DB_COLUMN_RACEID = "raceid";
	public static final String STR_DB_COLUMN_NUMBER = "number";
	public static final String STR_DB_COLUMN_NAME = "name";
	public static final String STR_DB_COLUMN_SECTION = "section";
	
	private static final int VERSION = 1;
	
	@Override
	public boolean onCreate() {
		runnerDatabaseHelper = new RunnerDatabaseHelper(getContext());
		return false;
	}
	
	@Override
	public int delete(Uri arg0, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = runnerDatabaseHelper.getWritableDatabase();
		
		int numDeleted = db.delete(STR_DB_NAME, selection, selectionArgs);
		return numDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = runnerDatabaseHelper.getWritableDatabase();
		
		long newId = db.insert(STR_DB_NAME, null, values);
		
		Uri newUri = Uri.parse(uri+"/"+newId);
		
		return newUri;
	}

	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = runnerDatabaseHelper.getReadableDatabase();
		
		Cursor c = db.query(STR_DB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = runnerDatabaseHelper.getWritableDatabase();
		
		int numUpdated = db.update(STR_DB_NAME, values, selection, selectionArgs);
		
		return numUpdated;
	}
	
	public class RunnerDatabaseHelper extends SQLiteOpenHelper{

		public RunnerDatabaseHelper(Context context) {
			// TODO バージョン考察
			super(context, STR_DB_NAME+".db", null, VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + STR_DB_NAME + "("
					+ STR_DB_COLUMN_KEY + " INTEGER PRIMARY KEY,"
					+ STR_DB_COLUMN_RACEID + " TEXT,"
					+ STR_DB_COLUMN_NUMBER + " TEXT,"
					+ STR_DB_COLUMN_NAME + " TEXT,"
					+ STR_DB_COLUMN_SECTION + " TEXT"
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
