package com.companyx.insuranceclaims.exception;

import lombok.Getter;

@Getter
public class InvalidPaginationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final ApiErrorCode code;
	
	public InvalidPaginationException(ApiErrorCode code, String message) {
		super(message);
		this.code = code;
	}

}
