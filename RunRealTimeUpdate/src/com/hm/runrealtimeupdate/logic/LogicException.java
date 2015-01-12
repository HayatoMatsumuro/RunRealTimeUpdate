package com.hm.runrealtimeupdate.logic;

@SuppressWarnings("serial")
public class LogicException extends Exception
{
	/**
	 * メッセージ
	 */
	public String message;

	/**
	 * コンストラクタ
	 * @param message メッセージ
	 */
	public LogicException( String message )
	{
		super( message );
		this.message = message;
		return;
	}
}
