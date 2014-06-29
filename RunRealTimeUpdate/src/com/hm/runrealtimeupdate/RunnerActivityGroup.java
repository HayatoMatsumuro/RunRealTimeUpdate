package com.hm.runrealtimeupdate;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

public class RunnerActivityGroup extends ActivityGroup {

	public static final String STR_INTENT_RACEID = "raceid";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitygroup_runner);
		
		// 大会ID取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra(STR_INTENT_RACEID);
		
		// 選手リスト表示
		showRunnerListActivity(raceId);
	}

	public void showRunnerListActivity( String raceId ){
		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.id_runner_layout);
		relativeLayout.removeAllViews();
		
		Intent intent = new Intent(RunnerActivityGroup.this, RunnerListActivity.class);
		intent.putExtra(RunnerListActivity.STR_INTENT_RACEID, raceId);
		
		Window childActivity = getLocalActivityManager().startActivity(RunnerListActivity.STR_ACTIVITY_ID, intent);
		relativeLayout.addView(childActivity.getDecorView());
	}
}
