package com.hm.runrealtimeupdate.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;

import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseTimeList;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseUpdateData;
import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.ParserRaceInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.IParserUpdate;
import com.hm.runrealtimeupdate.logic.preferences.PreferenceReserveTime;
import com.hm.runrealtimeupdate.logic.preferences.PreferenceStopCount;

/**
 * ロジック
 * @author Hayato Matsumuro
 *
 */
public class Logic
{
	@SuppressLint("SimpleDateFormat")
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/**
	 * 大会情報を登録する
	 * @param contentResolver　コンテントリゾルバ
	 * @param raceInfo 大会情報
	 */
	public static void entryRaceInfo( ContentResolver contentResolver, RaceInfo raceInfo )
	{
		// 登録情報設定
		DataBaseRaceInfo dbRaceInfo = new DataBaseRaceInfo();
		dbRaceInfo.id = raceInfo.id;
		dbRaceInfo.name = raceInfo.name;
		dbRaceInfo.date = raceInfo.date;
		dbRaceInfo.location = raceInfo.location;
		dbRaceInfo.updateFlg =  DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF;
		dbRaceInfo.updateDate = getDateString();

		// データベース登録
		DataBaseAccess.entryRace( contentResolver, dbRaceInfo );

		return;
	}

	/**
	 * 大会情報リストを取得する
	 * @param contentResolver コンテントリゾルバ
	 * @return 大会情報リスト
	 */
	public static List<RaceInfo> getRaceInfoList( ContentResolver contentResolver )
	{
		List<RaceInfo> raceInfoList = new ArrayList<RaceInfo>();

		// データベースから取得
		List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getAllRaceInfo( contentResolver );

		for( DataBaseRaceInfo dbRaceInfo : dbRaceInfoList )
		{
			// 大会情報取得
			RaceInfo raceInfo = getRaceInfoBydbRaceInfo( dbRaceInfo );
			raceInfoList.add( raceInfo );
		}

		return raceInfoList;
	}

	/**
	 * 大会IDの大会情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @return
	 */
	public static RaceInfo getRaceInfo( ContentResolver contentResolver, String raceId )
	{
		DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId( contentResolver, raceId );

		if( dbRaceInfo == null )
		{
			return null;
		}

		return getRaceInfoBydbRaceInfo( dbRaceInfo );
	}

	/**
	 * 大会情報を取得する
	 * @param dbRaceInfo データベース大会情報
	 * @return 大会情報
	 */
	private static RaceInfo getRaceInfoBydbRaceInfo( DataBaseRaceInfo dbRaceInfo )
	{
		RaceInfo raceInfo = new RaceInfo();
		raceInfo.id = dbRaceInfo.id;
		raceInfo.name = dbRaceInfo.name;
		raceInfo.date = dbRaceInfo.date;
		raceInfo.location = dbRaceInfo.location;

		int updateSts = Integer.parseInt( dbRaceInfo.updateFlg );
		raceInfo.updateSts = updateSts;

		return raceInfo;
	}

	/**
	 * 大会情報を削除する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 削除する大会Id
	 */
	public static void deleteRaceInfo( ContentResolver contentResolver, String raceId )
	{
		// 速報データ
		DataBaseAccess.deleteUpdateDataByRaceId( contentResolver, raceId );

		// タイムリスト
		DataBaseAccess.deleteTimeListByRaceId( contentResolver, raceId );

		// 選手削除
		DataBaseAccess.deleteRunnerInfoByRaceId( contentResolver, raceId );

		// 大会を削除する
		DataBaseAccess.deleteRaceInfoByRaceId( contentResolver, raceId );

		return;
	}
	
	/**
	 * 速報中の大会IDを取得する
	 *  大会IDがない場合、または大会情報を未取得ならばnull を返す
	 * @return 速報中の大会ID
	 */
	public static RaceInfo getUpdateRaceId( ContentResolver contentResolver )
	{
		List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getUpdateExeRaceInfo( contentResolver );

		RaceInfo raceInfo = null;

		if( !dbRaceInfoList.isEmpty() )
		{
			// 速報リストは一つだけのはず
			raceInfo = getRaceInfoBydbRaceInfo( dbRaceInfoList.get(0) );
		}

		return raceInfo;
	}

	/**
	 * 指定の大会を速報状態にする
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 */
	public static void setUpdateOnRaceId( ContentResolver contentResolver, String raceId )
	{
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate( contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON );
		return;
	}

	/**
	 * 指定の大会の速報状態を停止する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 */
	public static void setUpdateOffRaceId( ContentResolver contentResolver, String raceId )
	{
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate( contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF );
		return;
	}

	/**
	 * 指定の大会の速報状態を予約にする
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 */
	public static void setUpdateReserveRaceId( ContentResolver contentResolver, String raceId )
	{
		// 指定の大会を予約状態にする
		DataBaseAccess.setRaceUpdate( contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_RESERVE );
		return;
	}

	/**
	 * 指定の大会IDが登録済みかどうかを検索する
	 * @param raceId 大会ID
	 * @return false:未登録/true:登録済み
	 */
	public static boolean checkEntryRaceId( ContentResolver contentResolver, String raceId )
	{
		// 大会情報取得
		DataBaseRaceInfo info = DataBaseAccess.getRaceInfoByRaceId( contentResolver, raceId );

		if( info == null )
		{
			return false;
		}

		return true;
	}

	/**
	 * ネットワークから大会情報を取得する
	 * @param url アップデートサイトURL
	 * @param pass パス
	 * @param parserClassName パーサークラス名
	 * @return　取得した大会情報
	 * @throws LogicException 大会情報取得失敗
	 */
	public static RaceInfo getNetRaceInfo( String url, String pass, String parserClassName ) throws LogicException
	{
		try
		{
			IParserUpdate parser = ( IParserUpdate )Class.forName( parserClassName ).newInstance();

			// 大会情報取得
			ParserRaceInfo parserRaceInfo = parser.getRaceInfo( url, pass );

			// 大会情報設定
			RaceInfo raceInfo = new RaceInfo();
			raceInfo.id = pass;
			raceInfo.name = parserRaceInfo.name;
			raceInfo.date = parserRaceInfo.date;
			raceInfo.location = parserRaceInfo.location;
			raceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;

			return raceInfo;
		}
		catch( ParserException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch( InstantiationException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
	}

	/**
	 * ネットワークから選手情報を取得する
	 * @param url アップデートサイトのURL
	 * @param pass パス
	 * @param number ゼッケン番号
	 * @param parserClassName パーサークラス名
	 * @throws LogicException 選手情報取得失敗
	 */
	public static RunnerInfo getNetRunnerInfo( String url, String pass, String number, String parserClassName ) throws LogicException
	{
		try
		{
			IParserUpdate parser = ( IParserUpdate )Class.forName( parserClassName ).newInstance();

			// 選手情報取得
			ParserRunnerInfo parserRunnerInfo = parser.getRunnerInfo( url, pass, number );

			//　選手情報設定
			RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.name = parserRunnerInfo.name;
			runnerInfo.number = parserRunnerInfo.number;
			runnerInfo.section = parserRunnerInfo.section;
			for( ParserRunnerInfo.TimeInfo timelist : parserRunnerInfo.timeList )
			{
				RunnerInfo.TimeInfo infoTimeList = new RunnerInfo().new TimeInfo();
				infoTimeList.point = timelist.point;
				infoTimeList.split = timelist.split;
				infoTimeList.lap = timelist.lap;
				infoTimeList.currentTime = timelist.currentTime;
				
				runnerInfo.timeInfoList.add( infoTimeList );
			}

			return runnerInfo;
		}
		catch( ParserException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch ( InstantiationException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
			throw new LogicException( e.getMessage() );
		}
	}

	/**
	 * ネットワークから指定の大会のゼッケン番号の選手情報を取得する
	 * @param url　アップデートサイトURL
	 * @param pass 大会ID
	 * @param runnerList 選手情報リスト
	 * @param parserClassName パーサークラス名
	 * @return 選手情報リスト
	 */
	public static List<RunnerInfo> getNetRunnerInfoList( String url, String pass, List<RunnerInfo> runnerInfoList, String parserClassName )
	{
		List<RunnerInfo> netRunnerInfoList = new ArrayList<RunnerInfo>();

		for( RunnerInfo runnerInfo : runnerInfoList )
		{
			RunnerInfo netRunnerInfo = null;

			try
			{
				netRunnerInfo = getNetRunnerInfo( url, pass, runnerInfo.number, parserClassName );
			}
			catch( LogicException e )
			{
				// 取得に失敗した場合は、ゼッケンNoのみの要素を作成
				e.printStackTrace();
				netRunnerInfo = new RunnerInfo();
				netRunnerInfo.number = runnerInfo.number;
			}

			netRunnerInfoList.add( netRunnerInfo );
		}

		return netRunnerInfoList;
	}

	/**
	 * 指定の選手情報とデータベースの選手情報を比較する。
	 * 指定の選手情報の多いデータを更新する。
	 * 指定の選手情報とデータベースの選手情報の数は同じとする
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会情報
	 * @param runnerInfoList 選手情報リスト
	 * @return true:更新データあり、false:更新データなし
	 */
	public static boolean updateRunnerInfo( ContentResolver contentResolver, String raceId, List<RunnerInfo> newRunnerInfoList )
	{
		boolean updateFlg = false;

		// 本来ならば、引数の選手情報リストの選手を取得するべき
		// ただし、ここはNOTFinish の選手を取得するときのみ呼ばれる。
		// データベースアクセスの観点から、このインターフェースを使用する。
		List<RunnerInfo> oldRunnerInfoList = getRunnerInfoNOTFinish( contentResolver, raceId );
		for( int i = 0; i < oldRunnerInfoList.size(); i++ )
		{
			RunnerInfo newInfo = newRunnerInfoList.get( i );
			RunnerInfo oldInfo = oldRunnerInfoList.get( i );

			int newInfoTimeListSize = newInfo.timeInfoList.size();
			int oldInfoTimeListSize = oldInfo.timeInfoList.size();

			// タイムリストが更新されているならば、データベースに書き込み
			if( newInfoTimeListSize > oldInfoTimeListSize )
			{
				int updateCnt = newInfoTimeListSize - oldInfoTimeListSize;

				for( int j = 0; j < updateCnt; j++ )
				{
					String point = newInfo.timeInfoList.get( oldInfoTimeListSize + j ).point;
					String split = newInfo.timeInfoList.get( oldInfoTimeListSize + j ).split;
					String lap = newInfo.timeInfoList.get( oldInfoTimeListSize + j ).lap;
					String currentTime = newInfo.timeInfoList.get( oldInfoTimeListSize + j ).currentTime;

					// 日付設定
					String dateStr = getDateString();

					// タイムリスト書き込み
					DataBaseTimeList dbTimeList = new DataBaseTimeList();
					dbTimeList.raceId = raceId;
					dbTimeList.number = newInfo.number;
					dbTimeList.point = point;
					dbTimeList.split = split;
					dbTimeList.lap = lap;
					dbTimeList.currentTime = currentTime;
					dbTimeList.updateDate = dateStr;
					DataBaseAccess.entryTimeList( contentResolver, dbTimeList );

					// 速報データ書き込み
					DataBaseUpdateData dbUpdateData = new DataBaseUpdateData();
					dbUpdateData.raceId = raceId;
					dbUpdateData.name = newInfo.name;
					dbUpdateData.number = newInfo.number;
					dbUpdateData.section = newInfo.section;
					dbUpdateData.point = point;
					dbUpdateData.split = split;
					dbUpdateData.lap = lap;
					dbUpdateData.currentTime = currentTime;
					dbUpdateData.updateDate = dateStr;
					DataBaseAccess.entryUpdateData( contentResolver, dbUpdateData );
				}

				updateFlg = true;
			}
			
			// 登録時、部門が設定されていない場合があるので、更新時に確認する
			if( ( oldInfo.section == null ) || ( oldInfo.section.equals( "" ) ) )
			{
				DataBaseAccess.setRunnerSection( contentResolver, raceId, newInfo.number, newInfo.section );
			}
		}

		return updateFlg;
	}

	/**
	 * 選手情報を追加する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceInfo 大会情報
	 * @param runnerInfo 選手情報
	 */
	public static void entryRunnerInfo( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo )
	{
		// 登録情報設定
		DataBaseRunnerInfo dbRunnerInfo = new DataBaseRunnerInfo();

		dbRunnerInfo.raceId = raceInfo.id;
		dbRunnerInfo.name = runnerInfo.name;
		dbRunnerInfo.number = runnerInfo.number;
		dbRunnerInfo.section = runnerInfo.section;
		dbRunnerInfo.updateDate = getDateString();

		// データベース登録
		DataBaseAccess.entryRunner( contentResolver, dbRunnerInfo );

		return;
	}

	/**
	 * 選手情報リストを取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return 選手情報リスト
	 */
	public static List<RunnerInfo> getRunnerInfoList( ContentResolver contentResolver, String raceId )
	{
		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId( contentResolver, raceId );

		List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();

		for( DataBaseRunnerInfo dbRunnerInfo : dbRunnerInfoList )
		{
			RunnerInfo runnerInfo = getRunnerInfoByDBRunnerInfo( contentResolver, dbRunnerInfo );
			runnerInfoList.add( runnerInfo );
		}

		return runnerInfoList;
	}

	/**
	 * 指定のゼッケン番号が登録済みかどうかを検索する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceInfo 大会情報
	 * @param runnerInfo 選手情報
	 * @return　false 未登録、true 登録済み
	 */
	public static boolean checkEntryRunnerId( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo )
	{
		// 選手情報未取得
		DataBaseRunnerInfo info = DataBaseAccess.getRunnerInfoByRaceIdAndNumber( contentResolver, raceInfo.id, runnerInfo.number );
		
		if( info == null )
		{
			return false;
		}

		return true;
	}

	/**
	 * 大会IDとゼッケン番号から選手情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @param number ゼッケン番号
	 * @return 選手情報
	 */
	public static RunnerInfo getRunnerInfo( ContentResolver contentResolver, String raceId, String number)
	{
		// 選手情報取得
		DataBaseRunnerInfo dbRunnerInfo = DataBaseAccess.getRunnerInfoByRaceIdAndNumber( contentResolver, raceId, number );

		RunnerInfo runnerInfo = getRunnerInfoByDBRunnerInfo( contentResolver, dbRunnerInfo );

		return runnerInfo;
	}

	/**
	 * Finishを通過していない選手情報取得
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @return Finishを通過していない選手情報
	 */
	public static List<RunnerInfo> getRunnerInfoNOTFinish( ContentResolver contentResolver, String raceId )
	{
		List<RunnerInfo> runnerInfoNOTFinishList = new ArrayList<RunnerInfo>();

		List<RunnerInfo> runnerInfoList = getRunnerInfoList( contentResolver, raceId );

		for( RunnerInfo runnerInfo : runnerInfoList )
		{
			List<DataBaseTimeList> dbFinishTimeList = DataBaseAccess.getTimeListByPoint( contentResolver, raceId, runnerInfo.number, "Finish" );

			if( dbFinishTimeList.isEmpty() )
			{
				runnerInfoNOTFinishList.add( runnerInfo );
			}
		}

		return runnerInfoNOTFinishList;
	}

	/**
	 * 選手情報を削除する
	 * @param contentResolver コンテントリゾルバ
	 * @param runnerInfo 選手情報
	 */
	public static void deleteRunnerInfo( ContentResolver contentResolver, String raceId, String number )
	{
		// 速報リスト削除
		DataBaseAccess.deleteUpdateDataByRaceIdAndNumber( contentResolver, raceId, number );

		// タイムリスト削除
		DataBaseAccess.deleteTimeListByRaceIdAndNumber( contentResolver, raceId, number );

		// 選手情報削除
		DataBaseAccess.deleteRunnerInfoByRaceIdAndNumber( contentResolver, raceId, number );

		return;
	}

	/**
	 * 現在の時刻の文字列を取得する
	 * @return 現在の時刻
	 */
	private static String getDateString()
	{
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return DATEFORMAT.format( date );
	}

	/**
	 * 速報情報を取得する( 新しい順 )
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @param recentTime 現在の日付からこの時間前ならば、recentFlgがtrue( ミリ秒 )
	 * @return 速報情報
	 */
	public static List<UpdateInfo> getUpdateInfoList( ContentResolver contentResolver, String raceId, long recentTime )
	{
		List<DataBaseUpdateData> dbUpdateDataList = DataBaseAccess.getUpdateDataByRaceId( contentResolver, raceId );

		List<UpdateInfo> updateInfoList = new ArrayList<UpdateInfo>();

		// 現在の時刻の秒取得
		Calendar cal = Calendar.getInstance();
		Date nowDate = cal.getTime();
		long nowTime = nowDate.getTime();

		for( DataBaseUpdateData dbUpdateData: dbUpdateDataList )
		{
			UpdateInfo updateInfo = new UpdateInfo();

			updateInfo.name = dbUpdateData.name;
			updateInfo.number = dbUpdateData.number;
			updateInfo.section = dbUpdateData.section;
			updateInfo.point = dbUpdateData.point;
			updateInfo.split = dbUpdateData.split;
			updateInfo.lap = dbUpdateData.lap;
			updateInfo.currentTime = dbUpdateData.currentTime;

			try
			{
				Date date = DATEFORMAT.parse( dbUpdateData.updateDate );
				long updateTime = date.getTime();

				if( nowTime - updateTime < recentTime )
				{
					updateInfo.recentFlg = true;
				}
				else
				{
					updateInfo.recentFlg =  false;
				}
			}
			catch( ParseException e )
			{
				e.printStackTrace();
				updateInfo.recentFlg =  false;
			}

			updateInfoList.add( updateInfo );
		}

		// スプリット順に並べ替え
		Collections.sort( updateInfoList, new UpdateInfoSplitComparator() );

		return updateInfoList;
	}

	/**
	 * 部門ごとの選手情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @param noSectionName 部門未取得の場合に格納する選手リストの部門名
	 * @return 部門選手情報
	 */
	public static List<SectionRunnerInfo> getSectionRunnerInfo( ContentResolver contentResolver, String raceId, String noSectionName )
	{
		List<SectionRunnerInfo> sectionRunnerInfoList = new ArrayList<SectionRunnerInfo>();

		// 選手リストを取得
		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId( contentResolver, raceId );

		boolean searchFlg;

		for( DataBaseRunnerInfo dbRunnerInfo : dbRunnerInfoList )
		{
			searchFlg = false;

			RunnerInfo runnerInfo = getRunnerInfoByDBRunnerInfo( contentResolver, dbRunnerInfo );

			for( SectionRunnerInfo sectionRunnerInfo : sectionRunnerInfoList )
			{
				// 部門名が一致する
				if( sectionRunnerInfo.section.equals( dbRunnerInfo.section ) )
				{
					sectionRunnerInfo.runnerInfoList.add( runnerInfo );
					searchFlg = true;
					break;
				}

				// 部門名がない場合
				if( sectionRunnerInfo.section.equals( noSectionName ) )
				{
					if( ( dbRunnerInfo.section == null ) || dbRunnerInfo.section.equals( "" ) )
					{
						sectionRunnerInfo.runnerInfoList.add( runnerInfo );
						searchFlg = true;
						break;
					}
				}
			}

			// 部門が未登録なら新しく登録する
			if( !searchFlg )
			{
				SectionRunnerInfo sectionRunnerInfo = new SectionRunnerInfo();

				if( ( dbRunnerInfo.section == null ) || dbRunnerInfo.section.equals( "" ) )
				{
					sectionRunnerInfo.section = noSectionName;
					sectionRunnerInfo.runnerInfoList.add( runnerInfo );
					sectionRunnerInfoList.add( sectionRunnerInfo );
				}
				else
				{
					sectionRunnerInfo.section = dbRunnerInfo.section;
					sectionRunnerInfo.runnerInfoList.add( runnerInfo );
					sectionRunnerInfoList.add( 0, sectionRunnerInfo );
				}
			}
		}

		return sectionRunnerInfoList;
	}

	/**
	 * 名前から選手情報を検索する
	 * @param url アップデートサイトURL
	 * @param raceId 大会ID
	 * @param sei 姓
	 * @param mei 名
	 * @param parserClassName パーサークラス名
	 * @return 選手情報
	 */
	public static List<RunnerInfo> searchRunnerInfoByName( String url, String raceId, String sei, String mei, String parserClassName )
	{
		List<RunnerInfo> runnerInfoList = null;

		List<ParserRunnerInfo> parserRunnerInfoList = null;

		try
		{
			IParserUpdate parser = ( IParserUpdate )Class.forName( parserClassName ).newInstance();

			parserRunnerInfoList = parser.searchRunnerInfoByName( url, raceId, sei, mei );

			runnerInfoList = new ArrayList<RunnerInfo>();

			for( ParserRunnerInfo parserRunnerInfo : parserRunnerInfoList )
			{
				RunnerInfo runnerInfo = new RunnerInfo();
				runnerInfo.number = parserRunnerInfo.number;
				runnerInfo.name = parserRunnerInfo.name;
				runnerInfoList.add( runnerInfo );
			}
		}
		catch( ParserException e )
		{
			e.printStackTrace();
		}
		catch( InstantiationException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}

		return runnerInfoList;
	}

	/**
	 * データベース選手情報から選手情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @param dbRunnerInfo データベース選手情報
	 * @return 選手情報
	 */
	private static RunnerInfo getRunnerInfoByDBRunnerInfo( ContentResolver contentResolver, DataBaseRunnerInfo dbRunnerInfo )
	{
		RunnerInfo runnerInfo = new RunnerInfo();
		runnerInfo.name = dbRunnerInfo.name;
		runnerInfo.number = dbRunnerInfo.number;
		runnerInfo.section = dbRunnerInfo.section;

		List<DataBaseTimeList> dbTimelistList = DataBaseAccess.getTimeListByRaceIdAndNumber( contentResolver, dbRunnerInfo.raceId, dbRunnerInfo.number );

		for( DataBaseTimeList dbTimeList : dbTimelistList )
		{
			RunnerInfo.TimeInfo timeInfo = new RunnerInfo().new TimeInfo();
			timeInfo.point = dbTimeList.point;
			timeInfo.split = dbTimeList.split;
			timeInfo.lap = dbTimeList.lap;
			timeInfo.currentTime = dbTimeList.currentTime;

			runnerInfo.timeInfoList.add( timeInfo );
		}
		return runnerInfo;
	}

	/**
	 * 地点選手情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @param raceId 大会ID
	 * @param recentTime　最近の時間
	 * @return 地点選手情報
	 */
	public static List<PassRunnerInfo> getPassRunnerInfoList( ContentResolver contentResolver, String raceId, long recentTime )
	{
		List<PassRunnerInfo> passRunnerInfoList = new ArrayList<PassRunnerInfo>();

		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId( contentResolver, raceId );

		// 現在の時刻の秒取得
		Calendar cal = Calendar.getInstance();
		Date nowDate = cal.getTime();
		long nowTime = nowDate.getTime();

		for( DataBaseRunnerInfo dbInfo : dbRunnerInfoList )
		{
			RunnerInfo runnerInfo = getRunnerInfoByDBRunnerInfo( contentResolver, dbInfo );

			// タイムリストが空なら何もしない
			if( runnerInfo.timeInfoList.size() == 0 )
			{
				continue;
			}

			//　部門検索
			PassRunnerInfo passRunnerInfoCurrent = null;
			String section = runnerInfo.section;
			boolean searchFlg = false;

			for( PassRunnerInfo passRunnerInfo : passRunnerInfoList )
			{
				if( passRunnerInfo.getSection().equals( section ) )
				{
					searchFlg = true;
					passRunnerInfoCurrent = passRunnerInfo;
				}
			}

			// 部門が見つからないならば、新しく追加
			if( searchFlg == false )
			{
				passRunnerInfoCurrent = new PassRunnerInfo();
				passRunnerInfoCurrent.setSection( section );
				passRunnerInfoList.add( passRunnerInfoCurrent );
			}

			// 通過地点のインデックス取得
			int pointIdx = runnerInfo.timeInfoList.size() - 1;

			RunnerInfo.TimeInfo timeList = runnerInfo.timeInfoList.get( pointIdx );

			// 選手情報設定
			PassRunnerInfo.PassPointInfo.PassPointRunnerInfo passPointRunnerInfo = new PassRunnerInfo().new PassPointInfo().new PassPointRunnerInfo();
			passPointRunnerInfo.setName( runnerInfo.name );
			passPointRunnerInfo.setNumber( runnerInfo.number );
			passPointRunnerInfo.setSplit( timeList.split );
			passPointRunnerInfo.setLap( timeList.lap );
			passPointRunnerInfo.setCurrentTime( timeList.currentTime );

			try {
				Date date = DATEFORMAT.parse( dbInfo.updateDate );
				long updateTime = date.getTime();

				if( nowTime - updateTime < recentTime )
				{
					passPointRunnerInfo.setRecentFlg( true );
				}
				else
				{
					passPointRunnerInfo.setRecentFlg( false );
				}
			}
			catch( ParseException e )
			{
				e.printStackTrace();
				passPointRunnerInfo.setRecentFlg( false );
			}

			// 地点情報検索
			int idx = -1;
			List<PassRunnerInfo.PassPointInfo> list = passRunnerInfoCurrent.getPassPointInfo();
			for( int i = 0; i < list.size(); i++ )
			{
				PassRunnerInfo.PassPointInfo passPointInfo = list.get( i );

				if( passPointInfo.getPoint().equals( timeList.point ) )
				{
					idx = i;
				}
			}

		 	if( idx == -1 )
			{
				// 新規追加
				PassRunnerInfo.PassPointInfo passPointInfo = new PassRunnerInfo().new PassPointInfo();
				passPointInfo.setPoint( timeList.point );
				passPointInfo.setPassPointNo( pointIdx );
				passPointInfo.getPassPointRunnerInfoList().add( passPointRunnerInfo );
				list.add( passPointInfo );
			}
			else
			{
				list.get( idx ).getPassPointRunnerInfoList().add( passPointRunnerInfo );
			}
		}

		for( PassRunnerInfo passRunnerInfo : passRunnerInfoList )
		{
			Collections.sort( passRunnerInfo.getPassPointInfo(), new PassPointInfoComparator() );

			// タイム順に並び替え
			for( PassRunnerInfo.PassPointInfo info : passRunnerInfo.getPassPointInfo() )
			{
				Collections.sort( info.getPassPointRunnerInfoList(), new PassPointInfoSplitComparator() );
			}
		}

		return passRunnerInfoList;
	}

	/**
	 * 自動停止カウントの設定をする
	 * @param context コンテキスト
	 * @param autoStopCount 自動停止カウント値
	 */
	public static void setAutoStopCount( Context context, int autoStopCount )
	{
		PreferenceStopCount.deleteStopCount( context, PreferenceStopCount.KEY_AUTOSTOPCOUNT );
		PreferenceStopCount.saveStopCount( context, PreferenceStopCount.KEY_AUTOSTOPCOUNT, autoStopCount );
		return;
	}

	/**
	 * 定期停止カウントの設定をする
	 * @param context コンテキスト
	 * @param regularStopCount 定期停止カウント値
	 */
	public static void setRegularStopCount( Context context, int regularStopCount )
	{
		PreferenceStopCount.deleteStopCount( context, PreferenceStopCount.KEY_REGULARSTOPCOUNT );
		PreferenceStopCount.saveStopCount( context, PreferenceStopCount.KEY_REGULARSTOPCOUNT, regularStopCount );
		return;
	}

	/**
	 * 自動停止カウントの更新
	 * @param context コンテキスト
	 * @return true:自動停止カウントが0以下/ false:自動停止カウントが0より大きい
	 * @throws LogicException
	 */
	public static boolean updateAutoStopCount( Context context ) throws LogicException
	{
		return updateStopCount( context, PreferenceStopCount.KEY_AUTOSTOPCOUNT );
	}

	/**
	 * 定期停止カウントの更新
	 * @param context コンテキスト
	 * @return true:定期停止カウントが0以下/ false:定期停止カウントが0より大きい
	 * @throws LogicException
	 */
	public static boolean updateRegularStopCount( Context context ) throws LogicException
	{
		return updateStopCount( context, PreferenceStopCount.KEY_REGULARSTOPCOUNT );
	}

	/**
	 * 停止カウントを更新する
	 *  停止カウントをデクリメントする。
	 * @param context　コンテキスト
	 * @param key キー
	 * @return　true:停止カウントが0以下/ false:自動停止カウントが0以上
	 * @throws LogicException 停止カウントが存在しないものがある
	 */
	private static boolean updateStopCount( Context context, String key ) throws LogicException
	{
		// 停止カウント
		int stopCount = PreferenceStopCount.loadAutoStopCount( context, key );

		if( stopCount == Integer.MAX_VALUE )
		{
			throw new LogicException( "no RegularStopCount" );
		}

		// 停止カウントデクリメント
		stopCount--;

		// 停止カウント保存
		PreferenceStopCount.saveStopCount( context, key, stopCount );

		if( stopCount <= 0 )
		{
			return true;
		}

		return false;
	}

	/**
	 * 予約時間を設定する
	 * @param context コンテキスト
	 * @param hour 時
	 * @param minute 分
	 */
	public static void setReserveTime( Context context, int hour, int minute )
	{
		PreferenceReserveTime.deleteReserveTime( context );

		PreferenceReserveTime.ReserveTime time = new PreferenceReserveTime().new ReserveTime();
		time.hour = hour;
		time.minute = minute;
		PreferenceReserveTime.saveReserveTime( context, time );
		return;
	}

	/**
	 * 予約時間をHH:MM 形式で取得する
	 * @param context コンテキスト
	 * @return 予約時間
	 * @throws LogicException 予約時間未設定
	 */
	public static String getStringReserveTime( Context context ) throws LogicException
	{
		PreferenceReserveTime.ReserveTime time = PreferenceReserveTime.loadReserveTime( context );

		int hour = time.hour;
		int minute = time.minute;

		if( ( hour == Integer.MAX_VALUE) || ( minute == Integer.MAX_VALUE ) )
		{
			throw new LogicException( "no ReserveTime" );
		}

		// 2桁で0埋めする
		StringBuilder builder = new StringBuilder();
		builder.append( String.format("%02d", hour ) );
		builder.append( ":" );
		builder.append( String.format("%02d", minute ) );

		return builder.toString();
	}

	/**
	 * 地点情報番号比較
	 * @author Hayato Matsumuro
	 *
	 */
	private static class PassPointInfoComparator implements Comparator<PassRunnerInfo.PassPointInfo>
	{
		@Override
		public int compare( PassRunnerInfo.PassPointInfo o1, PassRunnerInfo.PassPointInfo o2 )
		{
			if( o1.getPassPointNo() < o2.getPassPointNo() )
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	/**
	 * 地点情報スプリット比較
	 * @author Hayato Matsumuro
	 *
	 */
	private static class PassPointInfoSplitComparator implements Comparator<PassRunnerInfo.PassPointInfo.PassPointRunnerInfo>
	{
		@Override
		public int compare( PassRunnerInfo.PassPointInfo.PassPointRunnerInfo o1, PassRunnerInfo.PassPointInfo.PassPointRunnerInfo o2 )
		{
			if( o1.getSplitLong() > o2.getSplitLong() )
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}

	/**
	 * 速報情報スプリット比較
	 * @author Hayato Matsumuro
	 *
	 */
	private static class UpdateInfoSplitComparator implements Comparator<UpdateInfo>
	{
		@Override
		public int compare( UpdateInfo o1, UpdateInfo o2 )
		{
			if( o1.getSplitLong() < o2.getSplitLong() )
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}
}
