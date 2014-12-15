package com.hm.runrealtimeupdate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 共通ライブラリ
 * @author Hayato Matsumuro
 *
 */
public class CommonLib
{
	/**
	 * 定期更新アラームを設定する
	 * @param context コンテキスト
	 * @param raceId 大会ID
	 * @param interval 更新間隔
	 */
	public static void setUpdateAlarm( Context context, String raceId, int interval )
	{
		AlarmManager alarmManager = ( AlarmManager )context.getSystemService( Context.ALARM_SERVICE );

		Intent intent = new Intent( context, UpdateService.class );
		intent.putExtra( UpdateService.STR_INTENT_RACEID, raceId );

		long time = System.currentTimeMillis();

		PendingIntent pendingIntent = PendingIntent.getService( context, UpdateService.INT_REQUESTCODE_START, intent, PendingIntent.FLAG_UPDATE_CURRENT );

		alarmManager.setRepeating( AlarmManager.RTC, time, interval, pendingIntent );

		return;
	}

	/**
	 * 更新サービスを停止する
	 * @param context コンテキスト
	 * @param raceId 大会ID
	 */
	public static void cancelUpdateAlarm( Context context, String raceId )
	{
		AlarmManager alarmManager = ( AlarmManager )context.getSystemService( Context.ALARM_SERVICE );

		Intent intent = new Intent( context, UpdateService.class );
		intent.putExtra( UpdateService.STR_INTENT_RACEID, raceId );

		PendingIntent pendingIntent = PendingIntent.getService( context, UpdateService.INT_REQUESTCODE_START, intent, PendingIntent.FLAG_CANCEL_CURRENT );

		alarmManager.cancel( pendingIntent );
	    pendingIntent.cancel();
		context.stopService( intent );

	    return;
	}

	/**
	 * 更新アラームが設定されているか確認する
	 * @param context コンテキスト
	 * @return true:起動中/false:起動中でない
	 */
	public static boolean isSetUpdateAlarm( Context context )
	{
		Intent intent = new Intent( context, UpdateService.class );

		PendingIntent pendingIntent = PendingIntent.getService
		(
			context,
			UpdateService.INT_REQUESTCODE_START,
			intent,
			PendingIntent.FLAG_NO_CREATE
		);

		if( pendingIntent == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
