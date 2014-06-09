package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public static List<RaceInfo> getRaceInfoList( ContentResolver contentResolver ){
		
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
		
		if( dbRaceInfo == null){
			return null;
		}else{
			return getRaceInfoBydbRaceInfo( dbRaceInfo );
		}
		
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
	 * @param raceId 削除する大会Id
	 */
	public static void deleteRaceInfo( ContentResolver contentResolver, String raceId ){
		
		// 速報データ
		DataBaseAccess.deleteUpdateDataByRaceId(contentResolver, raceId );
		
		// タイムリスト
		DataBaseAccess.deleteTimeListByRaceId(contentResolver, raceId );
		
		// 選手削除
		DataBaseAccess.deleteRunnerInfoByRaceId(contentResolver, raceId );
		
		// 大会を削除する
		DataBaseAccess.deleteRaceInfoByRaceId(contentResolver, raceId );
		
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
	 * 指定の大会を速報状態にする
	 * @param raceId 大会ID
	 */
	public static void setUpdateOnRaceId( ContentResolver contentResolver, String raceId){
		
		// 指定の大会を速報状態にする
		DataBaseAccess.setRaceUpdate(contentResolver, raceId, DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON);
		
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
				
		return;
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
			raceInfo.setRaceUpdate(false);
			
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
	 * ネットワークから指定の大会のゼッケン番号の選手情報を取得する
	 * @param url　アップデートサイトURL
	 * @param raceId 大会ID
	 * @param runnerList 選手情報リスト
	 * @return　選手情報リスト
	 */
	public static List<RunnerInfo> getNetRunnerInfoList( String url, String raceId, List<RunnerInfo> runnerInfoList){
		
		List<RunnerInfo> netRunnerInfoList = new ArrayList<RunnerInfo>();
		
		for( RunnerInfo runnerInfo : runnerInfoList ){
			RunnerInfo netRunnerInfo = null;
			
			try{
				netRunnerInfo = getNetRunnerInfo(url, raceId, runnerInfo.getNumber());
			} catch (LogicException e) {
				
				// 取得に失敗した場合は、ゼッケンNoのみの要素を作成
				e.printStackTrace();
				netRunnerInfo = new RunnerInfo();
				netRunnerInfo.setNumber( runnerInfo.getNumber() );
			}
			
			netRunnerInfoList.add(netRunnerInfo);
		}
		return netRunnerInfoList;
	}
	
	/**
	 * 指定の選手情報とデータベースの選手情報を比較する。
	 * 指定の選手情報の多いデータを更新する。
	 * 指定の選手情報とデータベースの選手情報の数は同じとする
	 * @param contentResolver
	 * @param raceId 大会情報
	 * @param runnerInfoList 選手情報リスト
	 * @return true:更新データあり、false:更新データなし
	 */
	public static boolean updateRunnerInfo( ContentResolver contentResolver, String raceId, List<RunnerInfo> newRunnerInfoList){
		
		boolean updateFlg = false;
		
		List<RunnerInfo> oldRunnerInfoList = getRunnerInfoList(contentResolver, raceId);
		for( int i=0; i < oldRunnerInfoList.size(); i++ ){
		
			RunnerInfo newInfo = newRunnerInfoList.get(i);
			RunnerInfo oldInfo = oldRunnerInfoList.get(i);
			
			int newInfoTimeListSize = newInfo.getTimeList().size();
			int oldInfoTimeListSize = oldInfo.getTimeList().size();
			
			// タイムリストが更新されているならば、データベースに書き込み
			if( newInfoTimeListSize > oldInfoTimeListSize ){
				int updateCnt = newInfoTimeListSize - oldInfoTimeListSize;
				
				for(int j=0; j < updateCnt; j++){
					
					String point = newInfo.getTimeList().get(oldInfoTimeListSize+j).getPoint();
					String split = newInfo.getTimeList().get(oldInfoTimeListSize+j).getSplit();
					String lap = newInfo.getTimeList().get(oldInfoTimeListSize+j).getLap();
					String currentTime = newInfo.getTimeList().get(oldInfoTimeListSize+j).getCurrentTime();
					
					// タイムリスト書き込み
					DataBaseTimeList dbTimeList = new DataBaseTimeList();
					dbTimeList.setRaceId(raceId);
					dbTimeList.setNumber(newInfo.getNumber());
					dbTimeList.setPoint(point);
					dbTimeList.setSplit(split);
					dbTimeList.setLap(lap);
					dbTimeList.setCurrentTime(currentTime);
					DataBaseAccess.entryTimeList( contentResolver, dbTimeList );
					
					// 速報データ書き込み
					DataBaseUpdateData dbUpdateData = new DataBaseUpdateData();
					dbUpdateData.setRaceId(raceId);
					dbUpdateData.setName(newInfo.getName());
					dbUpdateData.setNumber(newInfo.getNumber());
					dbUpdateData.setSection(newInfo.getSection());
					dbUpdateData.setPoint(point);
					dbUpdateData.setSplit(split);
					dbUpdateData.setLap(lap);
					dbUpdateData.setCurrentTime(currentTime);
					DataBaseAccess.entryUpdateData( contentResolver, dbUpdateData );
				}
				updateFlg = true;
			}
		}
		
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
	 * @param raceId 大会ID
	 * @return
	 */
	public static List<RunnerInfo> getRunnerInfoList( ContentResolver contentResolver, String raceId ){
		
		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(contentResolver, raceId );
		
		List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();
		
		for( DataBaseRunnerInfo dbRunnerInfo: dbRunnerInfoList ){
			RunnerInfo runnerInfo = new RunnerInfo();
				
			runnerInfo.setName(dbRunnerInfo.getName());
			runnerInfo.setNumber(dbRunnerInfo.getNumber());
			runnerInfo.setSection(dbRunnerInfo.getSection());
				
			List<DataBaseTimeList> dbTimeListList = DataBaseAccess.getTimeListByRaceIdAndNumber(contentResolver, raceId, dbRunnerInfo.getNumber());
				
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
	 * 指定のゼッケン番号が登録済みかどうかを検索する
	 * @param contentResolver
	 * @param raceInfo
	 * @param runnerInfo
	 * @return　false 未登録、true 登録済み
	 */
	public static boolean checkEntryRunnerId( ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo ){
		
		// 選手情報未取得
		DataBaseRunnerInfo info = DataBaseAccess.getRunnerInfoByRaceIdAndNumber(
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
	public static void deleteRunnerInfo( ContentResolver contentResolver, String raceId, String number ){
		
		
		// タイムリスト削除
		DataBaseAccess.deleteTimeListByRaceIdAndNumber(contentResolver, raceId, number);
		
		// 選手情報削除
		DataBaseAccess.deleteRunnerInfoByRaceIdAndNumber(contentResolver, raceId, number);
		
		return;
	}
	
	/**
	 * 速報情報を取得する( 新しい順 )
	 * @param contentResolver
	 * @param raceId 大会ID
	 * @return
	 */
	public static List<UpdateInfo> getUpdateInfoList( ContentResolver contentResolver, String raceId ){
		
		List<DataBaseUpdateData> dbUpdateDataList = DataBaseAccess.getUpdateDataByRaceId(contentResolver, raceId );
		
		List<UpdateInfo> updateInfoList = new ArrayList<UpdateInfo>();
		
		for( DataBaseUpdateData dbUpdateData: dbUpdateDataList ){
			UpdateInfo updateInfo = new UpdateInfo();
				
			updateInfo.setName(dbUpdateData.getName());
			updateInfo.setNumber(dbUpdateData.getNumber());
			updateInfo.setSection(dbUpdateData.getSection());
			updateInfo.setPoint(dbUpdateData.getPoint());
			updateInfo.setSplit(dbUpdateData.getSplit());
			updateInfo.setLap(dbUpdateData.getLap());
			updateInfo.setCurrentTime(dbUpdateData.getCurrentTime());
				
			updateInfoList.add(updateInfo);
		}
		
		// 順番を逆にする
		Collections.reverse(updateInfoList);
		
		return updateInfoList;
	}
	
	
	/**
	 * 選択中の大会の部門リストを作成する
	 * @param contentResolver
	 * @param raceId　大会ID
	 * @return
	 */
	public static List<String> getSectionList( ContentResolver contentResolver, String raceId ){
		
		List<String> sectionList = new ArrayList<String>();
		
		List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(contentResolver, raceId);
		
		for( DataBaseRunnerInfo dbRunnerInfo:dbRunnerInfoList){
			
			String section = dbRunnerInfo.getSection();
			
			if( sectionList.indexOf(section) == -1 ){
				sectionList.add(section);
			}
		}
		
		return sectionList;
	}
	
	public static List<PassPointInfo> getPassPointInfoList( ContentResolver contentResolver, String raceId, String section ){
		
		List<PassPointInfo> passPointInfoList = new ArrayList<PassPointInfo>();
		
		// 部門の選手リストを取得
        List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceIdandSection( contentResolver, raceId, section);
        
        for( DataBaseRunnerInfo dbRunnerInfo : dbRunnerInfoList){
        	
        	List<DataBaseTimeList> dbTimeListList = DataBaseAccess.getTimeListByRaceIdAndNumber(contentResolver, raceId, dbRunnerInfo.getNumber());
        	
        	// 通過地点のインデックス取得
        	int pointIdx = dbTimeListList.size()-1;
        	if( pointIdx == -1){
        		continue;
        	}
        	
        	// 最新の通過地点取得
        	DataBaseTimeList dbTimeList = dbTimeListList.get(pointIdx);
        	
        	PassPointInfo.PassPointRunnerInfo runnerInfo = new PassPointInfo().new PassPointRunnerInfo();
			runnerInfo.setName(dbRunnerInfo.getName());
			runnerInfo.setNumber(dbRunnerInfo.getNumber());
			runnerInfo.setSplit(dbTimeList.getSplit());
			runnerInfo.setLap(dbTimeList.getLap());
			runnerInfo.setCurrentTime(dbTimeList.getCurrentTime());
			
			int idx = getPassPointInfoListIdx(dbTimeList.getPoint(), passPointInfoList);
        	if( idx == -1){
        		// 新規追加
        		PassPointInfo passPointInfo = new PassPointInfo();
        		passPointInfo.setPoint(dbTimeList.getPoint());
        		passPointInfo.setPassPointNo(pointIdx);
        		passPointInfo.getPassPointRunnerInfoList().add(runnerInfo);
        		passPointInfoList.add(passPointInfo);
        	}else{
        		passPointInfoList.get(idx).getPassPointRunnerInfoList().add(runnerInfo);
        	}
        }
        
        Collections.sort( passPointInfoList, new PassPointInfoComparator());
		return passPointInfoList;
	}
    
	private static int getPassPointInfoListIdx( String point, List<PassPointInfo> list){
		
		for( int i=0; i<list.size(); i++ ){
			
			PassPointInfo passPointInfo = list.get(i);
			
			if( passPointInfo.getPoint().equals(point)){
				return i;
			}
		}
		
		return -1;
	}
	
	private static class PassPointInfoComparator implements Comparator<PassPointInfo>{

		@Override
		public int compare(PassPointInfo o1, PassPointInfo o2) {
			
			if( o1.getPassPointNo() < o2.getPassPointNo() ){
				return 1;
			}else{
				return -1;
			}
		}
		
	}
}
