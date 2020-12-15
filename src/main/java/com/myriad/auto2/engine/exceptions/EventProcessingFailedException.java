package com.myriad.auto2.engine.exceptions;

public class EventProcessingFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventProcessingFailedException(String message,Throwable throwable) {
		super(message,throwable);
	}
}
