package com.hm.runrealtimeupdate;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class RaceTabActivity extends TabActivity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabactivity_race);
		
		// タブ設定
		TabHost tabHost = getTabHost();
		tabHost.setCurrentTab(0);
	}

}
