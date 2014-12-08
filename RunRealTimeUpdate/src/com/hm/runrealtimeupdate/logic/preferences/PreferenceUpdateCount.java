package com.hm.runrealtimeupdate.logic.preferences;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class PreferenceUpdateCount {
	
	private static final String KEY_UPDATECOUNT = "updatecount";
	
	/**
	 * 更新カウントを保存する
	 * @param context コンテキスト
	 * @param updateCount 保存データ
	 */
	public static void saveUpdateCount( Context context, int updateCount ){
		
		Map< String, Integer > map = new HashMap< String, Integer>();
		
		map.put( KEY_UPDATECOUNT, updateCount );
		
		PreferenceAccess.saveIntData( context, map );
		
		return;
	}
	
	/**
	 * 更新カウントを取得する
	 * @param context コンテキスト
	 * @return 更新カウント ( データがないならば、Integer.MAX_VALUE となる )
	 */
	public static int loadUpdateCount( Context context ){
		
		return PreferenceAccess.loadIntData( context, KEY_UPDATECOUNT );
	}
	
	/**
	 * 更新カウントを削除する
	 * @param context コンテキスト
	 */
	public static void deleteUpdateCount( Context context ){
		
		PreferenceAccess.deleteData( context, KEY_UPDATECOUNT );
		
		return;
	}
}
