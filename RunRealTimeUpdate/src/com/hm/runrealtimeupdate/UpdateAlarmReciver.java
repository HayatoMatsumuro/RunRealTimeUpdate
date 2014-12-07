package com.hm.runrealtimeupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateAlarmReciver extends BroadcastReceiver {

	public static final String ACTION_UPDATESTART = "updatestart";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if( action.equals( ACTION_UPDATESTART ))
		{
			return;
		}

	}

}
