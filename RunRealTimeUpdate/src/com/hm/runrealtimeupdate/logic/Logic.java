package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;

import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseTimeList;
import com.hm.runrealtimeupdate.logic.dbaccess.DataBaseUpdateData;
import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.ParserRaceInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserRunnersUpdate;

public class Logic {
	
	/**
	 * 速報リスト
	 */
	private static List<UpdateInfo> m_UpdateInfoList = null;
	
	/**
	 * 部門リスト
	 */
	private static List<String> m_SectionList = null;
	
	/**
	 * 選択中の大会情報
	 */
	private static RaceInfo m_SelectRaceInfo = null;
	
	/**
	 * 選択中の選手情報
	 */
	private static RunnerInfo m_SelectRunnerInfo = null;
	
	/**
	 * 選択中の部門
	 */
	private static String m_SelectSection = null;
	
	
	private static List<RunnerInfo> m_NetRunnerInfoList = null;
	
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
		
	}
	
	/**
	 * 大会情報リストを取得する
	 * @param contentResolver コンテントリゾルバ
	 * @return
	 */
	public static List<RaceInfo> getRaceInfoList( ContentResolver contentResolver){
		
		List<RaceInfo> raceInfoList = new ArrayList<RaceInfo>();
		
		List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getAllRaceInfo(contentResolver);
			
		for( DataBaseRaceInfo dbRaceInfo: dbRaceInfoList ){
			// 大会情報取得
			RaceInfo raceInfo = getRaceInfoBydbRaceInfo(dbRaceInfo);
			raceInfoList.add(raceInfo);
		}
		
		return raceInfoList;
	}
	
	/**
	 * 大会IDの大会情報を取得する
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return
	 */
	public static RaceInfo getRaceInfo( ContentResolver contentResolver, String raceId ){
		
		DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(contentResolver, raceId);
		
		return getRaceInfoBydbRaceInfo(dbRaceInfo);
	}
	
	/**
	 * 大会情報を取得する
	 * @param dbRaceInfo
	 * @return
	 */
	private static RaceInfo getRaceInfoBydbRaceInfo( DataBaseRaceInfo dbRaceInfo ){
		RaceInfo raceInfo = new RaceInfo();
		raceInfo.setRaceId(dbRaceInfo.getRaceId());
		raceInfo.setRaceName(dbRaceInfo.getRaceName());
		raceInfo.setRaceDate(dbRaceInfo.getRaceDate());
		raceInfo.setRaceLocation(dbRaceInfo.getRaceLocation());
		
		if(dbRaceInfo.getUpdateFlg().equals(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON)){
			raceInfo.setRaceUpdate(true);
		}else{
			raceInfo.setRaceUpdate(false);
		}
		return raceInfo;
	}
	/**
	 * 大会情報を削除する
	 * @param contentResolver
	 * @param raceInfo 削除する大会情報
	 */
	public static void deleteRaceInfo( ContentResolver contentResolver, RaceInfo raceInfo ){
		
		// 速報データ
		DataBaseAccess.deleteUpdateDataByRaceId(contentResolver, raceInfo.getRaceId());
		
		// タイムリスト
		DataBaseAccess.deleteTimeListByRaceId(contentResolver, raceInfo.getRaceId());
		
		// 選手削除
		DataBaseAccess.deleteRunnerInfoByRaceId(contentResolver, raceInfo.getRaceId());
		
		// 大会を削除する
		DataBaseAccess.deleteRaceInfoByRaceId(contentResolver, raceInfo.getRaceId());
		
		return;
	}
	
	/**
	 * 速報中の大会IDを取得する
	 *  大会IDがない場合、または大会情報を未取得ならばnull を返す
	 * @return 速報中の大会ID
	 */
	public static RaceInfo getUpdateRaceId( ContentResolver contentResolver ){
		
		List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getUpdateExeRaceInfo( contentResolver );
		
		RaceInfo raceInfo = null;
		
		if( !dbRaceInfoList.isEmpty() ){
			// 速報リストは一つだけのはず
			raceInfo = getRaceInfoBydbRaceInfo(dbRaceInfoList.get(0));
		}
		return raceInfo;
	}
	
	/**
	 * 指定の大会IDが速報中かどうか判定する
	 * @param raceId 大会ID
	 * @return　true:速報中、false:速報中でない
	 */
	public static boolean checkRaceIdUpdate( String raceId ){
		
		//if( m_RaceIdUpdate == null ){
			// 速報中の大会がない
		//	return false;
		//}else{
			// 速報中の大会あり
		//	return m_RaceIdUpdate.equals(raceId);
		//}
		return true;
	}
	
	/**
	 * 指定の大会を速報状態にする
	 * @param raceId
	 */
	public static void setUpdateOnRaceId( ContentResolver contentResolver, RaceInfo raceInfo){
		
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate(contentResolver, raceInfo.getRaceId(), DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON);
		
		return;
	}
	
	/**
	 * 指定の大会の速報状態を停止する
	 * @param contentResolver 
	 * @param raceId 大会ID
	 */
	public static void setUpdateOffRaceId(ContentResolver contentResolver, RaceInfo raceInfo){
		
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate(contentResolver, raceInfo.getRaceId(), DataBaseAccess.STR_DBA_RACE_UPDATEFLG_OFF);
				
		return;
	}
	
	/**
	 * 選択中の大会情報を設定する
	 * @param raceInfo 大会情報
	 */
	public static void setSelectRaceInfo( RaceInfo raceInfo){
		m_SelectRaceInfo = raceInfo;
		
		// 速報リスト初期化
		if( m_UpdateInfoList != null ){
			m_UpdateInfoList.clear();
			m_UpdateInfoList = null;
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
	public static boolean checkEntryRaceId( ContentResolver contentResolver, String raceId ){
		
		// 大会情報未取得
		DataBaseRaceInfo info = DataBaseAccess.getRaceInfoByRaceId(contentResolver, raceId);
			
		if( info == null ){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * ネットワークから大会情報を取得する
	 * @param url URL
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
	 * @param url アップデートサイトのURL
	 * @param raceId 大会ID
	 * @param number ゼッケン番号
	 */
	public static RunnerInfo getNetRunnerInfo( String url, String raceId, String number ) throws LogicException{
		try {
			// 選手情報取得
			ParserRunnerInfo parserRunnerInfo = ParserRunnersUpdate.getRunnerInfo( raceId, number);
			
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
	
	/**
	 * 選択中の大会IDの選手情報をネットワークから取得する
	 */
	public static void loadNetRunnerInfoList(){
		
		// 古い情報を削除する
		if(m_NetRunnerInfoList != null){
			m_NetRunnerInfoList.clear();
		}
		
		m_NetRunnerInfoList = new ArrayList<RunnerInfo>();
		
		//for( RunnerInfo runnerInfo : m_RunnerInfoList){
		//	RunnerInfo netRunnerInfo = null;
		//	try {
		//		netRunnerInfo = getNetRunnerInfo(runnerInfo.getNumber());
		//	} catch (LogicException e) {
		//		e.printStackTrace();
		//		netRunnerInfo = new RunnerInfo();
		//	}
		//	m_NetRunnerInfoList.add(netRunnerInfo);
		//}
	}
	
	
	/**
	 * ネットワークから取得した選手情報を更新する
	 * @return true:更新データあり、false:更新データなし
	 */
	public static boolean updateRunnerInfo( ContentResolver contentResolver){
		
		// ネットワークから未取得ならば、なにもしない( こないはず )
		if(m_NetRunnerInfoList == null){
			return false;
		}
		
		boolean updateFlg = false;
		//for( int i=0; i < m_RunnerInfoList.size(); i++ ){
		//	RunnerInfo newInfo = m_NetRunnerInfoList.get(i);
		//	RunnerInfo oldInfo = m_RunnerInfoList.get(i);
			
		//	int newInfoTimeListSize = newInfo.getTimeList().size();
		//	int oldInfoTimeListSize = oldInfo.getTimeList().size();
			
			// タイムリストが更新されているならば、データベースに書き込み
		//	if( newInfoTimeListSize > oldInfoTimeListSize ){
		//		int updateCnt = newInfoTimeListSize - oldInfoTimeListSize;
				
		//		for(int j=0; j < updateCnt; j++){
					
		//			String point = newInfo.getTimeList().get(oldInfoTimeListSize+j).getPoint();
		//			String split = newInfo.getTimeList().get(oldInfoTimeListSize+j).getSplit();
		//			String lap = newInfo.getTimeList().get(oldInfoTimeListSize+j).getLap();
		//			String currentTime = newInfo.getTimeList().get(oldInfoTimeListSize+j).getCurrentTime();
		//			
					// タイムリスト書き込み
		//			DataBaseAccess.entryTimeList(
		//				contentResolver,
		//				m_SelectRaceInfo.getRaceId(),
		//				newInfo.getNumber(),
		//				point,
		//				split,
		//				lap,
		//				currentTime
		//			);
					
					// タイムリスト追加
		//			RunnerInfo.TimeList timeList = new RunnerInfo().new TimeList();
		//			timeList.setPoint(point);
		//			timeList.setSplit(split);
		//			timeList.setLap(lap);
		//			timeList.setCurrentTime(currentTime);
		//			oldInfo.getTimeList().add(timeList);
					
					// 速報データ書き込み
		//			DataBaseAccess.entryUpdateData(
		//				contentResolver,
		//				m_SelectRaceInfo.getRaceId(),
		//				newInfo.getNumber(),
		//				newInfo.getName(),
		//				newInfo.getSection(),
		//				point,
		//				split,
		//				lap,
		//				currentTime
		//			);
					
					// 速報データ追加
		//			if( m_UpdateInfoList != null){
		//				UpdateInfo updateInfo = new UpdateInfo();
		//				updateInfo.setName(newInfo.getName());
		//				updateInfo.setSection(newInfo.getSection());
		//				updateInfo.setNumber(newInfo.getNumber());
		//				updateInfo.setPoint(point);
		//				updateInfo.setSplit(split);
		//				updateInfo.setLap(lap);
		//				updateInfo.setCurrentTime(currentTime);
						
		//				m_UpdateInfoList.add(0, updateInfo);
		//			}
		//		}
		//		updateFlg = true;
		//	}
		//}
		
		return updateFlg;
	}
	/**
	 * 選手情報を追加する
	 * @param contentResolver
	 * @param raceInfo
	 * @param runnerInfo
	 */
	public static void entryRunnerInfo( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo ){
		
		// 登録情報設定
		DataBaseRunnerInfo dbRunnerInfo = new DataBaseRunnerInfo();

		dbRunnerInfo.setRaceId(raceInfo.getRaceId());
		dbRunnerInfo.setName(runnerInfo.getName());
		dbRunnerInfo.setNumber(runnerInfo.getNumber());
		dbRunnerInfo.setSection(runnerInfo.getSection());
		
		// データベース登録
		DataBaseAccess.entryRunner(contentResolver, dbRunnerInfo );
		
	}
	
	/**
	 * 選手情報リストを取得する
	 * @param contentResolver
	 * @param raceInfo 大会情報
	 * @return
	 */
	public static List<RunnerInfo> getRunnerInfoList( ContentResolver contentResolver, RaceInfo raceInfo ){
		
		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(contentResolver, raceInfo.getRaceId());
		
		List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();
		
		for( DataBaseRunnerInfo dbRunnerInfo: dbRunnerInfoList ){
			RunnerInfo runnerInfo = new RunnerInfo();
				
			runnerInfo.setName(dbRunnerInfo.getName());
			runnerInfo.setNumber(dbRunnerInfo.getNumber());
			runnerInfo.setSection(dbRunnerInfo.getSection());
				
			List<DataBaseTimeList> dbTimeListList = DataBaseAccess.getTimeListByRaceIdandNo(contentResolver, raceInfo.getRaceId(), dbRunnerInfo.getNumber());
				
			for( DataBaseTimeList dbTimeList:dbTimeListList){
				RunnerInfo.TimeList timeList = new RunnerInfo().new TimeList();
				timeList.setPoint(dbTimeList.getPoint());
				timeList.setSplit(dbTimeList.getSplit());
				timeList.setLap(dbTimeList.getLap());
				timeList.setCurrentTime(dbTimeList.getCurrentTime());
					
				runnerInfo.getTimeList().add(timeList);
			}
			
			runnerInfoList.add(runnerInfo);
		}
		return runnerInfoList;
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
	 * @param raceInfo
	 * @param runnerInfo
	 * @return　false 未登録、true 登録済み
	 */
	public static boolean checkEntryRunnerId( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo ){
		
		// 選手情報未取得
		DataBaseRunnerInfo info = DataBaseAccess.getRunnerInfoByRaceIdandNumber(
				contentResolver,
				raceInfo.getRaceId(),
				runnerInfo.getNumber());
			
		if( info == null ){
			return false;
		}else{
			return true;
		}
		
	}
	
	/**
	 * 選手情報を削除する
	 * @param contentResolver
	 * @param runnerInfo
	 */
	public static void deleteRunnerInfo( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo ){
		
		// TODO: 速報リスト削除
		
		// タイムリスト削除
		DataBaseAccess.deleteTimeListByRaceIdandNo(contentResolver, raceInfo.getRaceId(), runnerInfo.getNumber());
		
		// 選手情報削除
		DataBaseAccess.deleteRunnerInfoByNo(contentResolver, m_SelectRaceInfo.getRaceId(), m_SelectRunnerInfo.getNumber());
		
		return;
	}
	
	/**
	 * 選択中の選手情報を取得する
	 * @return 選手情報
	 */
	public static RunnerInfo getSelectRunnerInfo(){
		return m_SelectRunnerInfo;
	}
	
	/**
	 * 速報情報を取得する
	 * @param contentResolver
	 * @return
	 */
	public static List<UpdateInfo> getUpdateInfoList(ContentResolver contentResolver){
		
		if( m_UpdateInfoList == null ){
			// 速報リスト未取得
			List<DataBaseUpdateData> dbUpdateDataList = DataBaseAccess.getUpdateDataByRaceId(contentResolver, m_SelectRaceInfo.getRaceId());
			
			m_UpdateInfoList = new ArrayList<UpdateInfo>();
			
			for( DataBaseUpdateData dbUpdateData: dbUpdateDataList ){
				UpdateInfo updateInfo = new UpdateInfo();
				
				updateInfo.setName(dbUpdateData.getName());
				updateInfo.setNumber(dbUpdateData.getNumber());
				updateInfo.setSection(dbUpdateData.getSection());
				updateInfo.setPoint(dbUpdateData.getPoint());
				updateInfo.setSplit(dbUpdateData.getSplit());
				updateInfo.setLap(dbUpdateData.getLap());
				updateInfo.setCurrentTime(dbUpdateData.getCurrentTime());
				
				m_UpdateInfoList.add(updateInfo);
			}
			
			Collections.reverse(m_UpdateInfoList);
		}
		
		return m_UpdateInfoList;
	}
	
	/**
	 * 選択中の大会の部門リストを作成する
	 * @return
	 */
	public static List<String> getSectionList(){
		
		if(m_SectionList != null ){
			m_SectionList.clear();
			m_SectionList = null;
		}
		
		m_SectionList = new ArrayList<String>();
		
		//for( RunnerInfo runnerInfo:m_RunnerInfoList){
			
		//	String section = runnerInfo.getSection();
			
		//	if( m_SectionList.indexOf(section) == -1 ){
        //		m_SectionList.add(section);
        //	}
		//}
		
		return m_SectionList;
	}
	
	public static void setSelectSection( String section){
		m_SelectSection = section;
	}
	
	public static String getSelectSection(){
		return m_SelectSection;
	}
}
