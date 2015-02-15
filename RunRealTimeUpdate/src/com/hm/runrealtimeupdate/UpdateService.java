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
	static final String STR_INTENT_RACEID = "raceid";

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
		List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoNOTFinish( getContentResolver(), raceId );

		// パーサー情報取得
		CommonLib.ParserInfo parserInfo = CommonLib.getParserInfoByRaceId( UpdateService.this, raceId );

		// 更新タスク開始
		m_UpdateTask = new RunnerInfoUpdateTask( raceInfo, getContentResolver() );
		RunnerInfoUpdateTask.TaskParam param = m_UpdateTask.new TaskParam();

		param.url = parserInfo.url;
		param.pass = parserInfo.pass;
		param.runnerInfoList = runnerInfoList;
		param.parserClassName = parserInfo.parserClassName;

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
			return Logic.getNetRunnerInfoList(
						params[0].url,
						params[0].pass,
						params[0].runnerInfoList,
						params[0].parserClassName );
		}

		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList )
		{
			// データアップデート
			boolean updateFlg = Logic.updateRunnerInfo( m_ContentResolver, m_RaceInfo.id, runnerInfoList );

			// 停止フラグ
			boolean stopFlg = false;

			// 更新がある場合は通知
			if( updateFlg )
			{
				Notification notification = new Notification( R.drawable.ic_launcher, getString( R.string.str_msg_updaterunner ), System.currentTimeMillis() );
				notification.flags = Notification.FLAG_AUTO_CANCEL;

				Intent notifiIntent = new Intent( UpdateService.this, RaceTabActivity.class );
				notifiIntent.putExtra( RaceTabActivity.STR_INTENT_RACEID, m_RaceInfo.id );
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
				notification.vibrate = Common.LONG_BIVRATION;

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

			// 範囲外の時間ならば、自動停止
			if( !stopFlg )
			{
				int hour = CommonLib.getHourOfDay();

				if( hour < Common.INT_PARMIT_AUTOSTART || hour >= Common.INT_PARMIT_AUTOSTOP )
				{
					// 更新を停止する
					stopFlg = true;
				}
			}

			// 更新を停止する
			if( stopFlg )
			{
				CommonLib.cancelUpdateAlarm( UpdateService.this, m_RaceInfo.id );

				// データベース変更
				Logic.setUpdateOffRaceId( getContentResolver(), m_RaceInfo.id );

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
		private class TaskParam
		{
			/**
			 * アップデートサイトURL
			 */
			private String url;

			/**
			 * パス
			 */
			private String pass;

			/**
			 * 選手リスト
			 */
			private List<RunnerInfo> runnerInfoList;

			/**
			 * パーサークラス名
			 */
			private String parserClassName;
		}
	}
}
