package com.companyx.insuranceclaims.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //tells Boot not to replace PostgreSql with an embedded database, we want real PostgreSQL behavior

//DataJpaTest wraps each test in one transaction. Propagation.NOT_SUPPORTED allows us to simulate separate requests/create several independent transactions manually
@Transactional(propagation = Propagation.NOT_SUPPORTED) 
public class ClaimOptimisticLockingIntegrationTest {
	
  	@Autowired
  	private ClaimRepository claimRepository;

  	@Autowired
  	private PlatformTransactionManager transactionManager;

  	@Test
  	void rejectsUpdateUsingStaleVersion() {
  		
  		//The database requires claim_number to be unique. A random suffix prevents collisions with earlier or concurrent test runs
  		String claimNumber = "CLM-LOCK-"+ UUID.randomUUID().toString().substring(0, 8);
  		
  		//TransactionTemplate lets each callback execute in its own transaction
  		//Create the Claim record in a separate transaction
  		TransactionTemplate transactions = new TransactionTemplate(transactionManager);
  		Long claimId = transactions.execute(status -> {
  			Claim claim = Claim.create(
  					claimNumber,
  					"POL-LOCK-001",
  					"Andrea Reyes",
  					LocalDate.of(2026, 9, 22),
  					ClaimType.AUTO,
  					new BigDecimal("1900.00"),
  					"Optimistic locking test");

  			return claimRepository.saveAndFlush(claim).getId();
  		});

  		try {
  			//Read the Claim record into firstCopy and staleCopy in separate transactions
  			//firstCopy and staleCopy both status==SUBMITTED, version==0
  			Claim firstCopy = transactions.execute(status -> claimRepository.findById(claimId).orElseThrow());
  			Claim staleCopy = transactions.execute(status -> claimRepository.findById(claimId).orElseThrow());
  			
  			//They do not need to execute simultaneously. 
  			//Optimistic locking detects stale versions, so this sequential setup is deterministic and tests the same conflict safely.
  			//At this point, firstCopy and staleCopy still has version = 0, since they are not persisted yet
  			firstCopy.transitionTo(ClaimStatus.UNDER_REVIEW);
  			staleCopy.transitionTo(ClaimStatus.UNDER_REVIEW);
  			
  			//persist first copy. Hibernate handles versioning, it auto-adds version incrementing and checking to the resulting SQL statement 
  			transactions.executeWithoutResult(status -> claimRepository.saveAndFlush(firstCopy));
  			
  			//attempting the stale update. 
  			//staleCopy still has version 0 but database row has version = 1 due to previous transaction of firstCopy save()
  			assertThrows(ObjectOptimisticLockingFailureException.class,	
  					() -> transactions.executeWithoutResult(status -> claimRepository.saveAndFlush(staleCopy)));

  			//This retrieves the final database state using another transaction.
  			Claim persisted = transactions.execute(status -> claimRepository.findById(claimId).orElseThrow());

  			assertAll(
  					() -> assertEquals(ClaimStatus.UNDER_REVIEW,persisted.getStatus()),
  					() -> assertEquals(1L, persisted.getVersion()));
  		} finally {
  			
  			//cleanup
  			transactions.executeWithoutResult(status ->	claimRepository.deleteById(claimId));
  		}
  	}

	
	
}
