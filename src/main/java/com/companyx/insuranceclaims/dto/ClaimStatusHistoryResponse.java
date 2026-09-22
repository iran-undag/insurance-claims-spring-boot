package com.companyx.insuranceclaims.dto;

import java.time.LocalDateTime;

import com.companyx.insuranceclaims.entity.ClaimStatus;

import lombok.Getter;

@Getter
public class ClaimStatusHistoryResponse {
	
	private final Long id;
	private final Long claimId;
	private final String claimNumber;
	private final ClaimStatus status;
	private final LocalDateTime changedAt;
	
	public ClaimStatusHistoryResponse(
			Long id,
			Long claimId,
			String claimNumber,
			ClaimStatus status,
			LocalDateTime changedAt
			) {
		this.id = id;
		this.claimId = claimId;
		this.claimNumber = claimNumber;
		this.status = status;
		this.changedAt = changedAt;
		
	}
}
