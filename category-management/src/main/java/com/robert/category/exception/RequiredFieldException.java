package com.robert.category.exception;

public class RequiredFieldException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String errorMessage;
	private final String errorCode;
	private final boolean status;

	public RequiredFieldException(String errorMessage, String errorCode, boolean status) {
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
