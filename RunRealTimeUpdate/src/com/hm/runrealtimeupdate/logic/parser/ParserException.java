package com.hm.runrealtimeupdate.logic.parser;

@SuppressWarnings("serial")
public class ParserException extends Exception
{
	/**
	 * メッセージ
	 */
	private String message;

	/**
	 * コンストラクタ
	 * @param message メッセージ
	 */
	public ParserException(String message){
		super(message);
		this.message = message;
		return;
	}

	public String getMessage(){
		return message;
	}
}
