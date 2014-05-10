package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.sqlite.RaceProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

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
		values.put(RaceProvider.STR_DB_COLUMN_RACEDATE, raceDate);
		values.put(RaceProvider.STR_DB_COLUMN_RACELOCATION, raceLocation);
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, RaceProvider.STR_UPDATEFLG_OFF);
		
		contentResolver.insert(RaceProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 全大会情報を取得する
	 * @param contentResolver
	 * @return 大会情報。存在しない場合は空のリスト。
	 */
	public static List<DataBaseRaceInfo> getAllRaceInfo( ContentResolver contentResolver ){
		
		List<DataBaseRaceInfo> list = new ArrayList<DataBaseRaceInfo>();
		
		String[] projection = {RaceProvider.STR_DB_COLUMN_RACEID, RaceProvider.STR_DB_COLUMN_RACENAME, RaceProvider.STR_DB_COLUMN_RACEDATE, RaceProvider.STR_DB_COLUMN_RACELOCATION, RaceProvider.STR_DB_COLUMN_UPDATEFLG};
		
		Cursor c = contentResolver.query(RaceProvider.URI_DB, projection, null, null, null);
		
		while(c.moveToNext()){
			// データ設定
			DataBaseRaceInfo info = getRaceInfoByCursor(c);
			list.add(info);
		}

		c.close();
		
		return list;
	}
	
	/**
	 * 大会IDから大会情報を取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return　大会情報。存在しない場合はnull
	 */
	public static DataBaseRaceInfo getRaceInfoByRaceId( ContentResolver contentResolver, String raceId ){
		
		DataBaseRaceInfo raceInfo = null;
		
		String[] projection = {RaceProvider.STR_DB_COLUMN_RACEID, RaceProvider.STR_DB_COLUMN_RACENAME, RaceProvider.STR_DB_COLUMN_RACEDATE, RaceProvider.STR_DB_COLUMN_RACELOCATION, RaceProvider.STR_DB_COLUMN_UPDATEFLG};
		String selection = RaceProvider.STR_DB_COLUMN_RACEID + "='" + raceId+"'";
		
		Cursor c = contentResolver.query(RaceProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			raceInfo = getRaceInfoByCursor(c);
		}
		return raceInfo;
	}
	
	private static DataBaseRaceInfo getRaceInfoByCursor(Cursor c){
		
		// データ取り出し
		String id = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACEID));
		String name = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACENAME));
		String date = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACEDATE));
		String location = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACELOCATION));
		String updateFlg = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_UPDATEFLG));
		
		// データ設定
		DataBaseRaceInfo info = new DataBaseRaceInfo();
		info.setRaceId(id);
		info.setRaceName(name);
		info.setRaceDate(date);
		info.setRaceLocation(location);
		info.setUpdateFlg(updateFlg);
		
		return info;
	}
}
