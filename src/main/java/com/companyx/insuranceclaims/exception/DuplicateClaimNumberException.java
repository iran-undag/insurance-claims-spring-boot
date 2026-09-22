package com.companyx.insuranceclaims.exception;

public class DuplicateClaimNumberException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DuplicateClaimNumberException(String claimNumber) {
		super("Claim number already exists "+claimNumber);
	}

}
