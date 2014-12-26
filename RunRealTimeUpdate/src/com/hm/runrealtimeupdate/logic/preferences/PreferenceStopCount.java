package com.hm.runrealtimeupdate.logic.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * 停止カウント プリファレンス
 * @author Hayato Matsumuro
 *
 */
public class PreferenceStopCount
{
	/**
	 * キー　自動停止カウント
	 */
	public static final String KEY_AUTOSTOPCOUNT = "autostopcount";

	/**
	 * キー 定期停止カウント
	 */
	public static final String KEY_REGULARSTOPCOUNT = "regularstopcount";
	
	/**
	 * 停止カウントをプリファレンスに保存する
	 * @param context コンテキスト
	 * @param stopCount 自動停止カウント
	 */
	public static void saveStopCount( Context context, String key, int stopCount )
	{
		Map< String, Integer > map = new HashMap< String, Integer>();

		map.put( key, stopCount );

		PreferenceAccess.saveIntData( context, map );

		return;
	}

	/**
	 * 停止カウントをプリファレンスからロードする
	 * @param context コンテキスト
	 * @param key キー
	 * @return 自動停止カウント
	 */
	public static int loadAutoStopCount( Context context, String key )
	{
		return PreferenceAccess.loadIntData( context, key );
	}

	/**
	 * 停止カウントをプリファレンスから削除する
	 * @param context コンテキスト
	 * @param key キー
	 */
	public static void deleteStopCount( Context context, String key )
	{
		PreferenceAccess.deleteData( context, key );

		return;
	}
}
