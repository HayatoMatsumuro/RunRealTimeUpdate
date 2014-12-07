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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	//TODO: 暫定値
	/**
	 * タイマー間隔
	 */
	private static int INT_TIMER_INTERVAL = 120000;
	
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
	 * タイマータスク
	 */
	private UpdateTimerTask m_UpdateTimerTask;
	
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
		//m_UpdateTimerTask.cancel();
		//m_IntervalTimer.cancel();
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
        	Log.d("service", "stopSelf");
        	stopSelf();
        	return;
        }
        
        // 選手情報取得
        List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList(getContentResolver(), raceId);
        
		// TODO:
		Log.d("service", "start");
		
		// タイマー開始
		m_UpdateTimerTask = new UpdateTimerTask( getContentResolver(), raceInfo, runnerInfoList );
		m_IntervalTimer.schedule( m_UpdateTimerTask, INT_TIMER_DELAY, INT_TIMER_INTERVAL );
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
		 * アップデートタスク
		 */
		RunnerInfoUpdateTask m_UpdateTask;
		
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
			m_UpdateTask = new RunnerInfoUpdateTask( m_RaceInfo, m_ContentResolver );
		}
		
		@Override
		public void run() {
			
			m_Handler.post(new Runnable() {
				
				@Override
				public void run() {
					
					// 速報回数が最大を超えたら速報を自動停止する
					m_IntervalCnt++;
					if( m_IntervalCnt > INT_TIMER_INTERAVAL_CNT_MAX ){
						// TODO:
						Log.d("service", "stopSelf");
						
						// 速報停止状態にする
						Logic.setUpdateOffRaceId( m_ContentResolver, m_RaceInfo.getRaceId() );
						stopSelf();
						return;
					}
					
					if( m_UpdateTask.getStatus() != AsyncTask.Status.RUNNING ){
						
						RunnerInfoUpdateTask.TaskParam param = m_UpdateTask.new TaskParam();
					
						param.setUrl( getString( R.string.str_txt_defaulturl ) );
						param.setRaceId( m_RaceInfo.getRaceId() );
						param.setRunnerInfoList( m_RunnerInfoList );
					
						// 手動更新タスク起動
						m_UpdateTask = new RunnerInfoUpdateTask( m_RaceInfo, m_ContentResolver );
						m_UpdateTask.execute( param );
					}
				}
			});
		}

		@Override
		public boolean cancel() {
			
			if( ( m_UpdateTask != null ) && ( m_UpdateTask.getStatus() == AsyncTask.Status.RUNNING ) ){
				m_UpdateTask.cancel( true );
			}
			return super.cancel();
		}
		
	}
	
	private class RunnerInfoUpdateTask extends AsyncTask< RunnerInfoUpdateTask.TaskParam, Void, List<RunnerInfo > >{
		
		/**
		 * 大会情報
		 */
		private RaceInfo m_RaceInfo;
		
		/**
		 * コンテントリゾルバ
		 */
		private ContentResolver m_ContentResolver;
		
		/**
		 * コンストラクタ
		 * @param raceInfo 大会情報
		 * @param contentResolver コンテントリゾルバ
		 */
		RunnerInfoUpdateTask( RaceInfo raceInfo, ContentResolver contentResolver ){
			m_RaceInfo = raceInfo;
			m_ContentResolver = contentResolver;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected List<RunnerInfo> doInBackground(TaskParam... params) {
			
			// ネットワークから選手情報取得
			String url = params[0].getUrl();
			String raceId = params[0].getRaceId();
			List<RunnerInfo> runnerInfoList = params[0].getRunnerInfoList();

			// TODO:
			Log.d("service", "net get Start");
			return Logic.getNetRunnerInfoList( url, raceId, runnerInfoList );
		}
		
		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList ){
			
			// データアップデート
			boolean updateFlg = Logic.updateRunnerInfo( m_ContentResolver, m_RaceInfo.getRaceId(), runnerInfoList );
			
			// 更新がある場合は通知
			if(updateFlg){
				
				Notification notification = new Notification(
													R.drawable.ic_launcher,
													getString( R.string.str_msg_updaterunner ),
													System.currentTimeMillis() );
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				
				Intent notifiIntent = new Intent(UpdateService.this, RaceTabActivity.class);
				notifiIntent.putExtra(RaceTabActivity.STR_INTENT_RACEID, m_RaceInfo.getRaceId());
				notifiIntent.putExtra(RaceTabActivity.STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_UPDATE);
				
				PendingIntent pendIntent = PendingIntent.getActivity(UpdateService.this, 0, notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				// ステータスバー
				notification.setLatestEventInfo(
								getApplicationContext(),
								getString( R.string.app_name ),
								getString( R.string.str_msg_updaterunner ),
								pendIntent );
				
				// バイブ
				notification.vibrate = LONG_BIVRATION;
				
				NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				manager.notify(R.string.app_name, notification);
			}
			
			return;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
		public class TaskParam{
			
			/**
			 * 大会URL
			 */
			private String url;

			/**
			 * 大会ID
			 */
			private String raceId;
			
			/**
			 * 選手リスト
			 */
			private List<RunnerInfo> runnerInfoList;


			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
			
			public String getRaceId() {
				return raceId;
			}

			public void setRaceId(String raceId) {
				this.raceId = raceId;
			}

			public List<RunnerInfo> getRunnerInfoList() {
				return runnerInfoList;
			}

			public void setRunnerInfoList(List<RunnerInfo> runnerInfo) {
				this.runnerInfoList = runnerInfo;
			}
			
		}
	}
}
