package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class RaceTabActivity extends TabActivity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabactivity_race);
		
		// 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(RaceTabActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
		// ヘッダー
        RelativeLayout headerLayout = (RelativeLayout)findViewById(R.id.id_race_relative_header);
        headerLayout.setBackgroundColor(getResources().getColor(R.color.maincolor));
        
        // ボーダー
        RelativeLayout borderLayout = (RelativeLayout)findViewById(R.id.id_race_relative_border);
        borderLayout.setBackgroundColor(getResources().getColor(R.color.subcolor));
        
        // 大会一覧ボタン
        Button raceListButton = (Button)findViewById(R.id.id_race_btn_racelist);
        raceListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// メイン画面遷移
				Intent intent = new Intent(RaceTabActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		// タブ設定
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent tabIntent;
		
		// 大会詳細
		tabIntent = new Intent(this, RaceDetailActivity.class);
		tabIntent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, raceId);
		spec = tabHost.newTabSpec(getString(R.string.str_tab_race_detail)).setIndicator(getString(R.string.str_tab_race_detail)).setContent(tabIntent);
		tabHost.addTab(spec);
		
		// 選手リスト
		tabIntent = new Intent(this, RunnerActivityGroup.class);
		tabIntent.putExtra(RunnerActivityGroup.STR_INTENT_RACEID, raceId);
		spec = tabHost.newTabSpec(getString(R.string.str_tab_race_runner)).setIndicator(getString(R.string.str_tab_race_runner)).setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}

}
