package com.companyx.insuranceclaims.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ClaimTest {
	
	@Test
	void newClaimStartsWithSubmittedStatus() {
		Claim claim = Claim.create(
				"CLM-10001",
				"POL-50001",
				"Alex Rivera",
				LocalDate.of(2026, 9, 1),
				ClaimType.AUTO,
				new BigDecimal("1250.75"),
				"Rear bumper damage"
				);
		
		assertAll(
					() -> assertNull(claim.getId()),
					() -> assertEquals("CLM-10001", claim.getClaimNumber()),
					() -> assertEquals("POL-50001", claim.getPolicyNumber()),
					() -> assertEquals("Alex Rivera", claim.getClaimantName()),
					() -> assertEquals(LocalDate.of(2026, 9, 1), claim.getIncidentDate()),
					() -> assertEquals(ClaimType.AUTO, claim.getClaimType()),
					() -> assertEquals(new BigDecimal("1250.75"), claim.getClaimedAmount()),
					() -> assertEquals("Rear bumper damage", claim.getDescription()),
					() -> assertEquals(ClaimStatus.SUBMITTED, claim.getStatus()),
					() -> assertNotNull(claim.getCreatedAt())				
				);
		
		
	}
}
