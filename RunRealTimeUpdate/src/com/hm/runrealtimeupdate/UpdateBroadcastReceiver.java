package com.hm.runrealtimeupdate;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 更新BroadcastReceiver
 * @author Hayato Matsumuro
 *
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver
{
	/**
	 * インテント アクション 更新開始
	 */
	public static final String STR_INTENT_ACTION_UPDATESTART = "updatestart";

	/**
	 *　インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	@Override
	public void onReceive( Context context, Intent intent )
	{
		String action = intent.getAction();

		if( action != null )
		{
			// 再起動
			if( action.equals( Intent.ACTION_BOOT_COMPLETED ) )
			{
				// TODO: 再起動時の処理
			}
			// 更新開始
			else if( action.equals( STR_INTENT_ACTION_UPDATESTART ) )
			{
				//String raceId = intent.getStringExtra( STR_INTENT_RACEID );
				//Logic.setUpdateOnRaceId( context.getContentResolver(), raceId );

				Intent intents = new Intent( context, UpdateStartDialogActivity.class );
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT );
		        try {
		            pendingIntent.send();
		        } catch (PendingIntent.CanceledException e) {
		            e.printStackTrace();
		        }
			}
		}
		return;
	}

}
