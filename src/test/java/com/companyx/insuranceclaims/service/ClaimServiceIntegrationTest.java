package com.companyx.insuranceclaims.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimStatusHistory;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.companyx.insuranceclaims.repository.ClaimStatusHistoryRepository;



@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClaimServiceIntegrationTest {
	
	@Autowired
	private ClaimService service;
	
	@Autowired
	private ClaimStatusHistoryRepository historyRepository;
	
	
	@Test
	void createsInitialStatusHistoryWithClaim() {
		Claim claim = Claim.create(
  				"CLM-SERVICE-HISTORY-001",
  				"POL-SERVICE-HISTORY-001",
  				"Taylor Mendoza",
  				LocalDate.of(2026, 9, 18),
  				ClaimType.HOME,
  				new BigDecimal("3200.00"),
  				"Roof storm damage"				
				);
		
		Claim created = service.create(claim);
		
		List<ClaimStatusHistory> matchingHistory = historyRepository.findAll()
															.stream()
															.filter(history -> history.getClaim().getId().equals(created.getId()))
															.toList();
		
		assertEquals(1, matchingHistory.size());
		
		ClaimStatusHistory initialHistory = matchingHistory.get(0);
		
		assertAll(
				() -> assertEquals(ClaimStatus.SUBMITTED, initialHistory.getStatus()),
				() -> assertEquals(created.getCreatedAt(), initialHistory.getChangedAt())				
				);
	}
	
	
	
	
}
