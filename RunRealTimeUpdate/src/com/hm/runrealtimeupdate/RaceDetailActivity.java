package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.app.ProgressDialog;
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
        
        // 速報ボタン
        Button updateButton = ( Button )findViewById( R.id.id_activity_racedetail_body_contents_update_button );
        
        // 速報止ボタンの表示設定
        RaceInfo updateRaceInfo = Logic.getUpdateRaceId( getContentResolver() );
        
        if( updateRaceInfo == null ){
        	// 速報中の大会なし
        	updateButton.setText(getString(R.string.str_btn_updatestart));
        	updateButton.setEnabled(true);
        }else if( updateRaceInfo.getRaceId().equals(raceInfo.getRaceId())){
        	// 選択中の大会IDと速報中の大会が一致
        	updateButton.setText(getString(R.string.str_btn_updatestop));
        	updateButton.setEnabled(true);
        }else{
        	// 他の大会が速報中
        	updateButton.setText(getString(R.string.str_btn_updatestart));
        	updateButton.setEnabled(false);
        }
        
        // 速報ボタンの処理設定
        updateButton.setTag(raceInfo);
        updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 速報が自動停止した場合は速報が停止しているが表示は「速報停止」となっている
				// 動作的に問題ないため、そのままとする
				RaceInfo raceInfo = ( RaceInfo )v.getTag();
				
				if( !raceInfo.isRaceUpdate() ){
					// 速報を開始する
					
					// 大会を速報状態にする
					Logic.setUpdateOnRaceId(getContentResolver(), raceInfo.getRaceId());
					
					raceInfo.setRaceUpdate(true);
					
					// 速報開始
					Intent intent = new Intent(RaceDetailActivity.this, UpdateService.class);
					intent.putExtra(UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId());
					startService(intent);
					
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
					Intent intent = new Intent(RaceDetailActivity.this, UpdateService.class);
					intent.putExtra(UpdateService.STR_INTENT_RACEID, raceInfo.getRaceId());
					stopService(intent);
					
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
				
				// 手動更新開始
				String[] params = { null };
				
				params[0] = getString( R.string.str_txt_defaulturl );
				
				// 手動更新タスク起動
				ManualUpdateTask task = new ManualUpdateTask( raceInfo.getRaceId() );
				task.execute( params );
			}
		});
	}
	
	/**
	 * 手動更新タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class ManualUpdateTask extends AsyncTask< String, Void, List<RunnerInfo> >{

		private String m_RaceId = null;
		
		private ProgressDialog m_ProgressDialog = null;
		
		private boolean m_CancellFlg = false;
		
		public ManualUpdateTask( String raceId ){
			super();
			m_RaceId = raceId;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			m_ProgressDialog = new ProgressDialog( RaceDetailActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_namesearch ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getResources().getString( R.string.str_dialog_msg_cancel ), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onCancelled();
				}
				
			});
			
			m_ProgressDialog.show();
		}
		
		@Override
		/**
		 * params[0]:アップデートサイトURL
		 */
		protected List<RunnerInfo> doInBackground(String... params) {
			
			// 選手の更新情報を取得する
			String url = params[0];
			
			// 選手情報を取得する
			List<RunnerInfo> runnerInfoList = Logic.getRunnerInfoList( getContentResolver(), m_RaceId );
			
			// 最新の選手情報を取得する
			return Logic.getNetRunnerInfoList( url, m_RaceId, runnerInfoList );
		}
		
		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList ){
			
			String message = null;
			
			if( m_CancellFlg ){
				return;
			}
			
			if( runnerInfoList != null ){
				
				// データアップデート
				boolean updateFlg = Logic.updateRunnerInfo( getContentResolver(), m_RaceId, runnerInfoList );
				
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
			
			// キャンセルフラグ設定
			m_CancellFlg = true;

			Toast.makeText( RaceDetailActivity.this, "自動更新をキャンセルしました。", Toast.LENGTH_SHORT ).show();
		}
	}
}
