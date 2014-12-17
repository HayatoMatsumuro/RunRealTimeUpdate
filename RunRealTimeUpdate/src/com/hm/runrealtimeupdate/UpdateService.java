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

/**
 * 更新サービスのService
 * @author Hayato Matsumuro
 *
 */
public class UpdateService extends Service
{
	/**
	 * インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	/**
	 * リクエストコード スタート
	 */
	public static final int INT_REQUESTCODE_START = 1;

	/**
	 * バイブ 
	 *  [ON時間, OFF時間, ・・・]
	 */
	private static long[] LONG_BIVRATION = {0, 100, 100, 100, 100, 100};

	/**
	 * 選手情報更新タスク
	 */
	private RunnerInfoUpdateTask m_UpdateTask = null;

	@Override
	public IBinder onBind( Intent arg0 )
	{
		return null;
	}

	@Override
	public void onStart( Intent intent, int startId )
	{
		super.onStart( intent, startId );

		// 大会情報取得
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		// 大会情報が取得できないなら、停止する
		if( raceInfo == null )
		{
			stopSelf();
			return;
		}

		if( ( m_UpdateTask != null ) && ( m_UpdateTask.getStatus() == AsyncTask.Status.RUNNING ) )
		{
			return;
		}

		// 選手情報取得
		List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList( getContentResolver(), raceId );

		// 更新タスク開始
		m_UpdateTask = new RunnerInfoUpdateTask( raceInfo, getContentResolver() );
		RunnerInfoUpdateTask.TaskParam param = m_UpdateTask.new TaskParam();

		param.setUrl( getString( R.string.str_txt_defaulturl ) );
		param.setRaceId( raceInfo.getRaceId() );
		param.setRunnerInfoList( runnerInfoList );

		m_UpdateTask.execute( param );

		return;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// タイマー停止
		if( ( m_UpdateTask != null ) && ( m_UpdateTask.getStatus() == AsyncTask.Status.RUNNING ) )
		{
			m_UpdateTask.cancel( true );
		}

		return;
	}

	/**
	 * 選手情報更新タスク
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerInfoUpdateTask extends AsyncTask< RunnerInfoUpdateTask.TaskParam, Void, List<RunnerInfo > >
	{
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
		RunnerInfoUpdateTask( RaceInfo raceInfo, ContentResolver contentResolver )
		{
			m_RaceInfo = raceInfo;
			m_ContentResolver = contentResolver;
			return;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			return;
		}

		@Override
		protected List<RunnerInfo> doInBackground( TaskParam... params )
		{
			// ネットワークから選手情報取得
			String url = params[0].getUrl();
			String raceId = params[0].getRaceId();
			List<RunnerInfo> runnerInfoList = params[0].getRunnerInfoList();

			return Logic.getNetRunnerInfoList( url, raceId, runnerInfoList );
		}

		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList )
		{
			// データアップデート
			boolean updateFlg = Logic.updateRunnerInfo( m_ContentResolver, m_RaceInfo.getRaceId(), runnerInfoList );

			// 停止フラグ
			boolean stopFlg = false;

			// 更新がある場合は通知
			if( updateFlg )
			{
				Notification notification = new Notification( R.drawable.ic_launcher, getString( R.string.str_msg_updaterunner ), System.currentTimeMillis() );
				notification.flags = Notification.FLAG_AUTO_CANCEL;

				Intent notifiIntent = new Intent( UpdateService.this, RaceTabActivity.class );
				notifiIntent.putExtra( RaceTabActivity.STR_INTENT_RACEID, m_RaceInfo.getRaceId() );
				notifiIntent.putExtra( RaceTabActivity.STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_UPDATE );

				PendingIntent pendIntent = PendingIntent.getActivity( UpdateService.this, 0, notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT );

				// ステータスバー
				notification.setLatestEventInfo(
					getApplicationContext(),
					getString( R.string.app_name ),
					getString( R.string.str_msg_updaterunner ),
					pendIntent
				);

				// バイブ
				notification.vibrate = LONG_BIVRATION;

				NotificationManager manager = ( NotificationManager )getSystemService( NOTIFICATION_SERVICE );
				manager.notify( R.string.app_name, notification );

				// カウントを再設定する
				Logic.setAutoStopCount( UpdateService.this, Common.INT_COUNT_AUTOSTOP_LASTUPDATE );
			}
			// 更新なし
			else
			{
				// 更新の停止処理
				try
				{
					stopFlg = Logic.updateAutoStopCount( UpdateService.this );
				}
				catch( LogicException e )
				{
					// 更新を停止する
					stopFlg = true;
					e.printStackTrace();
				}
			}

			// 自動停止がOFFならば、定期更新
			if( !stopFlg )
			{
				// 更新の停止処理
				try
				{
					stopFlg = Logic.updateRegularStopCount( UpdateService.this );
				}
				catch( LogicException e )
				{
					// 更新を停止する
					stopFlg = true;
					e.printStackTrace();
				}
			}

			// 更新を停止する
			if( stopFlg )
			{
				CommonLib.cancelUpdateAlarm( UpdateService.this, m_RaceInfo.getRaceId() );

				// データベース変更
				Logic.setUpdateOffRaceId( getContentResolver(), m_RaceInfo.getRaceId() );

				return;
			}
			stopSelf();

			return;
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();
			return;
		}

		/**
		 * タスクパラメータ
		 * @author Hayato Matsumuro
		 *
		 */
		public class TaskParam
		{
			/**
			 * アップデートサイトURL
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

			/**
			 * アップデートサイトURLを取得する
			 * @return アップデートサイトURL
			 */
			public String getUrl()
			{
				return url;
			}

			/**
			 * アップデートサイトURLを設定する
			 * @param url アップデートサイトURL
			 */
			public void setUrl( String url )
			{
				this.url = url;
				return;
			}

			/**
			 * 大会IDを取得する
			 * @return 大会ID
			 */
			public String getRaceId()
			{
				return raceId;
			}

			/**
			 * 大会IDを設定する
			 * @param raceId 大会ID
			 */
			public void setRaceId( String raceId )
			{
				this.raceId = raceId;
				return;
			}

			/**
			 * 選手情報リストを取得する
			 * @return 選手情報リスト
			 */
			public List<RunnerInfo> getRunnerInfoList()
			{
				return runnerInfoList;
			}

			/**
			 * 選手情報リストを設定する
			 * @param runnerInfo 選手情報リスト
			 */
			public void setRunnerInfoList( List<RunnerInfo> runnerInfo )
			{
				this.runnerInfoList = runnerInfo;
				return;
			}
		}
	}
}
