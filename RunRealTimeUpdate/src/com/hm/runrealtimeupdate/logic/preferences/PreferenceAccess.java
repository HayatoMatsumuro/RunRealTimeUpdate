package com.hm.runrealtimeupdate.logic.preferences;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceAccess {
	
	/**
	 * 文字列データをプリファレンスに保存する
	 * @param context　コンテキスト
	 * @param saveData 保存データ
	 */
	public static void saveStringData( Context context, Map<String, String> saveData ) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor editor = pref.edit();
		
		// 全部のキーと値を保持する
		for(String key:saveData.keySet()){
			editor.putString(key, saveData.get( key ) );
		}
		
		editor.commit();
		
		return;
	}
	
	/**
	 * 数値データをプリファレンスに保存する
	 * @param context コンテキスト
	 * @param saveData 保存データ
	 */
	public static void saveIntData( Context context, Map<String, Integer> saveData ){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor editor = pref.edit();
		
		// 全部のキーと値を保持する
		for(String key:saveData.keySet()){
			editor.putInt(key, saveData.get( key ) );
		}
		
		editor.commit();
		
		return;
	}
	
	/**
	 * プリファレンスから文字列データを取得する
	 * @param context コンテキスト
	 * @param key キー
	 * @return　データ
	 */
	public static String loadStringData( Context context, String key ) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		
		return pref.getString(key, null);
	}
	
	/**
	 * プリファレンスから数値データを取得する
	 * @param context コンテキスト
	 * @param key　キー
	 * @return データ
	 */
	public static int loadIntData( Context context, String key ){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		
		return pref.getInt( key, Integer.MAX_VALUE );
	}
	
	/**
	 * プリファレンスからデータを削除する
	 * @param context コンテキスト
	 * @param key　キー
	 */
	public static void deleteData( Context context, String key ) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor editor = pref.edit();
		editor.remove( key );
		editor.commit();
		return;
	}
	
	/**
	 * プリファレンスからキーリストのデータを削除する
	 * @param context　コンテキスト
	 * @param keyList キーリスト
	 */
	public static void deleteDatas( Context context, List<String> keyList ) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor editor = pref.edit();
		for( String key:keyList ){
			editor.remove( key );
		}
		editor.commit();
		return;
	}
	
	/**
	 * プリファレンスのデータを消去する
	 * @param context　コンテキスト
	 */
	public static void deleteAllData( Context context ) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
        editor.commit();
		return;
	}
}
