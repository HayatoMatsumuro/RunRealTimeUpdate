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
class CommonLib
{
	/**
	 * プロパティファイル キー ステータス
	 */
	private static final String STR_PROPERTIES_KEY_STATUS = "status";

	/**
	 * プロパティファイル キー 大会ID
	 */
	private static final String STR_PROPERTIES_KEY_RACEID = "raceid";

	/**
	 * プロパティファイル キー 大会名
	 */
	private static final String STR_PROPERTIES_KEY_RACENAME = "racename";

	/**
	 * プロパティファイル ステータス 都市型
	 */
	private static final String STR_PROPERTIES_STATUS_CITY = "city";

	/**
	 * 定期更新アラームを設定する
	 * @param context コンテキスト
	 * @param raceId 大会ID
	 * @param interval 更新間隔
	 */
	static void setUpdateAlarm( Context context, String raceId, int interval )
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
	static void cancelUpdateAlarm( Context context, String raceId )
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
	static boolean isSetUpdateAlarm( Context context )
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
	static void setUpdateReserveAlarm( Context context, String raceId, long time )
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
	static void cancelUpdateReserveAlarm( Context context, String raceId )
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
	static boolean isUpdateReserveAlarm( Context context )
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
	static int getHourOfDay()
	{
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		return date.getHours();
	}

	/**
	 * 現在の分を取得する
	 * @return 現在の分( 0～59 )
	 */
	static int getMinute()
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
	static long getAlarmTime( int hourOfDay, int minute )
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
	static List<CityProperties> getCityProperties( Context context )
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

							city.parserInfo = getParserInfoByRaceId( context, city.raceId );
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
	static ParserInfo getParserInfoByRaceId( Context context, String raceId )
	{
		String[] strArray = raceId.split( "," );

		CommonLib.ParserInfo parserInfo = new CommonLib().new ParserInfo();

		if( strArray.length == 1 )
		{
			parserInfo.url = context.getResources().getString( R.string.str_txt_defaulturl );
			parserInfo.pass = raceId;
			parserInfo.parserClassName = context.getResources().getString( R.string.str_txt_defaultupdateparser );
		}
		else
		{
			parserInfo.url = strArray[0];
			parserInfo.pass = null;
			parserInfo.parserClassName = strArray[1];
		}

		return parserInfo;
	}

	/**
	 * パーサー情報
	 * @author Hayato Matsumuro
	 *
	 */
	class ParserInfo
	{
		/**
		 * URL
		 */
		String url;

		/**
		 * パス
		 */
		String pass;

		/**
		 * パーサー名
		 */
		String parserClassName;
	}

	/**
	 * 都市型マラソンプロパティ
	 * @author Hayato Matsumuro
	 *
	 */
	class CityProperties
	{
		/**
		 * 大会ID
		 */
		String raceId;

		/**
		 * 大会名
		 */
		String raceName;

		/**
		 * パーサー情報
		 */
		ParserInfo parserInfo;

		/**
		 * コンストラクタ
		 */
		CityProperties()
		{
			parserInfo = new ParserInfo();
		}
	}
}
