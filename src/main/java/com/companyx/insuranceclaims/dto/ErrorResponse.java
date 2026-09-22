package com.companyx.insuranceclaims.dto;

import java.util.Collections;
import java.util.Map;

import com.companyx.insuranceclaims.exception.ApiErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	
	private final int status;
	private final ApiErrorCode code;
	private final String message;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final Map<String, String> fieldErrors;
	
	public ErrorResponse(
			int status,
			ApiErrorCode code,
			String message) {
		this(status, code, message, Collections.emptyMap());
	}
}
