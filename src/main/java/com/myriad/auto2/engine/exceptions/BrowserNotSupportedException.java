package com.myriad.auto2.engine.exceptions;

public class BrowserNotSupportedException extends RuntimeException{

	public BrowserNotSupportedException() {
	super("The specified browser or browser version is not supported by AutoSQUARE");
	}
	
	public BrowserNotSupportedException(String message, Throwable throwable) {
		super(message,throwable);
	}
	
}
