package com.hm.runrealtimeupdate;

import java.util.Timer;
import java.util.TimerTask;

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
	//private String m_RaceId;
	
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
		
		//TODO: ゼッケンNOリストを取得
		
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
					// TODO 自動生成されたメソッド・スタブ
					
				}
			});
		}
	}
}
