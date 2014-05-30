package com.hm.runrealtimeupdate.logic;

@SuppressWarnings("serial")
public class LogicException extends Exception {

	private String message;
	public LogicException(String message){
		super(message);
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
