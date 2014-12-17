package com.hm.runrealtimeupdate;

/**
 * 共通定数
 * @author Hayato Matsumuro
 *
 */
public class Common
{
	/**
	 * サービスの起動間隔
	 */
	public static final int INT_SERVICE_INTERVAL = 120000;

	/**
	 * 最後の更新から自動停止までの更新確認回数
	 */
	public static final int INT_COUNT_AUTOSTOP_LASTUPDATE = 25200000 / INT_SERVICE_INTERVAL;
}
