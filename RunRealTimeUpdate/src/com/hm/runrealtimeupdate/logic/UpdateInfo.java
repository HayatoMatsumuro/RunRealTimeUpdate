package com.hm.runrealtimeupdate.logic;

/**
 * 速報情報
 * @author Hayato Matsumuro
 *
 */
public class UpdateInfo
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
	 * 地点
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
	 * 通過時間
	 */
	public String currentTime;

	/**
	 * 最近フラグ
	 */
	public boolean recentFlg;

	/**
	 * スプリットをlong型で取得
	 * @return スプリット
	 */
	public long getSplitLong()
	{
		try
		{
			long time = getLongTime( split );
			return time;
		}
		catch( UpdateInfoException e )
		{
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 秒単位の時間を取得する
	 * @param timeStr　HH:MM:SS 形式
	 * @return 時間
	 * @throws PassPointException 時間取得失敗
	 */
	private long getLongTime( String timeStr ) throws UpdateInfoException
	{
		try
		{
			String split[] = timeStr.split( ":" );
			int h = Integer.parseInt( split[0] );
			int m = Integer.parseInt( split[1] );
			int s = Integer.parseInt( split[2] );

			return h * 3600 + m * 60 + s;
		}
		catch( Exception e )
		{
			throw new UpdateInfoException();
		}
	}

	@SuppressWarnings("serial")
	private class UpdateInfoException extends Exception
	{
	}
}
