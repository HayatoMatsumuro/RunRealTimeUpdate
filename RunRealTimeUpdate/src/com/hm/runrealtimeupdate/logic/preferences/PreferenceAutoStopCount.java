package com.hm.runrealtimeupdate.logic.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * 自動停止カウント プリファレンス
 * @author Hayato Matsumuro
 *
 */
public class PreferenceAutoStopCount
{
	/**
	 * キー　自動停止カウント
	 */
	private static final String KEY_AUTOSTOPCOUNT = "autostopcount";

	/**
	 * 自動停止カウントをプリファレンスに保存する
	 * @param context コンテキスト
	 * @param autoStopCount 自動停止カウント
	 */
	public static void saveAutoStopCount( Context context, int autoStopCount )
	{
		Map< String, Integer > map = new HashMap< String, Integer>();

		map.put( KEY_AUTOSTOPCOUNT, autoStopCount );

		PreferenceAccess.saveIntData( context, map );

		return;
	}

	/**
	 * 自動停止カウントをプリファレンスからロードする
	 * @param context コンテキスト
	 * @return 自動停止カウント
	 */
	public static int loadUAutoStopCount( Context context )
	{
		return PreferenceAccess.loadIntData( context, KEY_AUTOSTOPCOUNT );
	}

	/**
	 * 自動停止カウントをプリファレンスから削除する
	 * @param context コンテキスト
	 */
	public static void deleteAutoStopCount( Context context )
	{
		PreferenceAccess.deleteData( context, KEY_AUTOSTOPCOUNT );

		return;
	}
}
