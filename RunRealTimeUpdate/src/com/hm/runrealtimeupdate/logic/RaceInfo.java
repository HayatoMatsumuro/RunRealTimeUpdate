package com.hm.runrealtimeupdate.logic;

public class RaceInfo
{
	/**
	 * 速報状態 停止中
	 */
	public static final int INT_RACEUPDATE_OFF = 0;

	/**
	 * 速報状態 速報中
	 */
	public static final int INT_RACEUPDATE_ON = 1;

	/**
	 * 速報状態 停止中
	 */
	public static final int INT_RACEUPDATE_RESERVE = 2;

	/**
	 * 大会ID
	 */
	private String raceId;

	/**
	 * 大会名
	 */
	private String raceName;

	/**
	 * 大会開催日
	 */
	private String raceDate;

	/**
	 * 大会開催地
	 */
	private String raceLocation;

	/**
	 * 大会速報状態
	 */
	private int raceUpdate;

	/**
	 * 大会IDを取得する
	 * @return 大会ID
	 */
	public String getRaceId()
	{
		return raceId;
	}

	/**
	 * 大会IDを設定する
	 * @param raceId 大会ID
	 */
	public void setRaceId( String raceId )
	{
		this.raceId = raceId;
		return;
	}

	/**
	 * 大会名を取得する
	 * @return 大会名
	 */
	public String getRaceName()
	{
		return raceName;
	}

	/**
	 * 大会名を設定する
	 * @param raceName 大会名
	 */
	public void setRaceName( String raceName )
	{
		this.raceName = raceName;
		return;
	}

	/**
	 * 大会開催日を取得する
	 * @return 大会開催日
	 */
	public String getRaceDate()
	{
		return raceDate;
	}

	/**
	 * 大会開催日を設定する
	 * @param raceDate 大会開催日
	 */
	public void setRaceDate( String raceDate )
	{
		this.raceDate = raceDate;
		return;
	}

	/**
	 * 大会開催地を取得する
	 * @return 大会開催地
	 */
	public String getRaceLocation() {
		return raceLocation;
	}

	/**
	 * 大会開催地を設定する
	 * @param raceLocation 大会開催地
	 */
	public void setRaceLocation( String raceLocation )
	{
		this.raceLocation = raceLocation;
		return;
	}

	/**
	 * 大会速報状態を取得する
	 * @return 速報状態
	 */
	public int getRaceUpdate() {
		return raceUpdate;
	}

	/**
	 * 大会速報状態を設定する
	 * @param raceUpdate 速報状態
	 */
	public void setRaceUpdate( int raceUpdate )
	{
		this.raceUpdate = raceUpdate;
		return;
	}
}
