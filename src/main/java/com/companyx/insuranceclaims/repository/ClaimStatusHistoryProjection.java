package com.companyx.insuranceclaims.repository;

import java.time.LocalDateTime;

import com.companyx.insuranceclaims.entity.ClaimStatus;

public interface ClaimStatusHistoryProjection {
	Long getId();
	Long getClaimId();
	String getClaimNumber();
	ClaimStatus getStatus();
	LocalDateTime getChangedAt();
}
