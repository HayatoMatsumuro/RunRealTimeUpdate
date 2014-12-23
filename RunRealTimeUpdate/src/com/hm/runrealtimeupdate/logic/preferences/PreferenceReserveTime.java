package com.hm.runrealtimeupdate.logic.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * 予約時間 プリファレンス
 * @author Hayato Matsumuro
 *
 */
public class PreferenceReserveTime
{
	/**
	 * キー　予約 時
	 */
	private static final String KEY_RESERVEHOUR = "reservehour";

	/**
	 * キー 予約分
	 */
	private static final String KEY_RESERVEMINUTE = "reserveminute";
	
	/**
	 * 予約時間をプリファレンスに保存する
	 * @param context コンテキスト
	 * @param hour 予約時
	 * @param minute 予約分
	 */
	public static void saveReserveTime( Context context, int hour, int minute )
	{
		Map< String, Integer > map = new HashMap< String, Integer>();

		map.put( KEY_RESERVEHOUR, hour );
		map.put( KEY_RESERVEMINUTE, minute );

		PreferenceAccess.saveIntData( context, map );

		return;
	}

	/**
	 * 予約時間をプリファレンスからロードする
	 * @param context コンテキスト
	 * @param hour 予約時
	 * @param minute 予約分
	 */
	public static void loadReserveTime( Context context, Integer hour, Integer minute )
	{
		hour = PreferenceAccess.loadIntData( context, KEY_RESERVEHOUR );
		minute = PreferenceAccess.loadIntData( context, KEY_RESERVEMINUTE );
		return;
	}

	/**
	 * 予約時間をプリファレンスから削除する
	 * @param context コンテキスト
	 */
	public static void deleteReserveTime( Context context )
	{
		PreferenceAccess.deleteData( context, KEY_RESERVEHOUR );
		PreferenceAccess.deleteData( context, KEY_RESERVEMINUTE );
		return;
	}

}
