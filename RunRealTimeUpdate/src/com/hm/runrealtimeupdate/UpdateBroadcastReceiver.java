package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;

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
	static final String STR_INTENT_ACTION_UPDATESTART = "updatestart";

	/**
	 *　インテント 大会ID
	 */
	static final String STR_INTENT_RACEID = "raceid";

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
				String raceId = intent.getStringExtra( STR_INTENT_RACEID );
				Logic.setUpdateOnRaceId( context.getContentResolver(), raceId );

				// 速報開始
				CommonLib.setUpdateAlarm( context, raceId, Common.INT_SERVICE_INTERVAL );

				// 停止カウントを設定
				Logic.setAutoStopCount( context, Common.INT_COUNT_AUTOSTOP_LASTUPDATE );
				Logic.setRegularStopCount( context, Common.INT_COUNT_REGULARSTOP );

				Intent intents = new Intent( context, UpdateDialogActivity.class );
				intents.putExtra( UpdateDialogActivity.STR_INTENT_TITLE, "速報を開始しました" );

				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT );
				try
				{
					pendingIntent.send();
				}
				catch( PendingIntent.CanceledException e )
				{
					e.printStackTrace();
				}
			}
		}
		return;
	}

}
