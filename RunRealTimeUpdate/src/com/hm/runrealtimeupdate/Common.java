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
	public static final int INT_SERVICE_INTERVAL = 180000;

	/**
	 * 選手情報取得のネットアクセス間隔
	 */
	public static final int INT_SERVICE_NETGET_INTERVAL = 700;

	/**
	 * 最後の更新から自動停止までの更新確認回数( 7時間 )
	 */
	public static final int INT_COUNT_AUTOSTOP_LASTUPDATE = 25200000 / INT_SERVICE_INTERVAL;

	/**
	 * 自動開始から停止までの更新確認回数( 24時間 )
	 */
	public static final int INT_COUNT_REGULARSTOP = 86400000 / INT_SERVICE_INTERVAL;

	/**
	 * 手動更新の更新確認回数( 1回のみ )
	 */
	public static final int INT_COUNT_MANUALSTOP = 1;

	/**
	 * リクエストコード 更新定期
	 */
	public static final int INT_REQUESTCODE_UPDATEPERIODIC = 1;

	/**
	 * リクエストコード 更新予約
	 */
	public static final int INT_REQUESTCODE_UPDATERESERVE = 2;

}
