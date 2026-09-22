package com.companyx.insuranceclaims.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.companyx.insuranceclaims.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ClaimNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleClaimNotFound(ClaimNotFoundException exception){
		
		ErrorResponse response = new ErrorResponse(
										HttpStatus.NOT_FOUND.value(),
										ApiErrorCode.CLAIM_NOT_FOUND,
										exception.getMessage());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(DuplicateClaimNumberException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateClaimNumber(DuplicateClaimNumberException exception){
		
		ErrorResponse response = new ErrorResponse(
									HttpStatus.CONFLICT.value(),
									ApiErrorCode.DUPLICATE_CLAIM_NUMBER,
									exception.getMessage()
									);
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception){
		
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		
		for(FieldError fieldError: exception.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		
		ErrorResponse response = new ErrorResponse(
									HttpStatus.BAD_REQUEST.value(),
									ApiErrorCode.VALIDATION_FAILED,
									"Validation failed",
									fieldErrors);
		
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(InvalidPaginationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPagination(InvalidPaginationException exception){
		
		ErrorResponse response = new ErrorResponse(
									HttpStatus.BAD_REQUEST.value(),
									exception.getCode(),
									exception.getMessage()
									);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		
	}
	
	  @ExceptionHandler(DataIntegrityViolationException.class)
	  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
	  		DataIntegrityViolationException exception) {
		  
		//notice the handler does not use exception.getMessage(). 
		//that prevents PostgreSQL messages, SQL text and constraint names from entering the API response
	  	ErrorResponse response = new ErrorResponse(
	  			HttpStatus.CONFLICT.value(),
	  			ApiErrorCode.DUPLICATE_CLAIM_NUMBER,
	  			"Claim number already exists");

	  	return ResponseEntity
	  			.status(HttpStatus.CONFLICT)
	  			.body(response);
	  }
}
