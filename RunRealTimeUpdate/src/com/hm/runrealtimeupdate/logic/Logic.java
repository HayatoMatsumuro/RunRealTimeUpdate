package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;

import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseTimeList;
import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.ParserRaceInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnersUpdate;

public class Logic {

	/**
	 * 大会情報リスト
	 */
	private static List<RaceInfo> m_RaceInfoList = null;
	
	/**
	 * 選手情報リスト
	 */
	private static List<RunnerInfo> m_RunnerInfoList = null;
	
	/**
	 * 選択中の大会情報
	 */
	private static RaceInfo m_SelectRaceInfo = null;
	
	/**
	 * 速報中の大会情報
	 */
	private static String m_RaceIdUpdate = null;
	
	/**
	 * 選択中の選手情報
	 */
	private static RunnerInfo m_SelectRunnerInfo = null;
	
	/**
	 * 大会情報を登録する
	 * @param contentResolver　コンテントリゾルバ
	 * @param raceInfo 大会情報
	 */
	public static void entryRaceInfo( ContentResolver contentResolver, RaceInfo raceInfo ){
		
		// 登録情報設定
		DataBaseRaceInfo dbRaceInfo = new DataBaseRaceInfo();
		
		dbRaceInfo.setRaceId(raceInfo.getRaceId());
		dbRaceInfo.setRaceName(raceInfo.getRaceName());
		dbRaceInfo.setRaceDate(raceInfo.getRaceDate());
		dbRaceInfo.setRaceLocation(raceInfo.getRaceLocation());
		dbRaceInfo.setUpdateFlg(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
		
		// データベース登録
		DataBaseAccess.entryRace(contentResolver, dbRaceInfo);
		
		if( m_RaceInfoList != null ){
			m_RaceInfoList.add(raceInfo);
		}
	}
	
	/**
	 * 大会情報を取得する
	 * @param contentResolver コンテントリゾルバ
	 * @return
	 */
	public static List<RaceInfo> getRaceInfoList( ContentResolver contentResolver){
		
		// 大会情報リスト未取得
		if( m_RaceInfoList == null ){
			
			List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getAllRaceInfo(contentResolver);
			
			m_RaceInfoList = new ArrayList<RaceInfo>();
			
			for( DataBaseRaceInfo dbRaceInfo: dbRaceInfoList ){
				// 大会情報取得
				RaceInfo raceInfo = new RaceInfo();
				raceInfo.setRaceId(dbRaceInfo.getRaceId());
				raceInfo.setRaceName(dbRaceInfo.getRaceName());
				raceInfo.setRaceDate(dbRaceInfo.getRaceDate());
				raceInfo.setRaceLocation(dbRaceInfo.getRaceLocation());
				
				m_RaceInfoList.add(raceInfo);
				// 速報中ならば、ID保持
				if(dbRaceInfo.getUpdateFlg().equals(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON)){
					m_RaceIdUpdate = dbRaceInfo.getRaceId();
				}
			}
		}
		
		return m_RaceInfoList;
	}
	
	/**
	 * 速報中の大会IDを取得する
	 *  大会IDがない場合、または大会情報を未取得ならばnull を返す
	 * @return 速報中の大会ID
	 */
	public static String getUpdateRaceId(){
		return m_RaceIdUpdate;
	}
	
	/**
	 * 指定の大会IDが速報中かどうか判定する
	 * @param raceId 大会ID
	 * @return　true:速報中、false:速報中でない
	 */
	public static boolean checkRaceIdUpdate( String raceId ){
		
		if( m_RaceIdUpdate == null ){
			// 速報中の大会がない
			return false;
		}else{
			// 速報中の大会あり
			return m_RaceIdUpdate.equals(raceId);
		}
	}
	
	/**
	 * 指定の大会を速報状態にする
	 * @param raceId
	 */
	public static void setUpdateOnRaceId( ContentResolver contentResolver, String raceId){
		
		// 速報中の大会IDがあるなら速報状態を停止する( ないはず )
		if( m_RaceIdUpdate != null ){
			DataBaseAccess.setRaceUpdate(contentResolver, m_RaceIdUpdate, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
			m_RaceIdUpdate = null;
		}
		
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate(contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON);
		m_RaceIdUpdate = raceId;
		
		return;
	}
	
	/**
	 * 指定の大会の速報状態を停止する
	 * @param contentResolver 
	 * @param raceId 大会ID
	 */
	public static void setUpdateOffRaceId(ContentResolver contentResolver, String raceId){
		
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate(contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
		m_RaceIdUpdate = null;
				
		return;
	}
	
	/**
	 * 選択中の大会情報を設定する
	 * @param raceInfo 大会情報
	 */
	public static void setSelectRaceInfo( RaceInfo raceInfo){
		m_SelectRaceInfo = raceInfo;
		
		if( m_RunnerInfoList != null){
			m_RunnerInfoList.clear();
			m_RunnerInfoList = null;
		}
		return;
	}
	
	/**
	 * 選択中の大会情報を取得する
	 * @return 大会情報
	 */
	public static RaceInfo getSelectRaceInfo(){
		return m_SelectRaceInfo;
	}
	
	/**
	 * 指定の大会IDが登録済みかどうかを検索する
	 * @param raceId 大会ID
	 * @return false 未登録、true 登録済み
	 */
	public static boolean checkEntryRaceId( ContentResolver contentResolver, String raceId){
		
		if( m_RaceInfoList == null ){
			// 大会情報未取得
			DataBaseRaceInfo info = DataBaseAccess.getRaceInfoByRaceId(contentResolver, raceId);
			
			if( info == null ){
				return false;
			}else{
				return true;
			}
		}else{
			// 大会情報取得済み
			for( RaceInfo raceInfo:m_RaceInfoList){
				if( raceInfo.getRaceId().equals(raceId)){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * ネットワークから大会情報を取得する
	 * @param raceId 大会ID
	 * @return　取得した大会情報
	 * @throws LogicException 大会情報取得失敗
	 */
	public static RaceInfo getNetRaceInfo( String raceId ) throws LogicException{
		
		try {
			// 大会情報取得
			ParserRaceInfo parserRaceInfo = ParserRunnersUpdate.getRaceInfo(raceId);
			
			// 大会情報設定
			RaceInfo raceInfo = new RaceInfo();
			raceInfo.setRaceId(raceId);
			raceInfo.setRaceName(parserRaceInfo.getName());
			raceInfo.setRaceDate(parserRaceInfo.getDate());
			raceInfo.setRaceLocation(parserRaceInfo.getLocation());
			
			return raceInfo;
		} catch (ParserException e) {
			e.printStackTrace();
			throw new LogicException(e.getMessage());
		}
	}
	
	/**
	 * ネットワークから選手情報を取得する
	 * @param number ゼッケン番号
	 */
	public static RunnerInfo getNetRunnerInfo( String number ) throws LogicException{
		try {
			// 選手情報取得
			ParserRunnerInfo parserRunnerInfo = ParserRunnersUpdate.getRunnerInfo(m_SelectRaceInfo.getRaceId(), number);
			
			//　選手情報設定
			RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.setName(parserRunnerInfo.getName());
			runnerInfo.setNumber(parserRunnerInfo.getNumber());
			runnerInfo.setSection(parserRunnerInfo.getSection());
			for( ParserRunnerInfo.TimeList timelist:parserRunnerInfo.getTimeList()){
				RunnerInfo.TimeList infoTimeList = new RunnerInfo().new TimeList();
				infoTimeList.setPoint(timelist.getPoint());
				infoTimeList.setSplit(timelist.getSplit());
				infoTimeList.setLap(timelist.getLap());
				infoTimeList.setCurrentTime(timelist.getCurrentTime());
				
				runnerInfo.getTimeList().add(infoTimeList);
			}
			
			return runnerInfo;
		} catch (ParserException e) {
			e.printStackTrace();
			throw new LogicException(e.getMessage());
		}
	}
	
	public static void entryRunnerInfo( ContentResolver contentResolver, RunnerInfo runnerInfo ){
		
		// 登録情報設定
		DataBaseRunnerInfo dbRunnerInfo = new DataBaseRunnerInfo();

		dbRunnerInfo.setRaceId(m_SelectRaceInfo.getRaceId());
		dbRunnerInfo.setName(runnerInfo.getName());
		dbRunnerInfo.setNumber(runnerInfo.getNumber());
		dbRunnerInfo.setSection(runnerInfo.getSection());
		
		// データベース登録
		DataBaseAccess.entryRunner(contentResolver, dbRunnerInfo );
		
		if( m_RunnerInfoList != null ){
			m_RunnerInfoList.add(runnerInfo);
		}
	}
	
	/**
	 * 選択中の大会情報を取得する
	 * @return
	 */
	public static List<RunnerInfo> getRunnerInfoList( ContentResolver contentResolver ){
		
		// 選手情報未取得
		if( m_RunnerInfoList == null ){
			
			List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(contentResolver, m_SelectRaceInfo.getRaceId());
			
			m_RunnerInfoList = new ArrayList<RunnerInfo>();
			
			for( DataBaseRunnerInfo dbRunnerInfo: dbRunnerInfoList ){
				RunnerInfo runnerInfo = new RunnerInfo();
				
				runnerInfo.setName(dbRunnerInfo.getName());
				runnerInfo.setNumber(dbRunnerInfo.getNumber());
				runnerInfo.setSection(dbRunnerInfo.getSection());
				
				List<DataBaseTimeList> dbTimeListList = DataBaseAccess.getTimeListByRaceIdandNo(contentResolver, m_SelectRaceInfo.getRaceId(), dbRunnerInfo.getNumber());
				
				for( DataBaseTimeList dbTimeList:dbTimeListList){
					RunnerInfo.TimeList timeList = new RunnerInfo().new TimeList();
					timeList.setPoint(dbTimeList.getPoint());
					timeList.setSplit(dbTimeList.getSplit());
					timeList.setLap(dbTimeList.getLap());
					timeList.setCurrentTime(dbTimeList.getCurrentTime());
					
					runnerInfo.getTimeList().add(timeList);
				}
				m_RunnerInfoList.add(runnerInfo);
			}
		}
		return m_RunnerInfoList;
	}
	
	/**
	 * 選択中の選手情報を設定する
	 * @param runnerInfo 選手情報
	 */
	public static void setSelectRunnerInfo( RunnerInfo runnerInfo ){
		m_SelectRunnerInfo = runnerInfo;
	}
	
	/**
	 * 指定のゼッケン番号が登録済みかどうかを検索する
	 * @param contentResolver
	 * @param number
	 * @return　false 未登録、true 登録済み
	 */
	public static boolean checkEntryRunnerId( ContentResolver contentResolver, String number ){
		
		if( m_RunnerInfoList == null ){
			// 選手情報未取得
			DataBaseRunnerInfo info = DataBaseAccess.getRunnerInfoByRaceIdandNumber(
					contentResolver,
					m_SelectRaceInfo.getRaceId(),
					m_SelectRunnerInfo.getNumber());
			
			if( info == null ){
				return false;
			}else{
				return true;
			}
		}else{
			// 大会情報取得済み
			for( RunnerInfo runnerInfo:m_RunnerInfoList){
				if( runnerInfo.getNumber().equals(number)){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * 選択中の選手情報を削除する
	 * @param contentResolver
	 */
	public static void deleteRunnerInfo( ContentResolver contentResolver ){
		
		// タイムリスト削除
		DataBaseAccess.deleteTimeListByRaceIdandNo(contentResolver, m_SelectRaceInfo.getRaceId(), m_SelectRunnerInfo.getNumber());
		
		// 選手情報削除
		DataBaseAccess.deleteRunnerInfoByNo(contentResolver, m_SelectRaceInfo.getRaceId(), m_SelectRunnerInfo.getNumber());
		
		m_RunnerInfoList.remove(m_SelectRunnerInfo);
		
		m_SelectRunnerInfo = null;
		
		return;
	}
	
	/**
	 * 選択中の選手情報を取得する
	 * @return 選手情報
	 */
	public static RunnerInfo getSelectRunnerInfo(){
		return m_SelectRunnerInfo;
	}
}
