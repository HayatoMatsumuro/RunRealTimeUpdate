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
import com.hm.runrealtimeupdate.logic.parser.ParserRunnersUpdate;

public class Logic {

	private static List<RaceInfo> m_RaceInfoList = null;
	
	private static List<RunnerInfo> m_RunnerInfoList = null;
	
	private static String m_RaceIdUpdate = null;
	
	private static RaceInfo m_RaceInfoExe = null;
	
	//private static List<RunnerInfo> m_RunnerInfoList = null;
	
	/**
	 * 大会情報を登録する
	 * @param contentResolver　コンテントリゾルバ
	 * @param raceInfo 大会情報
	 */
	public static void entryRaceInfo( ContentResolver contentResolver, RaceInfo raceInfo ){
		
		// TODO: 大会登録インターフェース変更後
		//DataBaseAccess.entryRace(contentResolver, raceId, raceName, raceDate, raceLocation);
		
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
	 * 選択中の大会情報を設定する
	 * @param raceInfo 大会情報
	 */
	public static void setRaceInfoExe( RaceInfo raceInfo){
		m_RaceInfoExe = raceInfo;
		
		if( m_RunnerInfoList != null){
			m_RunnerInfoList.clear();
			m_RunnerInfoList = null;
		}
		
		return;
	}
	
	/**
	 * 選択中の大会情報を取得する
	 * @return
	 */
	public static List<RunnerInfo> getRunnerInfoList( ContentResolver contentResolver ){
		
		if(m_RaceInfoExe == null){
			return null;
		}
		// 選手情報未取得
		if( m_RunnerInfoList != null ){
			
			List<DataBaseRunnerInfo> dbRunnerInfoList = new ArrayList<DataBaseRunnerInfo>();
			
			DataBaseAccess.getRunnerInfoByRaceId(contentResolver, m_RaceInfoExe.getRaceId());
			m_RunnerInfoList = new ArrayList<RunnerInfo>();
			
			for( DataBaseRunnerInfo dbRunnerInfo: dbRunnerInfoList ){
				RunnerInfo runnerInfo = new RunnerInfo();
				
				runnerInfo.setName(dbRunnerInfo.getName());
				runnerInfo.setNumber(dbRunnerInfo.getNumber());
				runnerInfo.setSection(dbRunnerInfo.getSection());
				
				List<DataBaseTimeList> dbTimeListList = DataBaseAccess.getTimeListByRaceIdandNo(contentResolver, m_RaceInfoExe.getRaceId(), dbRunnerInfo.getNumber());
				
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
	
}
