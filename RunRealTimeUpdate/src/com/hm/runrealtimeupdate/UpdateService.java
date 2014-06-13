package com.hm.runrealtimeupdate;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	//TODO: 暫定値
	/**
	 * タイマー間隔
	 */
	private static int INT_TIMER_INTERVAL = 30000;
	
	//TODO: 暫定値
	/**
	 * タイマー開始遅延時間
	 */
	private static int INT_TIMER_DELAY = 0;
	
	//TODO: 暫定値
	/**
	 * バイブ 
	 *  [ON時間, OFF時間, ・・・]
	 */
	private static long[] LONG_BIVRATION = {0, 100, 100, 100, 100, 100};
	
	/**
	 * 速報を行う回数
	 * 1日で自動的に速報が停止する
	 */
	//TODO: 暫定
	private static int INT_TIMER_INTERAVAL_CNT_MAX = 86400000 / INT_TIMER_INTERVAL;
	
	/**
	 * タイマー
	 */
	private Timer m_IntervalTimer;
	
	/**
	 * 速報の回数
	 */
	private int m_IntervalCnt;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO:
		Log.d("service", "destroy");
		
		// タイマー停止
		m_IntervalTimer.cancel();
	}

	@Override
	public void onStart(Intent intent, int startId ) {
		super.onStart(intent, startId);
		
		// 初期化
		m_IntervalTimer = new Timer();
		m_IntervalCnt = 0;
		
		// 大会情報取得
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、停止する
        if( raceInfo == null ){
        	stopSelf();
        	return;
        }
        
        // 選手情報取得
        List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList(getContentResolver(), raceId);
        
		// TODO:
		Log.d("service", "start");
		
		// タイマー開始
		UpdateTimerTask timerTask = new UpdateTimerTask( getContentResolver(), raceInfo, runnerInfoList);
		m_IntervalTimer.schedule(timerTask, INT_TIMER_DELAY, INT_TIMER_INTERVAL);
	}
	
	/**
	 * データ更新タスク
	 * @author Hayato Matsumuro
	 *
	 */
	private class UpdateTimerTask extends TimerTask {

		/**
		 * ハンドラ
		 */
		private Handler m_Handler;
		
		/**
		 * コンテントリゾルバ
		 */
		private ContentResolver m_ContentResolver;
		
		/**
		 * 大会情報
		 */
		private RaceInfo m_RaceInfo;
		
		/**
		 * ゼッケン番号リスト
		 */
		private List<RunnerInfo> m_RunnerInfoList;
		
		/**
		 * ネットワークから取得した選手情報
		 */
		private List<RunnerInfo> m_NetRunnerInfoList;
		
		/**
		 * コンストラクタ
		 * @param raceInfo 大会情報
		 * @param numberList ゼッケン番号リスト
		 */
		UpdateTimerTask( ContentResolver contentResolver, RaceInfo raceInfo, List<RunnerInfo> runnerInfoList ){
			m_Handler = new Handler();
			m_ContentResolver = contentResolver;
			m_RaceInfo = raceInfo;
			m_RunnerInfoList = runnerInfoList;
			m_NetRunnerInfoList = null;
		}
		
		@Override
		public void run() {
			
			
			if( m_NetRunnerInfoList != null ){
				// ネットワーク選手リストが有効ならば更新をしない
				return;
			}
			
			// ネットワークから選手情報を取得する
			String url = getString(R.string.str_txt_defaulturl);
			m_NetRunnerInfoList = Logic.getNetRunnerInfoList(url, m_RaceInfo.getRaceId(), m_RunnerInfoList);
			m_Handler.post(new Runnable() {
				
				@Override
				public void run() {
					// データアップデート
					boolean updateFlg = Logic.updateRunnerInfo(m_ContentResolver, m_RaceInfo.getRaceId(), m_NetRunnerInfoList);
					
					// 更新がある場合は通知
					if(updateFlg){
						
						Notification notification = new Notification( R.drawable.ic_runner, getString(R.string.str_msg_updaterunner), System.currentTimeMillis());
						notification.flags = Notification.FLAG_AUTO_CANCEL;
						
						Intent notifiIntent = new Intent(UpdateService.this, UpdateListActivity.class);
						notifiIntent.putExtra(UpdateListActivity.STR_INTENT_RACEID, m_RaceInfo.getRaceId());
						
						PendingIntent pendIntent = PendingIntent.getActivity(UpdateService.this, 0, notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						
						// ステータスバー
						notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.str_msg_updaterunner), pendIntent);
						
						// バイブ
						notification.vibrate = LONG_BIVRATION;
						
						NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						manager.notify(R.string.app_name, notification);
					}
					
					// 速報回数が最大を超えたら速報を自動停止する
					m_IntervalCnt++;
					if( m_IntervalCnt >= INT_TIMER_INTERAVAL_CNT_MAX ){
						// TODO:
						Log.d("service", "stopSelf");
						
						// 速報停止状態にする
						Logic.setUpdateOffRaceId(getContentResolver(), m_RaceInfo.getRaceId());
						stopSelf();
					}
					
					m_NetRunnerInfoList.clear();
					m_NetRunnerInfoList = null;
				}
			});
		}
	}
}
