package com.hm.runrealtimeupdate.logic;

import com.hm.runrealtimeupdate.logic.sqlite.RaceProvider;

import android.content.ContentResolver;
import android.content.ContentValues;

public class DataBaseAccess {
	
	/**
	 * 大会情報をデータベースに登録
	 * @param contentResolver
	 * @param raceId
	 * @param raceName
	 * @param raceDate
	 * @param raceLocation
	 */
	public static void entryRace(
			ContentResolver contentResolver,
			String raceId,
			String raceName,
			String raceDate,
			String raceLocation )
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put(RaceProvider.STR_DB_COLUMN_RACEID, raceId );
		values.put(RaceProvider.STR_DB_COLUMN_RACENAME, raceName);
		values.put(RaceProvider.STR_DB_COLUMN_RACELOCATION, raceLocation);
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, RaceProvider.STR_UPDATEFLG_OFF);
		
		contentResolver.insert(RaceProvider.URI_DB, values);
		return;
	}
}
