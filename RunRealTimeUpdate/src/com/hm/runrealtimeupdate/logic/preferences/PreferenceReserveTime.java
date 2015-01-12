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
	 * @param time 予約時間
	 */
	public static void saveReserveTime( Context context, ReserveTime time )
	{
		Map< String, Integer > map = new HashMap< String, Integer>();

		map.put( KEY_RESERVEHOUR, time.hour );
		map.put( KEY_RESERVEMINUTE, time.minute );

		PreferenceAccess.saveIntData( context, map );

		return;
	}

	/**
	 * 予約時間をプリファレンスからロードする
	 * @param context コンテキスト
	 * @return 予約時間
	 */
	public static ReserveTime loadReserveTime( Context context )
	{
		PreferenceReserveTime.ReserveTime time = new PreferenceReserveTime().new ReserveTime();
		time.hour = PreferenceAccess.loadIntData( context, KEY_RESERVEHOUR );
		time.minute = PreferenceAccess.loadIntData( context, KEY_RESERVEMINUTE );
		return time;
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

	/**
	 * 予約時間
	 * @author Hayato Matsumuro
	 *
	 */
	public class ReserveTime
	{
		/**
		 * 時
		 */
		public int hour;

		/**
		 * 分
		 */
		public int minute;
	}
}
