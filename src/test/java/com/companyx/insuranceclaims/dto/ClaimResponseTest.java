package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;

public class ClaimResponseTest {
	
	@Test
	void mapsClaimToResponse() {
		Claim claim = Claim.create(
  				"CLM-DTO-013",
  				"POL-DTO-013",
  				"Elena Cruz",
  				LocalDate.of(2026, 9, 10),
  				ClaimType.HOME,
  				new BigDecimal("3500.00"),
  				"Storm damage to the roof"
				);
		
		ClaimResponse response = ClaimResponse.from(claim);
		
		assertAll(
				() -> assertNull(response.getId()),
				() -> assertEquals("CLM-DTO-013", response.getClaimNumber()),
				() -> assertEquals("POL-DTO-013", response.getPolicyNumber()),
  				() -> assertEquals("Elena Cruz", response.getClaimantName()),
  				() -> assertEquals(LocalDate.of(2026, 9, 10), response.getIncidentDate()),
  				() -> assertEquals(ClaimType.HOME, response.getClaimType()),
  				() -> assertEquals(new BigDecimal("3500.00"), response.getClaimedAmount()),
  				() -> assertEquals(ClaimStatus.SUBMITTED, response.getStatus()),
  				() -> assertEquals("Storm damage to the roof", response.getDescription()),
  				() -> assertEquals(claim.getCreatedAt(), response.getCreatedAt())				
				);	
	}
	
	@Test
	void includesIncidentLocationInResponse() {
		Claim claim = Claim.create(
	  			"CLM-DTO-LOCATION-002",
	  			"POL-DTO-LOCATION-002",
	  			"Paolo Reyes",
	  			LocalDate.of(2026, 9, 22),
	  			ClaimType.AUTO,
	  			new BigDecimal("1750.00"),
	  			"Side panel damage",
	  			"Makati City"				
				);
		
		ClaimResponse response = ClaimResponse.from(claim);
		
		assertEquals("Makati City", response.getIncidentLocation());
	}

}
