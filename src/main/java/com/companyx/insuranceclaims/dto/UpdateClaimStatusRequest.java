package com.companyx.insuranceclaims.dto;

import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateClaimStatusRequest {
	
	@NotNull
	private final ClaimStatus status;
	
	@JsonCreator
	public UpdateClaimStatusRequest(@JsonProperty("status") ClaimStatus status) {
		this.status = status;
	}
}
