package com.hm.runrealtimeupdate.logic.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserRunnerInfo
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
	public List<TimeInfo> timeList;

	/**
	 * コンストラクタ
	 */
	public ParserRunnerInfo()
	{
		this.name = null;
		this.number = null;
		this.section = null;
		this.timeList = new ArrayList<TimeInfo>();
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

