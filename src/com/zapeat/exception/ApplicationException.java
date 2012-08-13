package com.zapeat.exception;

@SuppressWarnings("serial")
public class ApplicationException extends Exception {
	
	public ApplicationException(Throwable ex) {
		super(ex);
	}
	
	public ApplicationException(String message) {
		super(message);
	}
	
}
