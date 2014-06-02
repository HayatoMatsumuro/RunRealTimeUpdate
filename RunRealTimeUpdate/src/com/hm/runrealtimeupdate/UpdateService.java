package com.hm.runrealtimeupdate;

import java.util.Timer;
import java.util.TimerTask;

import com.hm.runrealtimeupdate.logic.Logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
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
		
		// TODO:
		Log.d("service", "start");
		
		// タイマー開始
		UpdateTimerTask timerTask = new UpdateTimerTask();
		m_IntervalTimer.schedule(timerTask, INT_TIMER_DELAY, INT_TIMER_INTERVAL);
	}
	
	/**
	 * データ更新タスク
	 * @author Hayato Matsumuro
	 *
	 */
	private class UpdateTimerTask extends TimerTask {

		private Handler handler;
		
		UpdateTimerTask(){
			handler = new Handler();
		}
		
		@Override
		public void run() {
			Logic.loadNetRunnerInfoList();
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					
					// データアップデート
					boolean updateFlg = Logic.updateRunnerInfo(getContentResolver());
					
					// 更新がある場合は通知
					if(updateFlg){
						
						Notification notification = new Notification( R.drawable.ic_runner, getString(R.string.str_msg_updaterunner), System.currentTimeMillis());
						notification.flags = Notification.FLAG_AUTO_CANCEL;
						
						Intent notifiIntent = new Intent(UpdateService.this, UpdateListActivity.class);
						
						PendingIntent pendIntent = PendingIntent.getActivity(UpdateService.this, 0, notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						
						notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.str_msg_updaterunner), pendIntent);
						
						NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						manager.notify(R.string.app_name, notification);
					}
					
					// 速報回数が最大を超えたら速報を自動停止する
					m_IntervalCnt++;
					if( m_IntervalCnt >= INT_TIMER_INTERAVAL_CNT_MAX ){
						// TODO:
						Log.d("service", "stopSelf");
						
						// 速報停止状態にする
						Logic.setUpdateOffRaceId(getContentResolver(), Logic.getUpdateRaceId());
						stopSelf();
					}
				}
			});
		}
	}
}
