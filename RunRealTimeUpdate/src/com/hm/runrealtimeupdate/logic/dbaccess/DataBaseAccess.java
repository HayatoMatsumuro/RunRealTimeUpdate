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
		
		values.put(RaceProvider.STR_DB_COLUMN_RACEID, dbRaceInfo.getRaceId() );
		values.put(RaceProvider.STR_DB_COLUMN_RACENAME, dbRaceInfo.getRaceName());
		values.put(RaceProvider.STR_DB_COLUMN_RACEDATE, dbRaceInfo.getRaceDate());
		values.put(RaceProvider.STR_DB_COLUMN_RACELOCATION, dbRaceInfo.getRaceLocation());
		values.put(RaceProvider.STR_DB_COLUMN_UPDATEFLG, dbRaceInfo.getUpdateFlg());
		values.put(RaceProvider.STR_DB_COLUMN_DATE, dbRaceInfo.getDate());
		
		contentResolver.insert(RaceProvider.URI_DB, values);
		return;
	}
	
	/**
	 * 指定の大会の速報状態設定
	 * @param contentResolver
	 * @param raceId 大会ID
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
		
		values.put(RunnerProvider.STR_DB_COLUMN_RACEID, dbRunnerInfo.getRaceId());
		values.put(RunnerProvider.STR_DB_COLUMN_NUMBER, dbRunnerInfo.getNumber());
		values.put(RunnerProvider.STR_DB_COLUMN_NAME, dbRunnerInfo.getName());
		values.put(RunnerProvider.STR_DB_COLUMN_SECTION, dbRunnerInfo.getSection());
		values.put(RunnerProvider.STR_DB_COLUMN_DATE, dbRunnerInfo.getDate());
		
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
		/*
		// TODO: テスト用
		String[][] tRunnerInfo = {
				{ "2014utmf",	"100",		"テスト1",		"utmf",		"2014-05-31 00:00:00" },
				{ "2014utmf",	"200",		"テスト2",		"utmf",		"2014-05-31 01:00:00" },
				{ "2014utmf",	"300",		"テスト3",		"utmf",		"2014-05-31 02:00:00" },
				{ "2014utmf",	"400",		"テスト4",		"sty",		"2014-05-31 03:00:00" },
				{ "2014utmf",	"500",		"テスト5",		"sty",		"2014-05-31 04:00:00" },
				{ "2014utmf",	"600",		"テスト6",		"utmf",		"2014-05-31 05:00:00" },
		};
		
		for( int i=0; i<6; i++ ){
			DataBaseRunnerInfo info = new DataBaseRunnerInfo();
			info.setRaceId(tRunnerInfo[i][0]);
			info.setNumber(tRunnerInfo[i][1]);
			info.setName(tRunnerInfo[i][2]);
			info.setSection(tRunnerInfo[i][3]);
			info.setDate(tRunnerInfo[i][4]);
			list.add(info);
		}
		
		return list;
		*/
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
		
		values.put(TimelistProvider.STR_DB_COLUMN_RACEID, dbTimeList.getRaceId());
		values.put(TimelistProvider.STR_DB_COLUMN_NUMBER, dbTimeList.getNumber());
		values.put(TimelistProvider.STR_DB_COLUMN_POINT, dbTimeList.getPoint());
		values.put(TimelistProvider.STR_DB_COLUMN_SPLIT, dbTimeList.getSplit());
		values.put(TimelistProvider.STR_DB_COLUMN_LAP, dbTimeList.getLap());
		values.put(TimelistProvider.STR_DB_COLUMN_CURRENTTIME, dbTimeList.getCurrentTime());
		values.put(TimelistProvider.STR_DB_COLUMN_DATE, dbTimeList.getDate());
		
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
		
		// TODO: テストコード
		if( number.equals("100")){
			String ttInfo[][] = {
					{ "A1 out",		"02:00:00",		"02:00:00",		"17:00:00" },
					{ "A2 out",		"03:00:00",		"01:00:00",		"18:00:00" },
					{ "A3 out",		"05:00:00",		"02:00:00",		"20:00:00" },
			};
			for( int i = 0; i<3; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}
		else if( number.endsWith("200")){
			String ttInfo[][] = {
					{ "A1 out",		"02:00:00",		"02:00:00",		"17:00:00" },
					{ "A2 out",		"06:00:00",		"04:00:00",		"21:00:00" },
			};
			for( int i = 0; i<2; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}
		else if( number.endsWith("300")){
			String ttInfo[][] = {
					{ "A1 out",		"02:00:00",		"02:00:00",		"17:00:00" },
					{ "A2 out",		"03:00:00",		"01:00:00",		"18:00:00" },
					{ "A3 out",		"04:00:00",		"01:00:00",		"19:00:00" },
			};
			for( int i = 0; i<3; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}
		else if( number.endsWith("400")){
			String ttInfo[][] = {
					{ "A10 out",		"01:30:00",		"01:30:00",		"18:30:00" },
					{ "A11 out",		"02:00:00",		"00:30:00",		"19:00:00" },
					{ "A12 out",		"04:00:00",		"02:00:00",		"21:00:00" },
			};
			for( int i = 0; i<3; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}
		else if( number.endsWith("500")){
			String ttInfo[][] = {
					{ "A10 out",		"02:00:00",		"02:00:00",		"19:00:00" },
					{ "A11 out",		"04:00:00",		"02:00:00",		"21:00:00" },
			};
			for( int i = 0; i<2; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}else if( number.endsWith("600")){
			String ttInfo[][] = {
					{ "A1 out",		"02:00:00",		"02:00:00",		"17:00:00" },
					{ "A2 out",		"07:00:00",		"05:00:00",		"22:00:00" },
			};
			for( int i = 0; i<2; i++ ){
				DataBaseTimeList timeList = new DataBaseTimeList();
				timeList.setRaceId(raceId);
				timeList.setNumber(number);
				timeList.setPoint(ttInfo[i][0]);
				timeList.setSplit(ttInfo[i][1]);
				timeList.setLap(ttInfo[i][2]);
				timeList.setCurrentTime(ttInfo[i][3]);
				list.add(timeList);
			}
			
			return list;
		}
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
		
		values.put( UpdateDataProvider.STR_DB_COLUMN_RACEID, dbUpdateData.getRaceId() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_NUMBER, dbUpdateData.getNumber() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_NAME, dbUpdateData.getName() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_SECTION, dbUpdateData.getSection());
		values.put( UpdateDataProvider.STR_DB_COLUMN_POINT, dbUpdateData.getPoint() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_SPLIT, dbUpdateData.getSplit() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_LAP, dbUpdateData.getLap() );
		values.put( UpdateDataProvider.STR_DB_COLUMN_CURRENTTIME, dbUpdateData.getCurrentTime());
		values.put( UpdateDataProvider.STR_DB_COLUMN_DATE, dbUpdateData.getDate());
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
	 * @param c
	 * @return
	 */
	private static DataBaseRaceInfo getRaceInfoByCursor(Cursor c){
		
		// データ取り出し
		String id = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACEID));
		String raceName = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACENAME));
		String raceDate = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACEDATE));
		String raceLocation = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_RACELOCATION));
		String updateFlg = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_UPDATEFLG));
		String date = c.getString(c.getColumnIndex(RaceProvider.STR_DB_COLUMN_DATE));
		
		// データ設定
		DataBaseRaceInfo info = new DataBaseRaceInfo();
		info.setRaceId(id);
		info.setRaceName(raceName);
		info.setRaceDate(raceDate);
		info.setRaceLocation(raceLocation);
		info.setUpdateFlg(updateFlg);
		info.setDate(date);
		
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
		String date = c.getString(c.getColumnIndex(RunnerProvider.STR_DB_COLUMN_DATE));
		
		// データ設定
		DataBaseRunnerInfo info = new DataBaseRunnerInfo();
		info.setRaceId(raceId);
		info.setNumber(number);
		info.setName(name);
		info.setSection(section);
		info.setDate(date);
		
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
		String date = c.getString(c.getColumnIndex(TimelistProvider.STR_DB_COLUMN_DATE));
		
		// データ設定
		DataBaseTimeList timelist = new DataBaseTimeList();
		timelist.setRaceId(raceId);
		timelist.setNumber(number);
		timelist.setPoint(point);
		timelist.setSplit(split);
		timelist.setLap(lap);
		timelist.setCurrentTime(currentTime);
		timelist.setDate(date);
		
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
		String date = c.getString(c.getColumnIndex(UpdateDataProvider.STR_DB_COLUMN_DATE));
		
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
		updateData.setDate(date);
		
		return updateData;
	}
}
