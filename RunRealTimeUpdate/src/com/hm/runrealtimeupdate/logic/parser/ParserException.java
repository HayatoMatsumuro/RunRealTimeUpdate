package com.hm.runrealtimeupdate.logic.parser;

@SuppressWarnings("serial")
public class ParserException extends Exception {
	
	private String message;
	public ParserException(String message){
		super(message);
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
