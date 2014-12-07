package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RaceDetailActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	/**
	 * タイマー間隔
	 */
	private static int INT_TIMER_INTERVAL = 120000;
	
	/**
	 * 速報を行う回数
	 * 1日で自動的に速報が停止する
	 */
	private static int INT_TIMER_INTERAVAL_CNT_MAX = 86400000 / INT_TIMER_INTERVAL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racedetail);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(RaceDetailActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
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
        updateButton.setTag(raceInfo);
        updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 速報が自動停止した場合は速報が停止しているが表示は「速報停止」となっている
				// 動作的に問題ないため、そのままとする
				RaceInfo raceInfo = ( RaceInfo )v.getTag();
				
				AlarmManager alarmManager = ( AlarmManager )RaceDetailActivity.this.getSystemService( Context.ALARM_SERVICE );
				
				if( !raceInfo.isRaceUpdate() ){
					// 速報を開始する
					
					// 大会を速報状態にする
					Logic.setUpdateOnRaceId(getContentResolver(), raceInfo.getRaceId());
					
					raceInfo.setRaceUpdate(true);
					
					// 速報開始
					Intent intent = new Intent( RaceDetailActivity.this, UpdateService.class );
					intent.putExtra(UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId());
					long time = System.currentTimeMillis();
					
					PendingIntent pendingIntent = PendingIntent.getService(
							RaceDetailActivity.this,
							-1,
							intent,
							PendingIntent.FLAG_UPDATE_CURRENT );
					
					alarmManager.setRepeating( AlarmManager.RTC, time, INT_TIMER_INTERVAL, pendingIntent );
					
					// 更新カウントを設定
					Logic.setUpdateCountMax( RaceDetailActivity.this, INT_TIMER_INTERAVAL_CNT_MAX );
					
					// 速報中テキスト表示
					(( RaceTabActivity )getParent()).setVisibilityUpdateExe( View.VISIBLE );
					
					// ボタン表示変更
					((Button)v).setText(getString(R.string.str_btn_updatestop));
					
					// 手動更新を無効化
					Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
					manualButton.setEnabled( false );
					
					// Toast表示
					Toast.makeText( RaceDetailActivity.this, "速報を開始しました！", Toast.LENGTH_SHORT ).show();
				} else {
					// 速報停止ボタン押し
					
					// データベース変更
					Logic.setUpdateOffRaceId(getContentResolver(), raceInfo.getRaceId());

					raceInfo.setRaceUpdate(false);
					
					// 速報停止
					Intent intent = new Intent( RaceDetailActivity.this, UpdateService.class );
					intent.putExtra( UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId() );
					
				    PendingIntent pendingIntent = PendingIntent.getService(
				    		RaceDetailActivity.this,
				            -1,
				            intent,
				            PendingIntent.FLAG_CANCEL_CURRENT);
				    
				    alarmManager.cancel( pendingIntent );
				    pendingIntent.cancel();
				    
					// 速報中テキスト非表示
					(( RaceTabActivity )getParent()).setVisibilityUpdateExe( View.GONE );
					
					// ボタン表示変更
					((Button)v).setText(getString(R.string.str_btn_updatestart));
					

					// Toast表示
					Toast.makeText( RaceDetailActivity.this, "速報を停止しました！", Toast.LENGTH_SHORT ).show();
					
					// 手動更新を有効化
					Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
					manualButton.setEnabled( true );
				}
			}
		});
        
        // 手動更新ボタンの設定
        Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
        manualButton.setTag( raceInfo );
        manualButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
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
		});
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		// 大会情報取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra( STR_INTENT_RACEID );
		
		// 自動更新、手動更新ボタンの表示状態設定
		// 速報状態の大会情報を取得
		RaceInfo updateRaceInfo = Logic.getUpdateRaceId( getContentResolver() );
		
		Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
		Button manualButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_manual_button );
		
		// 速報中の大会なし
		if( updateRaceInfo == null ){
        	updateButton.setText( getString( R.string.str_btn_updatestart ) );
        	updateButton.setEnabled( true );
        	manualButton.setEnabled( true );
        }
		// 選択中の大会IDと速報中の大会が一致
		else if( updateRaceInfo.getRaceId().equals( raceId ) )
		{
        	updateButton.setText( getString( R.string.str_btn_updatestop ) );
        	updateButton.setEnabled( true );
        	manualButton.setEnabled( false );
        }
		// 他の大会が速報中
		else{
        	updateButton.setText( getString( R.string.str_btn_updatestart ) );
        	updateButton.setEnabled( false );
        	manualButton.setEnabled( false );
        }
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
		
		public ManualUpdateTask( ContentResolver contentResolver, String raceId ){
			super();
			m_ContentResolver = contentResolver;
			m_RaceId = raceId;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			m_ProgressDialog = new ProgressDialog( RaceDetailActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_manual ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getResources().getString( R.string.str_dialog_msg_cancel ), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancel( true );
				}
				
			});
			
			m_ProgressDialog.show();
		}
		
		@Override
		/**
		 * params[0]:アップデートサイトURL
		 */
		protected List<RunnerInfo> doInBackground( TaskParam... params ) {
			
			// ネットワークから選手情報取得
			String url = params[0].getUrl();
			String raceId = params[0].getRaceId();
			List<RunnerInfo> runnerInfoList = params[0].getRunnerInfoList();
			
			// 最新の選手情報を取得する
			return Logic.getNetRunnerInfoList( url, raceId, runnerInfoList );
		}
		
		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList ){
			
			String message = null;
			
			if( runnerInfoList != null ){
				
				// データアップデート
				boolean updateFlg = Logic.updateRunnerInfo( m_ContentResolver, m_RaceId, runnerInfoList );
				
				if( updateFlg ){
					message = "★★★更新情報があります★★★";
				}else{
					message = "更新情報はありません。";
				}
			}else{
				message = "手動更新に失敗しました。";
			}
			

			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}
			
			Toast.makeText( RaceDetailActivity.this, message, Toast.LENGTH_SHORT).show();
			
			return;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}

			Toast.makeText( RaceDetailActivity.this, "手動更新をキャンセルしました。", Toast.LENGTH_SHORT ).show();
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

			public void setRunnerInfoList(List<RunnerInfo> runnerInfoList) {
				this.runnerInfoList = runnerInfoList;
			}
		}
	}
}
