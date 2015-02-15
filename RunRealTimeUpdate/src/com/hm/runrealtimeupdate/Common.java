package com.hm.runrealtimeupdate;

/**
 * 共通定数
 * @author Hayato Matsumuro
 *
 */
class Common
{
	/**
	 * サービスの起動間隔
	 */
	static final int INT_SERVICE_INTERVAL = 180000;

	/**
	 * 選手情報取得のネットアクセス間隔
	 */
	static final int INT_SERVICE_NETGET_INTERVAL = 700;

	/**
	 * 最後の更新から自動停止までの更新確認回数( 7時間 )
	 */
	static final int INT_COUNT_AUTOSTOP_LASTUPDATE = 25200000 / INT_SERVICE_INTERVAL;

	/**
	 * 自動開始から停止までの更新確認回数( 24時間 )
	 */
	static final int INT_COUNT_REGULARSTOP = 86400000 / INT_SERVICE_INTERVAL;

	/**
	 * 手動更新の更新確認回数( 1回のみ )
	 */
	static final int INT_COUNT_MANUALSTOP = 1;

	/**
	 * リクエストコード 更新定期
	 */
	static final int INT_REQUESTCODE_UPDATEPERIODIC = 1;

	/**
	 * リクエストコード 更新予約
	 */
	static final int INT_REQUESTCODE_UPDATERESERVE = 2;

	/**
	 * NEWの表示時間( ms )
	 */
	static final long LONG_RESENT_TIME = 300000;

	/**
	 * 登録できる大会の最大数
	 */
	static final int INT_RACEINFO_NUM_MAX = 5;

	/**
	 * 登録できる選手の数
	 */
	static int INT_RUNNER_NUM_MAX = 20;

	/**
	 * バイブ 
	 *  [ON時間, OFF時間, ・・・]
	 */
	static long[] LONG_BIVRATION = {0, 100, 100, 100, 100, 100};

}
