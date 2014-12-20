package com.hm.runrealtimeupdate;

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
	 * インテント 大会ID
	 */
	public static final String STR_INTENT_RACEID = "raceid";

	/**
	 * インテント アクション 更新開始
	 */
	public static final String STR_INTENT_ACTION_UPDATESTART = "updatestart";

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
				// TODO: 更新開始
			}
		}
		return;
	}

}
