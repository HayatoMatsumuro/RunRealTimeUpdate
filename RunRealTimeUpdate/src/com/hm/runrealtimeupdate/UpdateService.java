package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final int INT_REQUESTCODE_START = 1;
	/**
	 * バイブ 
	 *  [ON時間, OFF時間, ・・・]
	 */
	private static long[] LONG_BIVRATION = {0, 100, 100, 100, 100, 100};
	
	private RunnerInfoUpdateTask m_UpdateTask = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId ) {
		super.onStart(intent, startId);
		
		// TODO:
		Log.d("service", "start");
		
		// 大会情報取得
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、停止する
        if( raceInfo == null ){
        	stopSelf();
        	return;
        }
        
        if( ( m_UpdateTask != null ) && ( m_UpdateTask.getStatus() == AsyncTask.Status.RUNNING ) ){
        	// TODO:
    		Log.d("service", "net Exe");
        	return;
        }
        // 選手情報取得
        List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList(getContentResolver(), raceId);
		
        // 更新タスク開始
        m_UpdateTask = new RunnerInfoUpdateTask( raceInfo, getContentResolver() );
        RunnerInfoUpdateTask.TaskParam param = m_UpdateTask.new TaskParam();
		
		param.setUrl( getString( R.string.str_txt_defaulturl ) );
		param.setRaceId( raceInfo.getRaceId() );
		param.setRunnerInfoList( runnerInfoList );
		
		m_UpdateTask.execute( param );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO:
		Log.d("service", "destroy");
		
		// タイマー停止
		if( ( m_UpdateTask != null ) && ( m_UpdateTask.getStatus() == AsyncTask.Status.RUNNING ) ){
			m_UpdateTask.cancel( true );
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

			// TODO:
			Log.d("service", "net get End");
			
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
			
			// 更新の停止処理
			try {
				boolean stopflg = Logic.updateUpdateCount( UpdateService.this );
				
				// 更新を停止する
				if( stopflg ){
					
					CommonLib.cancelUpdateAlarm( UpdateService.this, m_RaceInfo.getRaceId() );
				    
				    // データベース変更
					Logic.setUpdateOffRaceId(getContentResolver(), m_RaceInfo.getRaceId() );
					
				    // TODO:
					Log.d("service", "alarm End");
					
					return;
				}
			} catch (LogicException e) {
				// 特に何も処理しない( ありえないので )
				e.printStackTrace();
			}
			
			stopSelf();
			
			return;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();

			// TODO:
			Log.d("service", "net cancel");
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
