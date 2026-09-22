package com.companyx.insuranceclaims.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.companyx.insuranceclaims.dto.ClaimPageResponse;
import com.companyx.insuranceclaims.dto.ClaimResponse;
import com.companyx.insuranceclaims.dto.ClaimStatusHistoryResponse;
import com.companyx.insuranceclaims.dto.CreateClaimRequest;
import com.companyx.insuranceclaims.service.ClaimService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
	
	private final ClaimService claimService;
	
	public ClaimController(ClaimService claimService) {
		this.claimService = claimService;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClaimResponse create(@Valid @RequestBody CreateClaimRequest request) {
		
		return ClaimResponse.from(claimService.create(request.toClaim()));
		
	}
	
	@GetMapping("/{id}")
	public ClaimResponse getById(@PathVariable("id") long id) {
		return ClaimResponse.from(claimService.get(id));
	}
	
	@GetMapping
	public ClaimPageResponse list(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "20") int size) {
		return ClaimPageResponse.from(claimService.list(page, size));
	}
	
	@GetMapping("/{id}/status-history")
	public List<ClaimStatusHistoryResponse> getStatusHistory(
			@PathVariable("id") long id) {
		return claimService.getStatusHistory(id);
		
	}
}
