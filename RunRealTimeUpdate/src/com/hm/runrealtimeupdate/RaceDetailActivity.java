package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.content.Intent;
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
        TextView raceNameTextView = (TextView)findViewById(R.id.id_activity_racedetail_detailbox_racename);
        raceNameTextView.setText(raceInfo.getRaceName());
        
        // 大会日
        TextView raceDateTextView = (TextView)findViewById(R.id.id_activity_racedetail_detailbox_racedate_textview);
        raceDateTextView.setText(raceInfo.getRaceDate());
        
        // 開催地
        TextView raceLocationTextView = (TextView)findViewById(R.id.id_activity_racedetail_detailbox_racelocation_textview);
        raceLocationTextView.setText(raceInfo.getRaceLocation());
        
        // 速報ボタン
        Button updateButton = (Button)findViewById(R.id.id_activity_racedetail_detailbox_update_button);
        
        // 速報止ボタンの表示設定
        RaceInfo updateRaceInfo = Logic.getUpdateRaceId( getContentResolver());
        
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
				}
			}
		});
	}
}
