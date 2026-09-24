package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

//The test demonstrates tolerance of the newly added property
@JsonTest
class ClaimResponseBackwardCompatibilityTest {

	private final ObjectMapper objectMapper;

	@Autowired
	ClaimResponseBackwardCompatibilityTest(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private record LegacyClaimResponse(
			Long id,
			String claimNumber,
			String policyNumber,
			String claimantName,
			LocalDate incidentDate,
			ClaimType claimType,
			BigDecimal claimedAmount,
			ClaimStatus status,
			String description,
			LocalDateTime createdAt) {
	}
	
	@Test
	void legacyClientIgnoresIncidentLocation() throws Exception {
		Claim claim = Claim.create(
				"CLM-COMPATIBILITY-001",
				"POL-COMPATIBILITY-001",
				"Legacy Client",
				LocalDate.of(2026, 9, 22),
				ClaimType.AUTO,
				new BigDecimal("1800.00"),
				"Compatibility test",
				"Cebu City");

		String json = objectMapper.writeValueAsString(ClaimResponse.from(claim));

		assertEquals("Cebu City", objectMapper.readTree(json).get("incidentLocation").asText());

		LegacyClaimResponse legacyResponse = objectMapper.readValue(json, LegacyClaimResponse.class);

		assertAll(
					() -> assertEquals("CLM-COMPATIBILITY-001",	legacyResponse.claimNumber()),
					() -> assertEquals(ClaimStatus.SUBMITTED, legacyResponse.status()),
					() -> assertEquals("Compatibility test", legacyResponse.description())
				);
	}


}
