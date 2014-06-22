package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
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
        
		// タブ設定
		TabHost tabHost = getTabHost();
		tabHost.setCurrentTab(0);
	}

}
