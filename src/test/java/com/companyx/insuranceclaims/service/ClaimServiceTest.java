package com.companyx.insuranceclaims.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.companyx.insuranceclaims.dto.ClaimStatusHistoryResponse;
import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.companyx.insuranceclaims.repository.ClaimRepository;
import com.companyx.insuranceclaims.repository.ClaimStatusHistoryProjection;
import com.companyx.insuranceclaims.repository.ClaimStatusHistoryRepository;
import com.companyx.insuranceclaims.exception.DuplicateClaimNumberException;
import com.companyx.insuranceclaims.exception.ClaimNotFoundException;
import com.companyx.insuranceclaims.exception.ApiErrorCode;
import com.companyx.insuranceclaims.exception.InvalidPaginationException;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {
	
	@Mock
	private ClaimRepository repository;
	
	@Mock
	private ClaimStatusHistoryRepository historyRepository;
	
	@Mock
	private ClaimStatusHistoryProjection historyProjection;
	
	@InjectMocks
	private ClaimService service;
	
	@Test
	void createsClaim() {
		Claim claim = Claim.create(
                "CLM-SERVICE-001",
                "POL-SERVICE-001",
                "Jordan Reyes",
                LocalDate.of(2026, 9, 5),
                ClaimType.AUTO,
                new BigDecimal("1750.00"),
                "Front fender damage"
				);
		
		when(repository.save(claim)).thenReturn(claim);
		
		Claim result = service.create(claim);
		
		assertSame(claim, result);
		verify(repository).save(claim);	
		
		
	}
	
	@Test
	void rejectsDuplicateClaimNumberWithoutSaving() {
		Claim claim = Claim.create(
	              "CLM-SERVICE-002",
	              "POL-SERVICE-002",
	              "Casey Lim",
	              LocalDate.of(2026, 9, 6),
	              ClaimType.HOME,
	              new BigDecimal("4200.00"),
	              "Kitchen fire damage"
				);
		
		when(repository.existsByClaimNumber("CLM-SERVICE-002")).thenReturn(true);
		
		assertThrows(DuplicateClaimNumberException.class, () -> service.create(claim));
		
		verify(repository, never()).save(any(Claim.class));	
	}
	
	@Test
	void getsExistingClaimById() {
		Claim claim = Claim.create(
	              "CLM-SERVICE-003",
	              "POL-SERVICE-003",
	              "Riley Tan",
	              LocalDate.of(2026, 9, 7),
	              ClaimType.AUTO,
	              new BigDecimal("950.00"),
	              "Side mirror damage"
				);
		
		when(repository.findById(42L)).thenReturn(Optional.of(claim));
		
		Claim result = service.get(42L);
		
		assertSame(claim, result);
		verify(repository).findById(42L);
	}
	
	
	@Test
	void rejectsUnknownClaimId() {
		when(repository.findById(404L)).thenReturn(Optional.empty());
		
		ClaimNotFoundException exception = assertThrows(ClaimNotFoundException.class, () -> service.get(404L));
		
		assertTrue(exception.getMessage().contains("404"));
		verify(repository).findById(404L);
	}
	
	@Test
	void listRequestedZeroBasedPageWithTotals() {
		Claim claim = Claim.create(              
				"CLM-SERVICE-PAGE-001",
	             "POL-SERVICE-PAGE-001",
	             "Paged Claimant",
	             LocalDate.of(2026, 9, 10),
	             ClaimType.AUTO,
	             new BigDecimal("750.00"),
	             "Paginated service claim"
				);
		
		List<Claim> content =  List.of(claim);
		Pageable pageable = PageRequest.of(1, 2, Sort.by("id").ascending());
		
		Page<Claim> repositoryPage = new PageImpl<>(content, pageable, 5L);
		when(repository.findAll(pageable)).thenReturn(repositoryPage);
		
		ClaimPage result = service.list(1,2);
		
		assertEquals(content, result.getContent());
		assertEquals(1, result.getPage());
		assertEquals(2, result.getSize());
		assertEquals(5L, result.getTotalElements());
		assertEquals(3, result.getTotalPages());
		
		verify(repository).findAll(pageable);				
	}
	
	@Test
	void rejectsNegativePage() {
		InvalidPaginationException exception = assertThrows(InvalidPaginationException.class, () -> service.list(-1, 20));
		
		assertEquals(ApiErrorCode.INVALID_PAGE, exception.getCode());
		assertEquals("page must be zero or greater", exception.getMessage());
		
		verifyNoInteractions(repository);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 101})
	void rejectsSizeOutsideAllowedRange(int size) {
		InvalidPaginationException exception = assertThrows(InvalidPaginationException.class, () -> service.list(0, size));
		
		assertEquals(ApiErrorCode.INVALID_PAGE_SIZE, exception.getCode());
		assertEquals("size must be between 1 and 100", exception.getMessage());
		
		verifyNoInteractions(repository);
	}
	
	@Test
	void returnsStatusHistoryForExistingClaim() {
		LocalDateTime changedAt = LocalDateTime.of(2026, 9, 21, 12, 30);
		
		when(repository.existsById(42L)).thenReturn(true);
		when(historyProjection.getId()).thenReturn(7L);
		when(historyProjection.getClaimId()).thenReturn(42L);
	  	when(historyProjection.getClaimNumber()).thenReturn("CLM-SERVICE-HISTORY-042");
	  	when(historyProjection.getStatus()).thenReturn(ClaimStatus.SUBMITTED);
	  	when(historyProjection.getChangedAt()).thenReturn(changedAt);
	  	when(historyRepository.findByClaimId(42L)).thenReturn(List.of(historyProjection));
	  	
	  	List<ClaimStatusHistoryResponse> result = service.getStatusHistory(42L);
	  	
	  	assertAll(
	  			() -> assertEquals(1, result.size()),
	  			() -> assertEquals(7L, result.get(0).getId()),
	  			() -> assertEquals(42L, result.get(0).getClaimId()),
	  			() -> assertEquals("CLM-SERVICE-HISTORY-042",result.get(0).getClaimNumber()),
	  			() -> assertEquals(ClaimStatus.SUBMITTED,result.get(0).getStatus()),
	  			() -> assertEquals(changedAt,result.get(0).getChangedAt())  			
	  			);
	  }
	
	@Test
	void rejectsStatusHistoryForUnknownClaim() {
		when(repository.existsById(404L)).thenReturn(false);
		
		ClaimNotFoundException exception = assertThrows(ClaimNotFoundException.class, () -> service.getStatusHistory(404L));
		
		assertTrue(exception.getMessage().contains("404"));
		verifyNoInteractions(historyRepository);
	}
	
}
