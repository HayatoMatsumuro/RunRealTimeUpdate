package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
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
		raceNameTextView.setText( raceInfo.name );

		// 大会日
		TextView raceDateTextView = ( TextView )findViewById( R.id.id_activity_racedetail_body_contents_detailbox_racedate_title_textview );
		raceDateTextView.setText( raceInfo.date );

		// 開催地
		TextView raceLocationTextView = ( TextView )findViewById( R.id.id_activity_racedetail_body_contents_detailbox_racelocation_title_textview );
		raceLocationTextView.setText( raceInfo.location );

		// 速報ボタンの設定
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

					Button reserveButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_reserve_button );
					Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );

					// 速報停止中
					if( raceInfo.updateSts == RaceInfo.INT_UPDATESTS_OFF )
					{
						// 大会を速報状態にする
						Logic.setUpdateOnRaceId( getContentResolver(), raceInfo.id );

						raceInfo.updateSts = RaceInfo.INT_UPDATESTS_ON;

						// 速報開始
						CommonLib.setUpdateAlarm( RaceDetailActivity.this, raceInfo.id, Common.INT_SERVICE_INTERVAL );

						// 停止カウントを設定
						Logic.setAutoStopCount( RaceDetailActivity.this, Common.INT_COUNT_AUTOSTOP_LASTUPDATE );
						Logic.setRegularStopCount( RaceDetailActivity.this, Common.INT_COUNT_REGULARSTOP );

						// 速報中テキスト表示
						( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_UPDATESTS_ON );

						// ボタン表示変更
						( ( Button )v ).setText( getString( R.string.str_btn_updatestop ) );

						// 予約ボタンを無効化
						reserveButton.setEnabled( false );

						// 手動ボタンを無効化
						manualButton.setEnabled( false );

						// Toast表示
						Toast.makeText( RaceDetailActivity.this, "速報を開始しました！", Toast.LENGTH_SHORT ).show();
					}
					// 速報中( 予約中の場合は、ボタンは無効になる )
					else
					{
						// データベース変更
						Logic.setUpdateOffRaceId( getContentResolver(), raceInfo.id );

						raceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;

						// 速報停止
						CommonLib.cancelUpdateAlarm( RaceDetailActivity.this, raceInfo.id );

						// 速報中テキスト非表示
						( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_UPDATESTS_OFF );

						// ボタン表示変更
						((Button)v).setText( getString( R.string.str_btn_updatestart ) );

						// Toast表示
						Toast.makeText( RaceDetailActivity.this, "速報を停止しました！", Toast.LENGTH_SHORT ).show();

						// 予約ボタンを有効化
						reserveButton.setEnabled( true );

						// 手動ボタンを有効化
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
					if( raceInfo.updateSts == RaceInfo.INT_UPDATESTS_OFF )
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
						CommonLib.cancelUpdateReserveAlarm( RaceDetailActivity.this, raceInfo.id );

						// 大会を速報停止状態にする
						Logic.setUpdateOffRaceId( getContentResolver(), raceInfo.id );
						raceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;

						// ボタン表示変更
						( ( Button )v ).setText( getString( R.string.str_btn_reservestart ) );

						// 自動ボタン
						Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
						updateButton.setEnabled( true );

						// 手動ボタン
						Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
						manualButton.setEnabled( true );

						// Toast表示
						Toast.makeText( RaceDetailActivity.this, "予約を解除しました！", Toast.LENGTH_SHORT ).show();

						// 速報バーの表示更新
						( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_UPDATESTS_OFF );
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

					// 速報開始
					CommonLib.setUpdateAlarm( RaceDetailActivity.this, raceInfo.id, Common.INT_SERVICE_INTERVAL );

					// 停止カウントを設定
					Logic.setAutoStopCount( RaceDetailActivity.this, Common.INT_COUNT_MANUALSTOP );
					Logic.setRegularStopCount( RaceDetailActivity.this, Common.INT_COUNT_MANUALSTOP );

					Toast.makeText( RaceDetailActivity.this, "手動更新を開始しました。", Toast.LENGTH_SHORT ).show();
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
		Button reserveButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_reserve_button );
		Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );

		// 速報中の大会なし
		if( updateRaceInfo == null )
		{
			// 速報ボタン
			updateButton.setText( getString( R.string.str_btn_updatestart ) );
			updateButton.setEnabled( true );

			// 予約ボタン
			reserveButton.setText( getString( R.string.str_btn_reservestart ) );
			reserveButton.setEnabled( true );

			// 手動ボタン
			manualButton.setEnabled( true );
		}
		// 選択中の大会IDと速報中の大会が一致
		else if( updateRaceInfo.id.equals( raceId ) )
		{
			// 速報中
			if( updateRaceInfo.updateSts == RaceInfo.INT_UPDATESTS_ON )
			{
				// アラーム停止中
				if( !CommonLib.isSetUpdateAlarm( RaceDetailActivity.this ) )
				{
					// 速報停止状態
					Logic.setUpdateOffRaceId( getContentResolver(), updateRaceInfo.id );
					raceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;

					// 速報ボタン
					updateButton.setText( getString( R.string.str_btn_updatestart ) );
					updateButton.setEnabled( true );

					// 予約ボタン
					reserveButton.setText( getString( R.string.str_btn_reservestart ) );
					reserveButton.setEnabled( true );

					// 手動ボタン
					manualButton.setEnabled( true );
				}
				// アラーム起動中
				else
				{
					// 速報ボタン
					updateButton.setText( getString( R.string.str_btn_updatestop ) );
					updateButton.setEnabled( true );

					// 予約ボタン
					reserveButton.setText( getString( R.string.str_btn_reservestart ) );
					reserveButton.setEnabled( false );

					// 手動ボタン
					manualButton.setEnabled( false );
				}
			}
			// 予約中
			else if( updateRaceInfo.updateSts == RaceInfo.INT_UPDATESTS_RESERVE )
			{
				// アラーム停止中
				if( !CommonLib.isUpdateReserveAlarm( RaceDetailActivity.this ) )
				{
					// 速報停止状態
					Logic.setUpdateOffRaceId( getContentResolver(), updateRaceInfo.id );
					raceInfo.updateSts = RaceInfo.INT_UPDATESTS_OFF;

					// 速報ボタン
					updateButton.setText( getString( R.string.str_btn_updatestart ) );
					updateButton.setEnabled( true );

					// 予約ボタン
					reserveButton.setText( getString( R.string.str_btn_reservestart ) );
					reserveButton.setEnabled( true );

					// 手動ボタン
					manualButton.setEnabled( true );
				}
				// アラーム起動中
				else
				{
					// 速報ボタン
					updateButton.setText( getString( R.string.str_btn_updatestart ) );
					updateButton.setEnabled( false );

					// 予約ボタン
					reserveButton.setText( getString( R.string.str_btn_reservecancel ) );
					reserveButton.setEnabled( true );

					// 手動ボタン
					manualButton.setEnabled( false );
				}
			}
			
		}
		// 他の大会が速報中
		else
		{
			// 速報ボタン
			updateButton.setText( getString( R.string.str_btn_updatestart ) );
			updateButton.setEnabled( false );

			// 予約ボタン
			reserveButton.setText( getString( R.string.str_btn_reservestart ) );
			reserveButton.setEnabled( false );

			// 手動ボタン
			manualButton.setEnabled( false );
		}

		// 速報バーの表示
		( ( RaceTabActivity )getParent() ).setDispUpdateBar( raceInfo.updateSts );

		updateButton.setTag( raceInfo );

		return;
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
			CommonLib.setUpdateReserveAlarm( RaceDetailActivity.this, m_RaceInfo.id, alarmTime );

			// 大会を速報予約状態にする
			Logic.setUpdateReserveRaceId( getContentResolver(), m_RaceInfo.id );

			m_RaceInfo.updateSts = RaceInfo.INT_UPDATESTS_RESERVE;

			// アラーム時間の設定
			Logic.setReserveTime( RaceDetailActivity.this, hourOfDay, minute );

			// 速報バーの表示更新
			( ( RaceTabActivity )getParent() ).setDispUpdateBar( RaceInfo.INT_UPDATESTS_RESERVE );

			// ボタン表示変更
			( ( Button )m_View ).setText( getString( R.string.str_btn_reservecancel ) );

			// 自動ボタン
			Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
			updateButton.setEnabled( false );

			// 手動ボタン
			Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
			manualButton.setEnabled( false );

			// Toast表示
			Toast.makeText( RaceDetailActivity.this, "速報の予約をしました！", Toast.LENGTH_SHORT ).show();

			return;
		}	
	}
}
