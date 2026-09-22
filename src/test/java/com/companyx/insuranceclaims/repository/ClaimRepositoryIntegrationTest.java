package com.companyx.insuranceclaims.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimType;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //tells Boot not to replace PostgreSql with an embedded database
public class ClaimRepositoryIntegrationTest {
	
	@Autowired
	private ClaimRepository claimRepository;
	
	@Test
	void savesAndRetrievesClaimFromPostgreSql() {
		Claim claim = Claim.create(
                "CLM-REPOSITORY-001",
                "POL-50001",
                "Alex Rivera",
                LocalDate.of(2026, 9, 1),
                ClaimType.AUTO,
                new BigDecimal("1250.75"),
                "Rear bumper damage"
				);
		
		Claim saved = claimRepository.saveAndFlush(claim);
		Optional<Claim> found = claimRepository.findById(saved.getId());
		
		assertAll(
				() -> assertNotNull(saved.getId()),
				() -> assertTrue(found.isPresent()),
				() -> assertEquals("CLM-REPOSITORY-001", found.orElseThrow().getClaimNumber())			
				);
		
	}
	
	@Test
	void detectsWhetherClaimNumberExists() {
		Claim claim = Claim.create(
	              "CLM-REPOSITORY-EXISTS-001",
	              "POL-REPOSITORY-002",
	              "Morgan Cruz",
	              LocalDate.of(2026, 9, 2),
	              ClaimType.HOME,
	              new BigDecimal("2100.00"),
	              "Kitchen water damage"

				);
		
		claimRepository.saveAndFlush(claim);
		
		assertAll(
				() -> assertTrue(claimRepository.existsByClaimNumber("CLM-REPOSITORY-EXISTS-001")),
				() -> assertFalse(claimRepository.existsByClaimNumber("CLM-REPOSITORY-MISSING"))			
				);
	}
}
