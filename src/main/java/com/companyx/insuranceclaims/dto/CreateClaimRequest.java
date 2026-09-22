package com.companyx.insuranceclaims.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateClaimRequest {
	
	@NotBlank
	@Size(min = 5, max = 30)
	private final String claimNumber;
	
	@NotBlank
	@Size(min = 5, max = 30)
	private final String policyNumber;
	
	@NotBlank
	@Size(min = 2, max = 100)
	private final String claimantName;
	
	@NotNull
	@PastOrPresent
	private final LocalDate incidentDate;
	
	@NotNull
	private final ClaimType claimType;
	
	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private final BigDecimal claimedAmount;	
	
	@NotBlank
	@Size(max = 500)
	private final String description;
	
	
	@JsonCreator
	public CreateClaimRequest(
			@JsonProperty("claimNumber") String claimNumber,
			@JsonProperty("policyNumber") String policyNumber,
			@JsonProperty("claimantName") String claimantName,
			@JsonProperty("incidentDate") LocalDate incidentDate,
			@JsonProperty("claimType") ClaimType claimType,
			@JsonProperty("claimedAmount") BigDecimal claimedAmount,
			@JsonProperty("description") String description) {
		
		this.claimNumber = claimNumber;
		this.policyNumber = policyNumber;
		this.claimantName = claimantName;
		this.incidentDate = incidentDate;
		this.claimType = claimType;
		this.claimedAmount = claimedAmount;
		this.description = description;
		
	}
	
	//Jackson converts the HTTP request body (JSON) into CreateClaimRequest DTO via @JsonCreator constructor
	//this method converts the DTO into a Claim entity
	public Claim toClaim() {
		return Claim.create(
				claimNumber, 
				policyNumber, 
				claimantName, 
				incidentDate, 
				claimType, 
				claimedAmount, 
				description);
	}
}
