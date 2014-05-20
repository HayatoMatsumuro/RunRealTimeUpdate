package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.os.Bundle;

public class UpdateListActivity extends Activity {

	public static String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
	}

}
