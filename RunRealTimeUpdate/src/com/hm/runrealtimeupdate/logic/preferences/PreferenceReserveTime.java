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

		map.put( KEY_RESERVEHOUR, time.getHour() );
		map.put( KEY_RESERVEMINUTE, time.getMinute() );

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
		int hour = PreferenceAccess.loadIntData( context, KEY_RESERVEHOUR );
		int minute = PreferenceAccess.loadIntData( context, KEY_RESERVEMINUTE );

		PreferenceReserveTime.ReserveTime time = new PreferenceReserveTime().new ReserveTime();
		time.setHour( hour );
		time.setMinute( minute );
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
		private int hour;

		/**
		 * 分
		 */
		private int minute;

		/**
		 * 時を取得する
		 * @return
		 */
		public int getHour() {
			return hour;
		}

		/**
		 * 時を設定する
		 * @param hour
		 */
		public void setHour(int hour) {
			this.hour = hour;
			return;
		}

		/**
		 * 分を取得する
		 * @return 分
		 */
		public int getMinute() {
			return minute;
		}

		/**
		 * 分を設定する
		 * @param minute 分
		 */
		public void setMinute(int minute)
		{
			this.minute = minute;
			return;
		}
	}
}
