package com.hm.runrealtimeupdate.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * 選手情報
 * @author Hayato Matsumuro
 *
 */
public class RunnerInfo
{
	/**
	 * 名前
	 */
	public String name;

	/**
	 * ゼッケン番号
	 */
	public String number;

	/**
	 * 部門
	 */
	public String section;

	/**
	 * タイムリスト
	 */
	public List<TimeInfo> timeInfoList;

	/**
	 * コンストラクタ
	 */
	public RunnerInfo()
	{
		timeInfoList = new ArrayList<TimeInfo>();
	}

	/**
	 * タイム情報
	 * @author Hayato Matsumuro
	 *
	 */
	public class TimeInfo
	{
		/**
		 * 計測ポイント
		 */
		public String point;

		/**
		 * スプリット
		 */
		public String split;

		/**
		 * ラップ
		 */
		public String lap;

		/**
		 * 通過時刻
		 */
		public String currentTime;
	}
}
