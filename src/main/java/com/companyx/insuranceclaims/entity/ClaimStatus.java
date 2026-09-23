package com.companyx.insuranceclaims.entity;

public enum ClaimStatus {
	SUBMITTED,
	UNDER_REVIEW,
	APPROVED,
	REJECTED;
	
	public boolean canTransitionTo(ClaimStatus nextStatus) {
		return switch(this) {
			case SUBMITTED -> nextStatus == UNDER_REVIEW;
			case UNDER_REVIEW -> nextStatus == APPROVED || nextStatus == REJECTED;
			case APPROVED, REJECTED -> false;
		};
	}
}
