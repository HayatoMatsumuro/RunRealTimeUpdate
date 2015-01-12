package com.hm.runrealtimeupdate.logic;

/**
 * 大会情報
 * @author Hayato Matsumuro
 *
 */
public class RaceInfo
{
	/**
	 * 速報状態 停止中
	 */
	public static final int INT_UPDATESTS_OFF = 0;

	/**
	 * 速報状態 速報中
	 */
	public static final int INT_UPDATESTS_ON = 1;

	/**
	 * 速報状態 停止中
	 */
	public static final int INT_UPDATESTS_RESERVE = 2;

	/**
	 * 大会ID
	 */
	public String id;

	/**
	 * 大会名
	 */
	public String name;

	/**
	 * 大会開催日
	 */
	public String date;

	/**
	 * 大会開催地
	 */
	public String location;

	/**
	 * 大会速報状態
	 */
	public int updateSts;
}
