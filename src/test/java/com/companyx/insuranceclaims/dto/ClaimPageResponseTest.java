package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.companyx.insuranceclaims.service.ClaimPage;

public class ClaimPageResponseTest {
	
	@Test
	void mapsClaimPageContentAndMetadata() {
  		Claim first = Claim.create(
  				"CLM-PAGE-001",
  				"POL-PAGE-001",
  				"First Claimant",
  				LocalDate.of(2026, 9, 1),
  				ClaimType.AUTO,
  				new BigDecimal("100.00"),
  				"First paginated claim");

  		Claim second = Claim.create(
  				"CLM-PAGE-002",
  				"POL-PAGE-002",
  				"Second Claimant",
  				LocalDate.of(2026, 9, 2),
  				ClaimType.HOME,
  				new BigDecimal("200.00"),
  				"Second paginated claim");
  		
  		ClaimPage claimPage = new ClaimPage(List.of(first, second), 1, 2, 5L, 3);
  		
  		ClaimPageResponse response = ClaimPageResponse.from(claimPage);
  		
  		assertAll(
  				() -> assertEquals(2, response.getContent().size()),
  				() -> assertEquals("CLM-PAGE-001", response.getContent().get(0).getClaimNumber()),
  				() -> assertEquals("CLM-PAGE-002", response.getContent().get(1).getClaimNumber()),
  				() -> assertEquals(1, response.getPage()),
  				() -> assertEquals(2, response.getSize()),
  				() -> assertEquals(5L, response.getTotalElements()),
  				() -> assertEquals(3, response.getTotalPages())  				
  				);
	}

}
