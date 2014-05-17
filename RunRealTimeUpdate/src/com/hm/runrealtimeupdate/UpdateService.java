package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfoParser;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class UpdateService extends Service {

	public static String STR_INTENT_RACEID = "raceid";
	
	//TODO: 暫定値
	/**
	 * タイマー間隔
	 */
	private static int INT_TIMER_INTERVAL = 120000;
	
	/**
	 * タイマー開始遅延時間
	 */
	private static int INT_TIMER_DELAY = 100000;
	
	/**
	 * タイマー
	 */
	private Timer m_IntervalTimer;
	
	/**
	 * 大会ID
	 */
	private String m_RaceId;
	
	/**
	 * 選手情報リスト
	 */
	private List<DataBaseRunnerInfo> m_RunnerInfoList;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId ) {
		super.onStart(intent, startId);
		
		// 初期化
		m_IntervalTimer = new Timer();
		m_RunnerInfoList = new ArrayList<DataBaseRunnerInfo>();
		
		// 大会Id取得
		m_RaceId = intent.getStringExtra(STR_INTENT_RACEID);
		
		//　選手情報リストを取得
		List<DataBaseRunnerInfo> list = DataBaseAccess.getRunnerInfoByRaceId(getContentResolver(), m_RaceId);
		for(DataBaseRunnerInfo info:list){
			m_RunnerInfoList.add(info);
		}
		
		// タイマー開始
		UpdateTimerTask timerTask = new UpdateTimerTask();
		m_IntervalTimer.schedule(timerTask, INT_TIMER_DELAY, INT_TIMER_INTERVAL);
	}
	
	private class UpdateTimerTask extends TimerTask {

		private Handler handler;
		
		UpdateTimerTask(){
			handler = new Handler();
		}
		
		@Override
		public void run() {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// 選手情報取得
					List<RunnerInfo> runnerInfoList = new ArrayList<RunnerInfo>();
					for(DataBaseRunnerInfo info:m_RunnerInfoList){
						
						try {
							RunnerInfo runnerInfo = RunnerInfoParser.getRunnerInfo(getString(R.string.str_txt_defaulturl), m_RaceId, info.getNumber());
							runnerInfoList.add(runnerInfo);
						} catch (ParserException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							
							// とりあえず空リストを作成する
							RunnerInfo runnerInfo = new RunnerInfo();
							runnerInfoList.add(runnerInfo);
						}
					}
				}
			});
		}
	}
}
