package com.robert.category.exception;

public class ApplicationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5498704558658160670L;
	private final String errorMessage;
	private final String errorCode;
	private final boolean status;

	public ApplicationException(String errorMessage, String errorCode, boolean status) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.status = status;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public boolean isStatus() {
		return this.status;
	}

}
