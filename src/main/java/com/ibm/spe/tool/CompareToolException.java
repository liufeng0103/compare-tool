package com.ibm.spe.tool;

public class CompareToolException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompareToolException() {
		super();
	}

	public CompareToolException(String message) {
		super(message);
	}

	public CompareToolException(Throwable cause) {
		super(cause);
	}
	
	public CompareToolException(String message, Throwable cause) {
		super(message, cause);
	}

}
