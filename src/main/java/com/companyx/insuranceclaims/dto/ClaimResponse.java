package com.companyx.insuranceclaims.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;

import lombok.Getter;

@Getter
public class ClaimResponse {
	
	private final Long id;
	private final String claimNumber;
	private final String policyNumber;
	private final String claimantName;
	private final LocalDate incidentDate;
	private final ClaimType claimType;
	private final BigDecimal claimedAmount;
	private final ClaimStatus status;
	private final String description;
	private final LocalDateTime createdAt;
	private final String incidentLocation;
	
	private ClaimResponse(Claim claim) {
		
		this.id = claim.getId();
		this.claimNumber = claim.getClaimNumber();
		this.policyNumber = claim.getPolicyNumber();
		this.claimantName = claim.getClaimantName();
		this.incidentDate = claim.getIncidentDate();
		this.claimType = claim.getClaimType();
		this.claimedAmount = claim.getClaimedAmount();
		this.status = claim.getStatus();
		this.description = claim.getDescription();
		this.createdAt = claim.getCreatedAt();		
		this.incidentLocation = claim.getIncidentLocation();
	}
	
	public static ClaimResponse from(Claim claim) {
		return new ClaimResponse(claim);
	}
}
