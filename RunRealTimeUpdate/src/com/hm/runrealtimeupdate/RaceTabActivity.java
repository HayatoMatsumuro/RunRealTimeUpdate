package com.hm.runrealtimeupdate;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class RaceTabActivity extends TabActivity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabactivity_race);
		
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
