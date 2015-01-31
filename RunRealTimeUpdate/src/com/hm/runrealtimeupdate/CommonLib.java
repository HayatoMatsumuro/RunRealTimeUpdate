package com.hm.runrealtimeupdate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

/**
 * 共通ライブラリ
 * @author Hayato Matsumuro
 *
 */
public class CommonLib
{
	private static final String STR_PROPERTIES_KEY_STATUS = "status";
	private static final String STR_PROPERTIES_KEY_RACEID = "raceid";
	private static final String STR_PROPERTIES_KEY_RACENAME = "racename";
	
	private static final String STR_PROPERTIES_STATUS_CITY = "city";


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
	 * @param time　時間
	 */
	public static void setUpdateReserveAlarm( Context context, String raceId, long time )
	{
		AlarmManager alarmManager = ( AlarmManager )context.getSystemService( Context.ALARM_SERVICE );

		Intent intent = new Intent( context, UpdateBroadcastReceiver.class );
		intent.setAction( UpdateBroadcastReceiver.STR_INTENT_ACTION_UPDATESTART );
		intent.putExtra( UpdateBroadcastReceiver.STR_INTENT_RACEID, raceId );

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

	/**
	 * 更新予約アラームを取り消す
	 * @param context コンテキスト
	 * @param raceId 大会ID
	 */
	public static void cancelUpdateReserveAlarm( Context context, String raceId )
	{
		AlarmManager alarmManager = ( AlarmManager )context.getSystemService( Context.ALARM_SERVICE );

		Intent intent = new Intent( context, UpdateBroadcastReceiver.class );
		intent.setAction( UpdateBroadcastReceiver.STR_INTENT_ACTION_UPDATESTART );
		intent.putExtra( UpdateBroadcastReceiver.STR_INTENT_RACEID, raceId );

		PendingIntent pendingIntent = PendingIntent.getBroadcast
				(
					context,
					Common.INT_REQUESTCODE_UPDATERESERVE,
					intent,
					PendingIntent.FLAG_CANCEL_CURRENT
				);

		alarmManager.cancel( pendingIntent );
	    pendingIntent.cancel();

	    return;
	}

	/**
	 * 更新予約アラームが設定されているか確認する
	 * @param context コンテキスト
	 * @return　true:起動中/false:起動中でない
	 */
	public static boolean isUpdateReserveAlarm( Context context )
	{
		Intent intent = new Intent( context, UpdateBroadcastReceiver.class );
		intent.setAction( UpdateBroadcastReceiver.STR_INTENT_ACTION_UPDATESTART );

		PendingIntent pendingIntent = PendingIntent.getBroadcast
				(
					context,
					Common.INT_REQUESTCODE_UPDATERESERVE,
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
	 * 現在の時を取得する
	 * @return 現在の時( 0 ～ 23 )
	 */
	public static int getHourOfDay()
	{
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return date.getHours();
	}

	/**
	 * 現在の分を取得する
	 * @return 現在の分( 0～59 )
	 */
	public static int getMinute()
	{
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return date.getMinutes();
	}

	/**
	 * アラームの設定時間を取得する
	 *  引数設定の次の時分で設定する
	 *  引数の設定時間が現在の時間と10秒以内ならば、1日後とする
	 * @param hourofday 時
	 * @param minute 分
	 * @return アラーム設定時間
	 */
	public static long getAlarmTime( int hourOfDay, int minute )
	{
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.SECOND, 10 );
		Date checkDate = cal.getTime();
		
		Date alarmDate = cal.getTime();
		alarmDate.setHours( hourOfDay );
		alarmDate.setMinutes( minute );
		alarmDate.setSeconds( 0 );

		long msec = alarmDate.getTime();

		// アラーム時間が現在よりも前だったら補正する
		if( alarmDate.before( checkDate ) )
		{
			// 1日加算
			msec = msec + 86400000;
		}

		return msec;
	}

	/**
	 * 都市型マラソンのプロパティファイルを読み込む
	 * @param context コンテキスト
	 * @return 都市型マラソンのプロパティファイルリスト
	 */
	public static List<CityProperties> getCityProperties( Context context )
	{
		List<CityProperties> cityPropertiesList = new ArrayList<CityProperties>();

		AssetManager assetManager = context.getResources().getAssets();

		try
		{
			Properties properties = null;
			InputStream inputStream = null;

			String[] filelist = assetManager.list("");

			for( String str : filelist )
			{
				// プロパティファイルからデータ読み込み
				if( str.endsWith( ".properties" ) )
				{
					try
					{
						inputStream = assetManager.open( str );
						properties = new Properties();
						properties.load( inputStream );

						String status = properties.get( STR_PROPERTIES_KEY_STATUS ).toString();

						// 都市型のみ読み込み
						if( ( status != null ) && ( status.equals( STR_PROPERTIES_STATUS_CITY ) ) )
						{
							CommonLib.CityProperties city = new CommonLib().new CityProperties();
							city.raceId = properties.get( STR_PROPERTIES_KEY_RACEID ).toString();
							city.raceName = properties.get( STR_PROPERTIES_KEY_RACENAME ).toString();

							city.parserInfo = getParserInfoByRaceId( city.raceId );
							cityPropertiesList.add( city );
						}
					}
					catch( IOException e )
					{
						return null;
					}
					finally
					{
						try
						{
							inputStream.close();
						}
						catch( IOException e )
						{
							return null;
						}
					}
				}
			}
		}
		catch( IOException e )
		{
			return null;
		}

		return cityPropertiesList;
	}

	/**
	 * 大会IDからパーサー情報を取得する。
	 * 大会IDがpassの場合は、null を返す
	 * @param raceId
	 * @return　パーサー情報( null は大会情報なし )
	 */
	public static ParserInfo getParserInfoByRaceId( String raceId )
	{
		String[] strArray = raceId.split( "," );

		if( strArray.length == 1 )
		{
			return null;
		}
		else
		{
			CommonLib.ParserInfo parserInfo = new CommonLib().new ParserInfo();
			parserInfo.url = strArray[0];
			parserInfo.parserClassName = strArray[1];
			return parserInfo;
		}
	}

	/**
	 * 都市型マラソンプロパティ
	 * @author Hayato Matsumuro
	 *
	 */
	public class CityProperties
	{
		/**
		 * 大会ID
		 */
		public String raceId;

		/**
		 * 大会名
		 */
		public String raceName;

		/**
		 * パーサー情報
		 */
		public ParserInfo parserInfo;

		/**
		 * コンストラクタ
		 */
		public CityProperties()
		{
			parserInfo = new ParserInfo();
		}
	}

	public class ParserInfo
	{
		/**
		 * URL
		 */
		public String url;

		/**
		 * パーサー名
		 */
		public String parserClassName;
	}
}
