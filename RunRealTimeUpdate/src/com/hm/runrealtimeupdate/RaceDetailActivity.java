package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 大会詳細画面のActivity
 * @author Hayato Matsumuro
 *
 */
public class RaceDetailActivity extends Activity
{
	/**
	 * インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_racedetail );

		// 大会情報取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		// 大会情報が取得できないなら、エラー画面
		if( raceInfo == null )
		{
			Intent intentErr = new Intent( RaceDetailActivity.this, ErrorActivity.class );
			intentErr.putExtra( ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。" );
			return;
		}

		// 大会名表示
		TextView raceNameTextView = ( TextView )findViewById( R.id.id_activity_racedetail_body_contents_detailbox_racename_textview );
		raceNameTextView.setText( raceInfo.getRaceName() );

		// 大会日
		TextView raceDateTextView = ( TextView )findViewById( R.id.id_activity_racedetail_body_contents_detailbox_racedate_title_textview );
		raceDateTextView.setText( raceInfo.getRaceDate() );

		// 開催地
		TextView raceLocationTextView = ( TextView )findViewById( R.id.id_activity_racedetail_body_contents_detailbox_racelocation_title_textview );
		raceLocationTextView.setText( raceInfo.getRaceLocation() );

		// 速報ボタンの処理設定
		Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
		updateButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					// 速報が自動停止した場合は速報が停止しているが表示は「速報停止」となっている
					// 動作的に問題ないため、そのままとする
					RaceInfo raceInfo = ( RaceInfo )v.getTag();

					// 速報停止中
					if( raceInfo.getRaceUpdate() == RaceInfo.INT_RACEUPDATE_OFF )
					{
						// 大会を速報状態にする
						Logic.setUpdateOnRaceId( getContentResolver(), raceInfo.getRaceId() );

						raceInfo.setRaceUpdate( RaceInfo.INT_RACEUPDATE_ON );

						// 速報開始
						CommonLib.setUpdateAlarm( RaceDetailActivity.this, raceInfo.getRaceId(), Common.INT_SERVICE_INTERVAL );

						// 停止カウントを設定
						Logic.setAutoStopCount( RaceDetailActivity.this, Common.INT_COUNT_AUTOSTOP_LASTUPDATE );
						Logic.setRegularStopCount( RaceDetailActivity.this, Common.INT_COUNT_REGULARSTOP );

						// 速報中テキスト表示
						( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_OFF );

						// ボタン表示変更
						( ( Button )v ).setText( getString( R.string.str_btn_updatestop ) );

						// 手動更新を無効化
						Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
						manualButton.setEnabled( false );

						// Toast表示
						Toast.makeText( RaceDetailActivity.this, "速報を開始しました！", Toast.LENGTH_SHORT ).show();
					}
					// 速報中( 予約中の場合は、ボタンは無効になる )
					else
					{
						// データベース変更
						Logic.setUpdateOffRaceId( getContentResolver(), raceInfo.getRaceId() );

						raceInfo.setRaceUpdate( RaceInfo.INT_RACEUPDATE_OFF );

						// 速報停止
						CommonLib.cancelUpdateAlarm( RaceDetailActivity.this, raceInfo.getRaceId() );

						// 速報中テキスト非表示
						( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_ON );

						// ボタン表示変更
						((Button)v).setText( getString( R.string.str_btn_updatestart ) );

						// Toast表示
						Toast.makeText( RaceDetailActivity.this, "速報を停止しました！", Toast.LENGTH_SHORT ).show();

						// 手動更新を有効化
						Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
						manualButton.setEnabled( true );
					}

					return;
				}
			}
		);

		// 予約ボタンの設定
		Button reserveButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_reserve_button );
		reserveButton.setTag( raceInfo );
		reserveButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					RaceInfo raceInfo = ( RaceInfo )v.getTag();

					// 停止中
					if( raceInfo.getRaceUpdate() == RaceInfo.INT_RACEUPDATE_OFF )
					{
						TimePickerDialog dialog = new TimePickerDialog
							(
								RaceDetailActivity.this,
								new OnReserveTimeSetListener( raceInfo, v ),
								CommonLib.getHourOfDay(),
								CommonLib.getMinute(),
								true
							);
						dialog.show();
					}
					else
					{
						// 予約をキャンセルする
						CommonLib.cancelUpdateReserveAlarm( RaceDetailActivity.this );

						// 大会を速報停止状態にする
						Logic.setUpdateOffRaceId( getContentResolver(), raceInfo.getRaceId() );

						raceInfo.setRaceUpdate( RaceInfo.INT_RACEUPDATE_OFF );

						// ボタン表示変更
						( ( Button )v ).setText( getString( R.string.str_btn_reservestart ) );
					}
					return;
				}
			}
		);

		// 手動更新ボタンの設定
		Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
		manualButton.setTag( raceInfo );
		manualButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					RaceInfo raceInfo = ( RaceInfo )v.getTag();
					String raceId = raceInfo.getRaceId();

					// 選手情報を取得する
					List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList( getContentResolver(), raceId );

					// 手動更新タスク起動
					ManualUpdateTask task = new ManualUpdateTask( getContentResolver(), raceInfo.getRaceId() );
					ManualUpdateTask.TaskParam param = task.new TaskParam();
					param.setRaceId( raceInfo.getRaceId() );
					param.setUrl( getString( R.string.str_txt_defaulturl ) );
					param.setRunnerInfoList( runnerInfoList );
					task.execute( param );
				}
			}
		);

		return;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// 大会情報取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId );

		// 自動更新、手動更新ボタンの表示状態設定
		// 速報状態の大会情報を取得
		RaceInfo updateRaceInfo = Logic.getUpdateRaceId( getContentResolver() );

		Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
		Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );

		// 速報中の大会なし
		if( updateRaceInfo == null )
		{
			updateButton.setText( getString( R.string.str_btn_updatestart ) );
			updateButton.setEnabled( true );
			manualButton.setEnabled( true );

			// 速報中テキスト非表示
			( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_OFF );
		}
		// 選択中の大会IDと速報中の大会が一致
		else if( updateRaceInfo.getRaceId().equals( raceId ) )
		{
			// 速報中
			if( updateRaceInfo.getRaceUpdate() == RaceInfo.INT_RACEUPDATE_ON )
			{
				// アラーム停止中
				if( !CommonLib.isSetUpdateAlarm( RaceDetailActivity.this ) )
				{
					updateButton.setText( getString( R.string.str_btn_updatestart ) );

					Logic.setUpdateOffRaceId( getContentResolver(), updateRaceInfo.getRaceId() );

					raceInfo.setRaceUpdate( RaceInfo.INT_RACEUPDATE_OFF );

					updateButton.setText( getString( R.string.str_btn_updatestop ) );
					manualButton.setEnabled( true );

					// 速報中テキスト非表示
					( ( RaceTabActivity )getParent()).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_OFF );
				}
				else
				{
					updateButton.setText( getString( R.string.str_btn_updatestop ) );
					
					manualButton.setEnabled( false );
				}
				updateButton.setEnabled( true );
			}
			// 予約中
			else if( updateRaceInfo.getRaceUpdate() == RaceInfo.INT_RACEUPDATE_RESERVE ){
				// TODO:処理を記載する
			}
			
		}
		// 他の大会が速報中
		else
		{
			updateButton.setText( getString( R.string.str_btn_updatestart ) );
			updateButton.setEnabled( false );
			manualButton.setEnabled( false );

			// 速報中テキスト非表示
			( ( RaceTabActivity )getParent()).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_OFF );
		}

		updateButton.setTag( raceInfo );

		return;
	}

	/**
	 * 手動更新タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class ManualUpdateTask extends AsyncTask< ManualUpdateTask.TaskParam, Void, List<RunnerInfo> >{

		/**
		 * コンテントリゾルバ
		 */
		private ContentResolver m_ContentResolver;

		/**
		 * 大会ID
		 */
		private String m_RaceId = null;

		/**
		 * 進捗ダイアログ
		 */
		private ProgressDialog m_ProgressDialog = null;

		/**
		 * コンストラクタ
		 * @param contentResolver コンテントリゾルバ
		 * @param raceId 大会ID
		 */
		public ManualUpdateTask( ContentResolver contentResolver, String raceId )
		{
			super();
			m_ContentResolver = contentResolver;
			m_RaceId = raceId;

			return;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// ダイアログ作成
			m_ProgressDialog = new ProgressDialog( RaceDetailActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_manual ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton
			(
				DialogInterface.BUTTON_NEGATIVE,
				getResources().getString( R.string.str_dialog_msg_cancel ),
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						cancel( true );

						return;
					}
				}
			);

			m_ProgressDialog.show();

			return;
		}

		@Override
		protected List<RunnerInfo> doInBackground( TaskParam... params )
		{
			// ネットワークから選手情報取得
			String url = params[0].getUrl();
			String raceId = params[0].getRaceId();
			List<RunnerInfo> runnerInfoList = params[0].getRunnerInfoList();

			// 最新の選手情報を取得する
			return Logic.getNetRunnerInfoList( url, raceId, runnerInfoList );
		}

		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList )
		{
			String message = null;

			if( runnerInfoList != null )
			{
				// データアップデート
				boolean updateFlg = Logic.updateRunnerInfo( m_ContentResolver, m_RaceId, runnerInfoList );
				
				if( updateFlg )
				{
					message = "★★★更新情報があります★★★";
				}
				else
				{
					message = "更新情報はありません。";
				}
			}
			else
			{
				message = "手動更新に失敗しました。";
			}

			// ダイアログ削除
			if( m_ProgressDialog != null )
			{
				m_ProgressDialog.dismiss();
			}

			Toast.makeText( RaceDetailActivity.this, message, Toast.LENGTH_SHORT ).show();

			return;
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();

			// ダイアログ削除
			if( m_ProgressDialog != null )
			{
				m_ProgressDialog.dismiss();
			}

			Toast.makeText( RaceDetailActivity.this, "手動更新をキャンセルしました。", Toast.LENGTH_SHORT ).show();

			return;
		}

		/**
		 * タスクパラメータ
		 * @author Hayato Matsumuro
		 *
		 */
		public class TaskParam{

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
			 * @return　アップデートサイトURL
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
			 * @param runnerInfoList 選手情報リスト
			 */
			public void setRunnerInfoList( List<RunnerInfo> runnerInfoList )
			{
				this.runnerInfoList = runnerInfoList;

				return;
			}
		}
	}

	/**
	 * 予約タイマーセットリスナー
	 * @author Hayato Matsumuro
	 *
	 */
	private class OnReserveTimeSetListener implements OnTimeSetListener
	{
		/**
		 * 大会情報
		 */
		private RaceInfo m_RaceInfo;

		/**
		 * ビュー
		 */
		private View m_View;

		/**
		 * コンストラクタ
		 * @param raceInfo 大会情報
		 * @param v ビュー
		 */
		public OnReserveTimeSetListener( RaceInfo raceInfo, View v )
		{
			m_RaceInfo = raceInfo;
			m_View = v;
			return;
		}

		@Override
		public void onTimeSet( TimePicker view, int hourOfDay, int minute )
		{
			long alarmTime = CommonLib.getAlarmTime( hourOfDay, minute );

			// アラームを設定する
			CommonLib.setUpdateReserveAlarm( RaceDetailActivity.this, m_RaceInfo.getRaceId(), alarmTime );

			// 大会を速報予約状態にする
			Logic.setUpdateReserveRaceId( getContentResolver(), m_RaceInfo.getRaceId() );

			m_RaceInfo.setRaceUpdate( RaceInfo.INT_RACEUPDATE_RESERVE );

			// ボタン表示変更
			( ( Button )m_View ).setText( getString( R.string.str_btn_reservecancel ) );

			// アラーム時間の設定
			Logic.setReserveTime( RaceDetailActivity.this, hourOfDay, minute );

			// 速報バーの表示更新
			( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_RACEUPDATE_RESERVE );

			return;
		}	
	}
}
