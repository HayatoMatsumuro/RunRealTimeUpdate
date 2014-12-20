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

		PendingIntent pendingIntent = PendingIntent.getService( context, Common.INT_REQUESTCODE_UPDATEPERIODIC, intent, PendingIntent.FLAG_UPDATE_CURRENT );

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

		PendingIntent pendingIntent = PendingIntent.getService( context, Common.INT_REQUESTCODE_UPDATEPERIODIC, intent, PendingIntent.FLAG_CANCEL_CURRENT );

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
			Common.INT_REQUESTCODE_UPDATEPERIODIC,
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

	/**
	 * 更新予約アラームを設定する
	 * @param context コンテキスト
	 * @param raceId 大会ID
	 * @param time　時間
	 */
	public static void setUpdateReserveAlarm( Context context, String raceId, long time )
	{
		AlarmManager alarmManager = ( AlarmManager )context.getSystemService( Context.ALARM_SERVICE );

		Intent intent = new Intent( context, UpdateBroadcastReceiver.class );
		intent.putExtra( UpdateBroadcastReceiver.STR_INTENT_RACEID, raceId ); 
		intent.setAction( UpdateBroadcastReceiver.STR_INTENT_ACTION_UPDATESTART );

		PendingIntent pendingIntent = PendingIntent.getBroadcast
				(
					context,
					Common.INT_REQUESTCODE_UPDATERESERVE,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT
				);

		alarmManager.set( AlarmManager.RTC, time, pendingIntent );

		return;
	}
}
