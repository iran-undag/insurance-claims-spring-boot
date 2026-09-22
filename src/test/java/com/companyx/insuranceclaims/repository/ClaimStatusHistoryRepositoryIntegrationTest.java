package com.companyx.insuranceclaims.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimStatusHistory;
import com.companyx.insuranceclaims.entity.ClaimType;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //tells Boot not to replace PostgreSql with an embedded database
class ClaimStatusHistoryRepositoryIntegrationTest {

	@Autowired
	private ClaimRepository claimRepository;

	@Autowired
	private ClaimStatusHistoryRepository historyRepository;

	@Test
	void savesAndRetrievesStatusHistoryFromPostgreSql() {
		Claim claim = Claim.create(
				"CLM-HISTORY-REPOSITORY-001",
				"POL-HISTORY-001",
				"Jamie Flores",
				LocalDate.of(2026, 9, 15),
				ClaimType.AUTO,
				new BigDecimal("950.00"),
				"Windshield damage");
		
		Claim savedClaim = claimRepository.saveAndFlush(claim);
		
		LocalDateTime changedAt = LocalDateTime.of(2026, 9, 21, 10, 30);
		ClaimStatusHistory history = ClaimStatusHistory.record(savedClaim, ClaimStatus.SUBMITTED, changedAt);
		ClaimStatusHistory savedHistory = historyRepository.saveAndFlush(history);
		
		ClaimStatusHistory found = historyRepository.findById(savedHistory.getId()).orElseThrow();

		assertAll(
				() -> assertNotNull(savedHistory.getId()),
				() -> assertEquals(savedClaim.getId(), found.getClaim().getId()),
				() -> assertEquals(ClaimStatus.SUBMITTED, found.getStatus()),
				() -> assertEquals(changedAt, found.getChangedAt()));
	}
	
	@Test
	void retrievesJoinedHistoryInChronologicalOrder() {
		Claim claim = Claim.create(
	  			"CLM-HISTORY-JOIN-001",
	  			"POL-HISTORY-JOIN-001",
	  			"Sam Villanueva",
	  			LocalDate.of(2026, 9, 16),
	  			ClaimType.HOME,
	  			new BigDecimal("1800.00"),
	  			"Ceiling water damage"
				);
		
		Claim savedClaim = claimRepository.saveAndFlush(claim);
		
		LocalDateTime earlier = LocalDateTime.of(2026, 9, 20, 9, 0);
		LocalDateTime later = LocalDateTime.of(2026, 9, 21, 14, 30);
		
		ClaimStatusHistory laterHistory = historyRepository.saveAndFlush(ClaimStatusHistory.record(savedClaim, ClaimStatus.SUBMITTED, later));
		ClaimStatusHistory earlierHistory = historyRepository.saveAndFlush(ClaimStatusHistory.record(savedClaim, ClaimStatus.SUBMITTED, earlier));
		
		List<ClaimStatusHistoryProjection> result = historyRepository.findByClaimId(savedClaim.getId());
		
		assertAll(
				() -> assertEquals(2, result.size()),
				() -> assertEquals(earlierHistory.getId(), result.get(0).getId()),
				() -> assertEquals(laterHistory.getId(), result.get(1).getId()),
	  			() -> assertEquals(savedClaim.getId(), result.get(0).getClaimId()),
	  			() -> assertEquals("CLM-HISTORY-JOIN-001", result.get(0).getClaimNumber()),
	  			() -> assertEquals(ClaimStatus.SUBMITTED, result.get(0).getStatus()),
	  			() -> assertEquals(earlier, result.get(0).getChangedAt())				
				);
		
		
	}
}
