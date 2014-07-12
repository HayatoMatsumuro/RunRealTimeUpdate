package com.hm.runrealtimeupdate;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

public class PassActivityGroup extends ActivityGroup {

	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitygroup_pass);
		
		// 大会ID取得
		Intent intent = getIntent();
		String raceId = intent.getStringExtra(STR_INTENT_RACEID);
		
		showPassListActivity( raceId );
	}
	
	public void showPassListActivity( String raceId ){
		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.id_pass_layout);
		relativeLayout.removeAllViews();
		
		Intent intent = new Intent( PassActivityGroup.this, PassListActivity.class );
		intent.putExtra( PassListActivity.STR_INTENT_RACEID, raceId );
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		Window passListdActivity = getLocalActivityManager().startActivity( PassListActivity.STR_ACTIVITY_ID, intent );
		relativeLayout.addView( passListdActivity.getDecorView() );
	}
}
