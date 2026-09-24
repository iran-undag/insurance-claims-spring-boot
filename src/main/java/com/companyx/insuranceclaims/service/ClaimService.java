package com.companyx.insuranceclaims.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.companyx.insuranceclaims.dto.ClaimStatusHistoryResponse;
import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimStatusHistory;
import com.companyx.insuranceclaims.exception.DuplicateClaimNumberException;
import com.companyx.insuranceclaims.exception.ClaimNotFoundException;
import com.companyx.insuranceclaims.exception.ApiErrorCode;
import com.companyx.insuranceclaims.exception.InvalidPaginationException;
import com.companyx.insuranceclaims.repository.ClaimRepository;
import com.companyx.insuranceclaims.repository.ClaimStatusHistoryProjection;
import com.companyx.insuranceclaims.repository.ClaimStatusHistoryRepository;


@Service
public class ClaimService {
	
	// Spring injects the sole constructor without @Autowired.
	// Constructor injection makes the required dependency explicit and allows it to remain final.
	// Putting @Autowired directly on the field would instead use field injection and we need to remove final
	private final ClaimRepository repository;
	private final ClaimStatusHistoryRepository historyRepository;
	
	// Lombok @RequiredArgsConstructor could generate the constructor for the final repository fields.
	// We write it explicitly here to make constructor injection easier to see and understand.
	public ClaimService(ClaimRepository repository, ClaimStatusHistoryRepository historyRepository) {
		this.repository = repository;
		this.historyRepository = historyRepository;
	}
	
	@Transactional
	public Claim create(Claim claim) {
		if(repository.existsByClaimNumber(claim.getClaimNumber())) {
			throw new DuplicateClaimNumberException(claim.getClaimNumber());
		}	
		
		Claim savedClaim = repository.save(claim);
		
		ClaimStatusHistory initialHistory = ClaimStatusHistory.record(savedClaim, savedClaim.getStatus(), savedClaim.getCreatedAt());		
		historyRepository.save(initialHistory);
		
		return savedClaim;
	}
	
	@Transactional(readOnly = true)
	public Claim get(long id) {
		return repository.findById(id).orElseThrow(() -> new ClaimNotFoundException(id));
	}
	
	@Transactional(readOnly = true)
	public ClaimPage list(int page, int size) {
		if(page < 0) {
			throw new InvalidPaginationException(ApiErrorCode.INVALID_PAGE, "page must be zero or greater");
		}
		
		if(size < 1 || size > 100) {
			throw new InvalidPaginationException(ApiErrorCode.INVALID_PAGE_SIZE, "size must be between 1 and 100");
		}
		
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
		
		Page<Claim> repositoryPage = repository.findAll(pageRequest);
		
		return new ClaimPage(
				repositoryPage.getContent(),
				repositoryPage.getNumber(),
				repositoryPage.getSize(),
				repositoryPage.getTotalElements(),
				repositoryPage.getTotalPages());			
	}
	
	@Transactional(readOnly = true)
	public List<ClaimStatusHistoryResponse> getStatusHistory(long claimId) {
		if(!repository.existsById(claimId)) {
			throw new ClaimNotFoundException(claimId);
		}
		
		return historyRepository.findByClaimId(claimId)
									.stream()
									.map(this::toStatusHistoryResponse)
									.toList();
	}
	
	private ClaimStatusHistoryResponse toStatusHistoryResponse(ClaimStatusHistoryProjection projection) {
		return new ClaimStatusHistoryResponse(
				projection.getId(),
				projection.getClaimId(),
				projection.getClaimNumber(),
				projection.getStatus(),
				projection.getChangedAt());
	}
	
	@Transactional
	public Claim updateStatus(long claimId, ClaimStatus newStatus) {
		Claim claim = repository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
		
		claim.transitionTo(newStatus);		
		ClaimStatusHistory history = ClaimStatusHistory.record(claim, claim.getStatus(), LocalDateTime.now());
		
		historyRepository.save(history);
		return claim;
		
		
		
	}
	
}
