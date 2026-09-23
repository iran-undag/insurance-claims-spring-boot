package com.companyx.insuranceclaims.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.companyx.insuranceclaims.exception.InvalidClaimStatusTransitionException;

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
	
	@Test
	void transitionsSubmittedClaimToUnderReview() {
		Claim claim = Claim.create(
	  			"CLM-TRANSITION-001",
	  			"POL-TRANSITION-001",
	  			"Jordan Ramos",
	  			LocalDate.of(2026, 9, 20),
	  			ClaimType.AUTO,
	  			new BigDecimal("1600.00"),
	  			"Vehicle collision damage"
	  			);
		
		claim.transitionTo(ClaimStatus.UNDER_REVIEW);
		
		assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());
	}
	
	@Test
	void rejectsTransitionToCurrentStatus() {
	  	Claim claim = Claim.create(
	  			"CLM-TRANSITION-002",
	  			"POL-TRANSITION-002",
	  			"Casey Torres",
	  			LocalDate.of(2026, 9, 20),
	  			ClaimType.HOME,
	  			new BigDecimal("2100.00"),
	  			"Storm damage"
	  			);
	  	
	  	InvalidClaimStatusTransitionException exception = assertThrows(InvalidClaimStatusTransitionException.class,() -> claim.transitionTo(ClaimStatus.SUBMITTED));
	  	
	  	assertAll(
	  			() -> assertEquals("Cannot transition claim status from SUBMITTED to SUBMITTED", exception.getMessage()),
	  			() -> assertEquals(ClaimStatus.SUBMITTED, claim.getStatus())	  			
	  			);
	}
	
	
	@Test
	void rejectsTransitionFromSubmittedDirectlyToApproved() {
	  	Claim claim = Claim.create(
	  			"CLM-TRANSITION-003",
	  			"POL-TRANSITION-003",
	  			"Morgan Reyes",
	  			LocalDate.of(2026, 9, 20),
	  			ClaimType.TRAVEL,
	  			new BigDecimal("2800.00"),
	  			"Cancelled international trip");
	  			
	  	assertThrows(InvalidClaimStatusTransitionException.class, () -> claim.transitionTo(ClaimStatus.APPROVED));
	  	
	  	assertEquals(ClaimStatus.SUBMITTED, claim.getStatus());
	}
	
	@Test
	void transitionsUnderReviewClaimToApproved() {
	  	Claim claim = Claim.create(
	  			"CLM-TRANSITION-004",
	  			"POL-TRANSITION-004",
	  			"Alex Mendoza",
	  			LocalDate.of(2026, 9, 21),
	  			ClaimType.HOME,
	  			new BigDecimal("3500.00"),
	  			"Flood damage");
	  	
	  	claim.transitionTo(ClaimStatus.UNDER_REVIEW);
	  	claim.transitionTo(ClaimStatus.APPROVED);
	  	
	  	assertEquals(ClaimStatus.APPROVED, claim.getStatus());
	  	
	}
	
	@Test
	void transitionsUnderReviewClaimToRejected() {
	  	Claim claim = Claim.create(
	  			"CLM-TRANSITION-005",
	  			"POL-TRANSITION-005",
	  			"Jamie Cruz",
	  			LocalDate.of(2026, 9, 21),
	  			ClaimType.AUTO,
	  			new BigDecimal("1900.00"),
	  			"Vehicle flood damage");

	  	claim.transitionTo(ClaimStatus.UNDER_REVIEW);
	  	claim.transitionTo(ClaimStatus.REJECTED);
	  	
	  	assertEquals(ClaimStatus.REJECTED, claim.getStatus());
	  	
	  	
	}
	
}
