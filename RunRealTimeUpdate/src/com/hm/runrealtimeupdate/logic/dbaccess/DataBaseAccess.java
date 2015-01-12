package com.hm.runrealtimeupdate.logic.dbaccess;

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
	
	/**
	 * 大会速報中
	 */
	public static final String STR_DBA_RACE_UPDATEFLG_ON = RaceProvider.STR_UPDATEFLG_ON;
	
	/**
	 * 大会速報中でない
	 */
	public static final String STR_DBA_RACE_UPDATEFLG_OFF = RaceProvider.STR_UPDATEFLG_OFF;
	
	/**
	 * 大会速報予約中
	 */
	public static final String STR_DBA_RACE_UPDATEFLG_RESERVE = RaceProvider.STR_UPDATEFLG_RESERVE;
	
	/**
	 * 大会情報をデータベースに登録
	 * @param contentResolver
	 * @param deRaceInfo 大会情報
	 */
	public static void entryRace(
			ContentResolver contentResolver,
			DataBaseRaceInfo dbRaceInfo )
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put(RaceProvider.STR_DB_COLUMN_RACEID, dbRaceInfo.id );
		values.put(RaceProvider.STR_DB_COLUMN_RACENAME, dbRaceInfo.name );
		values.put(RaceProvider.STR_DB_COLUMN_RACEDATE, dbRaceInfo.date );
		values.put(RaceProvider.STR_DB_COLUMN_RACELOCATION, dbRaceInfo.location );
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, dbRaceInfo.updateFlg );
		values.put(RaceProvider.STR_DB_COLUMN_DATE, dbRaceInfo.updateDate );
		
		contentResolver.insert(RaceProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 指定の大会の速報状態設定
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @param update 速報状態
	 */
	public static void setRaceUpdate( ContentResolver contentResolver, String raceId, String update ){
		
		ContentValues values = new ContentValues();
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, update );
		
		String selection = RaceProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		
		contentResolver.update(RaceProvider.URI_DB, values, selection, null );
		
		return;
	}
	
	/**
	 * 速報中( または予約中 ) の大会をすべて取得する。
	 * 速報中の大会がないならば、空リストを取得する
	 * @param contentResolver
	 * @return　速報中の大会。ない場合は空リスト。
	 */
	public static List<DataBaseRaceInfo> getUpdateExeRaceInfo( ContentResolver contentResolver ){
		
		List<DataBaseRaceInfo> list = new ArrayList<DataBaseRaceInfo>();
		
		String[] projection = {
				RaceProvider.STR_DB_COLUMN_RACEID,
				RaceProvider.STR_DB_COLUMN_RACENAME,
				RaceProvider.STR_DB_COLUMN_RACEDATE,
				RaceProvider.STR_DB_COLUMN_RACELOCATION,
				RaceProvider.STR_DB_COLUMN_UPDATEFLG,
				RaceProvider.STR_DB_COLUMN_DATE
		};
		
		String selection = RaceProvider.STR_DB_COLUMN_UPDATEFLG + "='" + RaceProvider.STR_UPDATEFLG_ON +"'" + " OR " + RaceProvider.STR_DB_COLUMN_UPDATEFLG + "='" + RaceProvider.STR_UPDATEFLG_RESERVE +"'";
		
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
				RaceProvider.STR_DB_COLUMN_UPDATEFLG,
				RaceProvider.STR_DB_COLUMN_DATE
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
		
		String[] projection = {
				RaceProvider.STR_DB_COLUMN_RACEID,
				RaceProvider.STR_DB_COLUMN_RACENAME,
				RaceProvider.STR_DB_COLUMN_RACEDATE,
				RaceProvider.STR_DB_COLUMN_RACELOCATION,
				RaceProvider.STR_DB_COLUMN_UPDATEFLG,
				RaceProvider.STR_DB_COLUMN_DATE
		};
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
	 * @param dbRunnerInfo 選手情報
	 */
	public static void entryRunner( ContentResolver contentResolver, DataBaseRunnerInfo dbRunnerInfo )
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put( RunnerProvider.STR_DB_COLUMN_RACEID, dbRunnerInfo.raceId );
		values.put( RunnerProvider.STR_DB_COLUMN_NUMBER, dbRunnerInfo.number );
		values.put( RunnerProvider.STR_DB_COLUMN_NAME, dbRunnerInfo.name );
		values.put( RunnerProvider.STR_DB_COLUMN_SECTION, dbRunnerInfo.section );
		values.put( RunnerProvider.STR_DB_COLUMN_DATE, dbRunnerInfo.updateDate );
		
		contentResolver.insert(RunnerProvider.URI_DB, values);
		return;
	}
	
	public static void setRunnerSection( ContentResolver contentResolver, String raceId, String number, String section ){
		
		ContentValues values = new ContentValues();
		values.put(RunnerProvider.STR_DB_COLUMN_SECTION, section );
		
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'  and " + RunnerProvider.STR_DB_COLUMN_NUMBER + "='" + number +"'";
		
		contentResolver.update(RunnerProvider.URI_DB, values, selection, null );
		
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
				RunnerProvider.STR_DB_COLUMN_SECTION,
				RunnerProvider.STR_DB_COLUMN_DATE
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

	/**
	 * 大会IDとゼッケン番号から選手情報を取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @param number　ゼッケン番号
	 * @return 選手情報
	 */
	public static DataBaseRunnerInfo getRunnerInfoByRaceIdAndNumber( ContentResolver contentResolver, String raceId, String number ){
		DataBaseRunnerInfo dbRunnerInfo = null;
		
		String[] projection = {
				RunnerProvider.STR_DB_COLUMN_RACEID,
				RunnerProvider.STR_DB_COLUMN_NUMBER,
				RunnerProvider.STR_DB_COLUMN_NAME,
				RunnerProvider.STR_DB_COLUMN_SECTION,
				RunnerProvider.STR_DB_COLUMN_DATE
		};
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId+"' and " + RunnerProvider.STR_DB_COLUMN_NUMBER + "='" + number +"'";
		
		Cursor c = contentResolver.query(RunnerProvider.URI_DB, projection, selection, null, null);
		
		while(c.moveToNext()){
			// データ設定
			dbRunnerInfo = getRunnerInfoByCursor(c);
		}

		c.close();
		
		return dbRunnerInfo;
	}
	
	/**
	 * 大会IDと部門に登録されている選手情報を取得する
	 * @param contentResolver
	 * @param raceId　大会ID
	 * @param section　部門
	 * @return
	 */
	public static List<DataBaseRunnerInfo> getRunnerInfoByRaceIdandSection( ContentResolver contentResolver, String raceId, String section){
		List<DataBaseRunnerInfo> list = new ArrayList<DataBaseRunnerInfo>();
		
		String[] projection = {
				RunnerProvider.STR_DB_COLUMN_RACEID,
				RunnerProvider.STR_DB_COLUMN_NUMBER,
				RunnerProvider.STR_DB_COLUMN_NAME,
				RunnerProvider.STR_DB_COLUMN_SECTION,
				RunnerProvider.STR_DB_COLUMN_DATE
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
	 * @param number ゼッケン番号
	 */
	public static void deleteRunnerInfoByRaceIdAndNumber( ContentResolver contentResolver, String raceId, String number ){
		String selection = RunnerProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "' and " + RunnerProvider.STR_DB_COLUMN_NUMBER + "='" + number + "'";
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
	 * @param dbTimeList　タイム情報
	 */
	public static void entryTimeList( ContentResolver contentResolver, DataBaseTimeList dbTimeList )
	{
		// データベースに登録
		ContentValues values = new ContentValues();
		
		values.put( TimelistProvider.STR_DB_COLUMN_RACEID, dbTimeList.raceId );
		values.put( TimelistProvider.STR_DB_COLUMN_NUMBER, dbTimeList.number );
		values.put( TimelistProvider.STR_DB_COLUMN_POINT, dbTimeList.point );
		values.put( TimelistProvider.STR_DB_COLUMN_SPLIT, dbTimeList.split );
		values.put( TimelistProvider.STR_DB_COLUMN_LAP, dbTimeList.lap );
		values.put( TimelistProvider.STR_DB_COLUMN_CURRENTTIME, dbTimeList.currentTime );
		values.put( TimelistProvider.STR_DB_COLUMN_DATE, dbTimeList.updateDate );
		
		contentResolver.insert(TimelistProvider.URI_DB, values);
		return;
	}
	
	
	/**
	 * 大会IDとゼッケンNo.から選手のタイムリストを取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @param number ゼッケン番号
	 * @return
	 */
	public static List<DataBaseTimeList> getTimeListByRaceIdAndNumber( ContentResolver contentResolver, String raceId, String number){
		List<DataBaseTimeList> list = new ArrayList<DataBaseTimeList>();
		
		String[] projection = {
			TimelistProvider.STR_DB_COLUMN_RACEID,
			TimelistProvider.STR_DB_COLUMN_NUMBER,
			TimelistProvider.STR_DB_COLUMN_POINT,
			TimelistProvider.STR_DB_COLUMN_SPLIT,
			TimelistProvider.STR_DB_COLUMN_LAP,
			TimelistProvider.STR_DB_COLUMN_CURRENTTIME,
			TimelistProvider.STR_DB_COLUMN_DATE
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
	 * 大会IDとゼッケン番号からタイムリストを削除する
	 * @param contentResolver
	 * @param raceId　大会ID
	 * @param number　ゼッケン番号
	 */
	public static void deleteTimeListByRaceIdAndNumber( ContentResolver contentResolver, String raceId, String number ){
		String selection = TimelistProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "' AND " + TimelistProvider.STR_DB_COLUMN_NUMBER + "='" + number + "'";
		contentResolver.delete(TimelistProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 速報情報を登録する
	 * @param contentResolver
	 * @param dbUpdateData 速報情報
	 */
	public static void entryUpdateData( ContentResolver contentResolver, DataBaseUpdateData dbUpdateData )
	{
		// データベースに登録
		ContentValues values = new ContentValues();

		values.put( UpdateDataProvider.STR_DB_COLUMN_RACEID, dbUpdateData.raceId );
		values.put( UpdateDataProvider.STR_DB_COLUMN_NUMBER, dbUpdateData.number );
		values.put( UpdateDataProvider.STR_DB_COLUMN_NAME, dbUpdateData.name );
		values.put( UpdateDataProvider.STR_DB_COLUMN_SECTION, dbUpdateData.section );
		values.put( UpdateDataProvider.STR_DB_COLUMN_POINT, dbUpdateData.point );
		values.put( UpdateDataProvider.STR_DB_COLUMN_SPLIT, dbUpdateData.split );
		values.put( UpdateDataProvider.STR_DB_COLUMN_LAP, dbUpdateData.lap );
		values.put( UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME, dbUpdateData.currentTime );
		values.put( UpdateDataProvider.STR_DB_COLUMN_DATE, dbUpdateData.updateDate );
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
			UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME,
			UpdateDataProvider.STR_DB_COLUMN_DATE
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
	 * @param contentResolver
	 * @param raceId　大会ID
	 */
	public static void deleteUpdateDataByRaceId( ContentResolver contentResolver, String raceId){
		String selection = UpdateDataProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "'";
		contentResolver.delete(UpdateDataProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 大会IDとゼッケン番号から速報情報を削除する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @param number　ゼッケン番号
	 */
	public static void deleteUpdateDataByRaceIdAndNumber( ContentResolver contentResolver, String raceId, String number ){
		String selection = UpdateDataProvider.STR_DB_COLUMN_RACEID + "='" + raceId + "' AND " + UpdateDataProvider.STR_DB_COLUMN_NUMBER + "='" + number + "'";
		contentResolver.delete(UpdateDataProvider.URI_DB, selection, null);
		
		return;
	}
	
	/**
	 * 大会情報取得
	 * @param c　カーソル
	 * @return データベース大会情報
	 */
	private static DataBaseRaceInfo getRaceInfoByCursor( Cursor c )
	{	
		// データ設定
		DataBaseRaceInfo info = new DataBaseRaceInfo();
		info.id = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_RACEID ) );
		info.name = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_RACENAME ) );
		info.date = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_RACEDATE ) );
		info.location = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_RACELOCATION ) );
		info.updateFlg = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_UPDATEFLG ) );
		info.updateDate = c.getString( c.getColumnIndex( RaceProvider.STR_DB_COLUMN_DATE ) );

		return info;
	}
	
	/**
	 * 選手情報取得
	 * @param c
	 * @return
	 */
	private static DataBaseRunnerInfo getRunnerInfoByCursor( Cursor c )
	{
		// データ設定
		DataBaseRunnerInfo info = new DataBaseRunnerInfo();
		info.raceId = c.getString( c.getColumnIndex( RunnerProvider.STR_DB_COLUMN_RACEID ) );
		info.number = c.getString( c.getColumnIndex( RunnerProvider.STR_DB_COLUMN_NUMBER ) );
		info.name = c.getString( c.getColumnIndex( RunnerProvider.STR_DB_COLUMN_NAME ) );
		info.section = c.getString( c.getColumnIndex( RunnerProvider.STR_DB_COLUMN_SECTION ) );
		info.updateDate = c.getString( c.getColumnIndex( RunnerProvider.STR_DB_COLUMN_DATE ) );

		return info;		
	}
	
	/**
	 * タイムリスト取得
	　* @param c
	 * @return
	 */
	private static DataBaseTimeList getTimeListByCursor( Cursor c )
	{
		// データ設定
		DataBaseTimeList timelist = new DataBaseTimeList();
		timelist.raceId = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_RACEID ) );
		timelist.number = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_NUMBER ) );
		timelist.point = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_POINT ) );
		timelist.split = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_SPLIT ) );
		timelist.lap = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_LAP ) );
		timelist.currentTime = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_CURRENTTIME ) );
		timelist.updateDate = c.getString( c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_DATE ) );

		return timelist;
	}
	
	/**
	 * 速報情報取得
	 * @param c
	 * @return
	 */
	private static DataBaseUpdateData getUpdateDataByCursor( Cursor c )
	{	
		// データ設定
		DataBaseUpdateData updateData = new DataBaseUpdateData();
		updateData.raceId = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_RACEID ) );
		updateData.number = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_NUMBER ) );
		updateData.name = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_NAME ) );
		updateData.section = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_SECTION ) );
		updateData.point = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_POINT ) );
		updateData.split = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_SPLIT ) );
		updateData.lap = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_LAP ) );
		updateData.currentTime = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME ) );
		updateData.updateDate = c.getString( c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_DATE ) );

		return updateData;
	}
}
