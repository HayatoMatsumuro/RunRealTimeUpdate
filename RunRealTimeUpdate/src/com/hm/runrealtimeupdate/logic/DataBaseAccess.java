package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.sqlite.RaceProvider;
import com.hm.runrealtimeupdate.logic.sqlite.RunnerProvider;
import com.hm.runrealtimeupdate.logic.sqlite.TimelistProvider;
import com.hm.runrealtimeupdate.logic.sqlite.UpdateDataProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class DataBaseAccess {
	
	public static final String STR_DBA_RACE_UPDATEFLG_ON = RaceProvider.STR_UPDATEFLG_ON;
	
	public static final String STR_DBA_RACE_UPDATEFLG_OFF = RaceProvider.STR_UPDATEFLG_OFF;
	
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
	 * 指定の大会の速報状態設定
	 * @param contentResolver
	 * @param raceId
	 */
	public static void setRaceUpdate( ContentResolver contentResolver, String raceId, String update ){
		
		ContentValues values = new ContentValues();
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, update );
		
		String selection = RaceProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		
		contentResolver.update(RaceProvider.URI_DB, values, selection, null );
		
		return;
	}
	
	/**
	 * 速報中の大会をすべて取得する。
	 * 速報中の大会がないならば、空リストを取得する
	 * @param contentResolver
	 * @return
	 */
	public static List<DataBaseRaceInfo> getUpdateExeRaceInfo( ContentResolver contentResolver ){
		
		List<DataBaseRaceInfo> list = new ArrayList<DataBaseRaceInfo>();
		
		String[] projection = {
				RaceProvider.STR_DB_COLUMN_RACEID,
				RaceProvider.STR_DB_COLUMN_RACENAME,
				RaceProvider.STR_DB_COLUMN_RACEDATE,
				RaceProvider.STR_DB_COLUMN_RACELOCATION,
				RaceProvider.STR_DB_COLUMN_UPDATEFLG
		};
		
		String selection = RaceProvider.STR_DB_COLUMN_UPDATEFLG + "='" + RaceProvider.STR_UPDATEFLG_ON +"'";
		
		Cursor c = contentResolver.query(RaceProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			// データ設定
			DataBaseRaceInfo info = getRaceInfoByCursor(c);
			list.add(info);
		}

		c.close();
		
		return list;
	}
	
	/**
	 * 全大会情報を取得する
	 * @param contentResolver
	 * @return 大会情報。存在しない場合は空のリスト。
	 */
	public static List<DataBaseRaceInfo> getAllRaceInfo( ContentResolver contentResolver ){
		
		List<DataBaseRaceInfo> list = new ArrayList<DataBaseRaceInfo>();
		
		String[] projection = {
				RaceProvider.STR_DB_COLUMN_RACEID,
				RaceProvider.STR_DB_COLUMN_RACENAME,
				RaceProvider.STR_DB_COLUMN_RACEDATE,
				RaceProvider.STR_DB_COLUMN_RACELOCATION,
				RaceProvider.STR_DB_COLUMN_UPDATEFLG
		};
		
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
		
		c.close();
		
		return raceInfo;
	}
	
	/**
	 * 大会IDから大会情報を削除する
	 * @param contentResolver 
	 * @param raceId 大会ID
	 */
	public static void deleteRaceInfoByRaceId( ContentResolver contentResolver, String raceId ){
		String selection = RaceProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		contentResolver.delete(RaceProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 選手情報を登録する
	 * @param contentResolver
	 * @param raceId
	 * @param number
	 * @param name
	 * @param section
	 */
	public static void entryRunner(
			ContentResolver contentResolver,
			String raceId,
			String number,
			String name,
			String section )
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put(RunnerProvider.STR_DB_COLUMN_RACEID, raceId);
		values.put(RunnerProvider.STR_DB_COLUMN_NUMBER, number);
		values.put(RunnerProvider.STR_DB_COLUMN_NAME, name);
		values.put(RunnerProvider.STR_DB_COLUMN_SECTION, section);
		
		contentResolver.insert(RunnerProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 大会IDの大会に登録されている選手情報を取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return
	 */
	public static List<DataBaseRunnerInfo> getRunnerInfoByRaceId( ContentResolver contentResolver, String raceId){
		List<DataBaseRunnerInfo> list = new ArrayList<DataBaseRunnerInfo>();
		
		String[] projection = {
				RunnerProvider.STR_DB_COLUMN_RACEID,
				RunnerProvider.STR_DB_COLUMN_NUMBER,
				RunnerProvider.STR_DB_COLUMN_NAME,
				RunnerProvider.STR_DB_COLUMN_SECTION
		};
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId+"'";
		
		Cursor c = contentResolver.query(RunnerProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			// データ設定
			DataBaseRunnerInfo info = getRunnerInfoByCursor(c);
			list.add(info);
		}

		c.close();
		
		return list;
	}

	public static List<DataBaseRunnerInfo> getRunnerInfoByRaceIdandSection( ContentResolver contentResolver, String raceId, String section){
		List<DataBaseRunnerInfo> list = new ArrayList<DataBaseRunnerInfo>();
		
		String[] projection = {
				RunnerProvider.STR_DB_COLUMN_RACEID,
				RunnerProvider.STR_DB_COLUMN_NUMBER,
				RunnerProvider.STR_DB_COLUMN_NAME,
				RunnerProvider.STR_DB_COLUMN_SECTION
		};
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId+"' and " + RunnerProvider.STR_DB_COLUMN_SECTION + "='" + section +"'";
		
		Cursor c = contentResolver.query(RunnerProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			// データ設定
			DataBaseRunnerInfo info = getRunnerInfoByCursor(c);
			list.add(info);
		}

		c.close();
		
		return list;
	}
	/**
	 * 大会IDとゼッケンNOから選手情報を削除する
	 * @param contentResolver 
	 * @param raceId 大会ID
	 * @param no　ゼッケンNO
	 */
	public static void deleteRunnerInfoByNo( ContentResolver contentResolver, String raceId, String no){
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "' and " + RunnerProvider.STR_DB_COLUMN_NUMBER + "='" + no + "'";
		contentResolver.delete(RunnerProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 大会IDから選手情報を削除する
	 * @param contentResolver
	 * @param raceId 大会ID
	 */
	public static void deleteRunnerInfoByRaceId( ContentResolver contentResolver, String raceId){
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		contentResolver.delete(RunnerProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * タイム情報を登録する
	 * @param contentResolver
	 * @param raceId
	 * @param number
	 * @param point
	 * @param split
	 * @param lap
	 * @param currentTime
	 */
	public static void entryTimeList(
			ContentResolver contentResolver,
			String raceId,
			String number,
			String point,
			String split,
			String lap,
			String currentTime)
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put(TimelistProvider.STR_DB_COLUMN_RACEID, raceId);
		values.put(TimelistProvider.STR_DB_COLUMN_NUMBER, number);
		values.put(TimelistProvider.STR_DB_COLUMN_POINT, point);
		values.put(TimelistProvider.STR_DB_COLUMN_SPLIT, split);
		values.put(TimelistProvider.STR_DB_COLUMN_LAP, lap);
		values.put(TimelistProvider.STR_DB_COLUMN_CURRENTTIME, currentTime);
		
		contentResolver.insert(TimelistProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 大会IDとゼッケンNo.から選手のタイムリストを取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @param number ゼッケンNo.
	 * @return
	 */
	public static List<DataBaseTimeList> getTimeListByRaceIdandNo( ContentResolver contentResolver, String raceId, String number){
		List<DataBaseTimeList> list = new ArrayList<DataBaseTimeList>();
		
		String[] projection = {
			TimelistProvider.STR_DB_COLUMN_RACEID,
			TimelistProvider.STR_DB_COLUMN_NUMBER,
			TimelistProvider.STR_DB_COLUMN_POINT,
			TimelistProvider.STR_DB_COLUMN_SPLIT,
			TimelistProvider.STR_DB_COLUMN_LAP,
			TimelistProvider.STR_DB_COLUMN_CURRENTTIME
		};
		
		String selection = TimelistProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "' AND " + TimelistProvider.STR_DB_COLUMN_NUMBER + "='" + number + "'";
		
		Cursor c = contentResolver.query(TimelistProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			DataBaseTimeList timeList = getTimeListByCursor(c);
			list.add(timeList);
		}
		
		c.close();
		
		return list;
	}
	
	/**
	 * 大会IDからタイムリストを削除する
	 * @param contentResolver
	 * @param raceId 大会ID
	 */
	public static void deleteTimeListByRaceId( ContentResolver contentResolver, String raceId ){
		String selection = TimelistProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		contentResolver.delete(TimelistProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 速報情報を登録する
	 * @param contentResolver
	 * @param raceId
	 * @param number
	 * @param name
	 * @param section
	 * @param point
	 * @param split
	 * @param lap
	 * @param currentTime
	 */
	public static void entryUpdateData(
		ContentResolver contentResolver,
		String raceId,
		String number,
		String name,
		String section,
		String point,
		String split,
		String lap,
		String currentTime)
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put( UpdateDataProvider.STR_DB_COLUMN_RACEID, raceId );
		values.put( UpdateDataProvider.STR_DB_COLUMN_NUMBER, number);
		values.put( UpdateDataProvider.STR_DB_COLUMN_NAME, name);
		values.put( UpdateDataProvider.STR_DB_COLUMN_SECTION, section);
		values.put( UpdateDataProvider.STR_DB_COLUMN_POINT, point);
		values.put( UpdateDataProvider.STR_DB_COLUMN_SPLIT, split);
		values.put( UpdateDataProvider.STR_DB_COLUMN_LAP, lap);
		values.put( UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME, currentTime);

		contentResolver.insert(UpdateDataProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 大会IDから速報情報を取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return
	 */
	public static List<DataBaseUpdateData> getUpdateDataByRaceId( ContentResolver contentResolver, String raceId){
		List<DataBaseUpdateData> list = new ArrayList<DataBaseUpdateData>();
		
		String[] projection = {
			UpdateDataProvider.STR_DB_COLUMN_RACEID,
			UpdateDataProvider.STR_DB_COLUMN_NUMBER,
			UpdateDataProvider.STR_DB_COLUMN_NAME,
			UpdateDataProvider.STR_DB_COLUMN_SECTION,
			UpdateDataProvider.STR_DB_COLUMN_POINT,
			UpdateDataProvider.STR_DB_COLUMN_SPLIT,
			UpdateDataProvider.STR_DB_COLUMN_LAP,
			UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME
		};
		
		String selection = UpdateDataProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		
		Cursor c = contentResolver.query(UpdateDataProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			DataBaseUpdateData updateData = getUpdateDataByCursor(c);
			list.add(updateData);
		}
		
		c.close();
		
		return list;
	}
	
	/**
	 * 大会IDから速報情報を削除する
	 */
	public static void deleteUpdateDataByRaceId( ContentResolver contentResolver, String raceId){
		String selection = UpdateDataProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		contentResolver.delete(UpdateDataProvider.URI_DB, selection, null);
		
		return;
	}
	/**
	 * 大会情報取得
	 * @param c
	 * @return
	 */
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
	
	/**
	 * 選手情報取得
	 * @param c
	 * @return
	 */
	private static DataBaseRunnerInfo getRunnerInfoByCursor(Cursor c){
		// データ取り出し
		String raceId = c.getString(c.getColumnIndex(RunnerProvider.STR_DB_COLUMN_RACEID));
		String number = c.getString(c.getColumnIndex(RunnerProvider.STR_DB_COLUMN_NUMBER));
		String name = c.getString(c.getColumnIndex(RunnerProvider.STR_DB_COLUMN_NAME));
		String section = c.getString(c.getColumnIndex(RunnerProvider.STR_DB_COLUMN_SECTION));
		
		// データ設定
		DataBaseRunnerInfo info = new DataBaseRunnerInfo();
		info.setRaceId(raceId);
		info.setNumber(number);
		info.setName(name);
		info.setSection(section);
		
		return info;
		
	}
	
	/**
	 * タイムリスト取得
	　* @param c
	 * @return
	 */
	private static DataBaseTimeList getTimeListByCursor( Cursor c ){
		// データ取り出し
		String raceId = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_RACEID));
		String number = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_NUMBER));
		String point = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_POINT));
		String split = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_SPLIT));
		String lap = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_LAP));
		String currentTime = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_CURRENTTIME));
		
		// データ設定
		DataBaseTimeList timelist = new DataBaseTimeList();
		timelist.setRaceId(raceId);
		timelist.setNumber(number);
		timelist.setPoint(point);
		timelist.setSplit(split);
		timelist.setLap(lap);
		timelist.setCurrentTime(currentTime);
		
		return timelist;
	}
	
	/**
	 * 速報情報取得
	 * @param c
	 * @return
	 */
	private static DataBaseUpdateData getUpdateDataByCursor( Cursor c){
		
		// データ取り出し
		String raceId = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_RACEID));
		String number = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_NUMBER));
		String name = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_NAME));
		String section = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_SECTION));
		String point = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_POINT));
		String split = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_SPLIT));
		String lap = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_LAP));
		String currentTime = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME));
		
		// データ設定
		DataBaseUpdateData updateData = new DataBaseUpdateData();
		updateData.setRaceId(raceId);
		updateData.setNumber(number);
		updateData.setName(name);
		updateData.setSection(section);
		updateData.setPoint(point);
		updateData.setSplit(split);
		updateData.setLap(lap);
		updateData.setCurrentTime(currentTime);
		
		return updateData;
	}
}
