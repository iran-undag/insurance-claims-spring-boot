package com.companyx.insuranceclaims.exception;

public class ClaimNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ClaimNotFoundException(long id) {
		super("Claim not found with id: "+id);
	}

}
