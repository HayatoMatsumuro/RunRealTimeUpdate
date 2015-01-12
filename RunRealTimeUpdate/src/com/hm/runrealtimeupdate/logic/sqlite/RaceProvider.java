package com.hm.runrealtimeupdate.logic.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * 大会プロパイダー
 * DB名:race
 * | key                 | raceid | racename | racedate | racelocation | updateflg | date                        |
 * | INTEGER PRIMARY KEY | TEXT   | TEXT     | TEXT     | TEXT         | TEXT      | TEXT( YYYY-MM-DD hh:mm:ss ) |
 * @author Hayato Matsumuro
 *
 */
public class RaceProvider extends ContentProvider
{
	public static final Uri URI_DB = Uri.parse( "content://com.hm.runrealtimeupdate.logic.sqlite.raceprovider" );

	public static final String STR_DB_NAME = "race";
	public static final String STR_DB_COLUMN_KEY = "key";
	public static final String STR_DB_COLUMN_RACEID = "raceid";
	public static final String STR_DB_COLUMN_RACENAME = "racename";
	public static final String STR_DB_COLUMN_RACEDATE = "racedate";
	public static final String STR_DB_COLUMN_RACELOCATION = "racelocation";
	public static final String STR_DB_COLUMN_UPDATEFLG = "updateflg";
	public static final String STR_DB_COLUMN_DATE = "date";

	public static final String STR_UPDATEFLG_OFF = "0";
	public static final String STR_UPDATEFLG_ON = "1";
	public static final String STR_UPDATEFLG_RESERVE = "2";

	private static final int VERSION = 2;

	private RaceDatabaseHelper updateRaceDatabaseHelper = null;

	@Override
	public int delete( Uri arg0, String selection, String[] selectionArgs )
	{
		SQLiteDatabase db = updateRaceDatabaseHelper.getWritableDatabase();

		int numDeleted = db.delete( STR_DB_NAME, selection, selectionArgs );

		return numDeleted;
	}

	@Override
	public String getType( Uri uri )
	{
		return null;
	}

	@Override
    public Uri insert( Uri uri, ContentValues values )
	{
		SQLiteDatabase db = updateRaceDatabaseHelper.getWritableDatabase();

		long newId = db.insert( STR_DB_NAME, null, values );

		Uri newUri = Uri.parse( uri + "/" + newId );

		return newUri;
	}

	@Override
	public boolean onCreate()
	{
		updateRaceDatabaseHelper = new RaceDatabaseHelper( getContext() );
		return false;
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder )
	{
		SQLiteDatabase db = updateRaceDatabaseHelper.getReadableDatabase();

		Cursor c = db.query( STR_DB_NAME, projection, selection, selectionArgs, null, null, sortOrder );

		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{
		SQLiteDatabase db = updateRaceDatabaseHelper.getWritableDatabase();

		int numUpdated = db.update( STR_DB_NAME, values, selection, selectionArgs );

		return numUpdated;
	}

	/**
	 * 大会ヘルパー
	 * @author Hayato Matsumuro
	 *
	 */
	public class RaceDatabaseHelper extends SQLiteOpenHelper
	{
		/**
		 * コンストラクタ
		 * @param context コンテキスト
		 */
		public RaceDatabaseHelper( Context context )
		{
			super( context, STR_DB_NAME + ".db", null, VERSION );
			return;
		}

		@Override
		public void onCreate( SQLiteDatabase db )
		{
			String sql = "CREATE TABLE " + STR_DB_NAME + "("
					+ STR_DB_COLUMN_KEY + " INTEGER PRIMARY KEY,"
					+ STR_DB_COLUMN_RACEID + " TEXT,"
					+ STR_DB_COLUMN_RACENAME + " TEXT,"
					+ STR_DB_COLUMN_RACEDATE + " TEXT,"
					+ STR_DB_COLUMN_RACELOCATION + " TEXT,"
					+ STR_DB_COLUMN_UPDATEFLG + " TEXT,"
					+ STR_DB_COLUMN_DATE + " TEXT"
					+ ")";
			db.execSQL( sql );
			return;
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
		{
			db.execSQL( "DROP TABLE IF EXISTS " + STR_DB_NAME );
			onCreate( db );
			return;
		}
	}
}
