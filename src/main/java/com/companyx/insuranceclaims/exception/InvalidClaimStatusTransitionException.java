package com.companyx.insuranceclaims.exception;

import com.companyx.insuranceclaims.entity.ClaimStatus;

public class InvalidClaimStatusTransitionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidClaimStatusTransitionException(ClaimStatus currentStatus, ClaimStatus requestedStatus) {
		super("Cannot transition claim status from "+currentStatus+" to "+requestedStatus);
	}

}
