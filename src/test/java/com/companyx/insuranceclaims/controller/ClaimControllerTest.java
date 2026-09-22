package com.companyx.insuranceclaims.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.dao.DataIntegrityViolationException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.companyx.insuranceclaims.dto.ClaimStatusHistoryResponse;
import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.companyx.insuranceclaims.service.ClaimPage;
import com.companyx.insuranceclaims.service.ClaimService;
import com.companyx.insuranceclaims.exception.ClaimNotFoundException;
import com.companyx.insuranceclaims.exception.DuplicateClaimNumberException;
import com.companyx.insuranceclaims.exception.ApiErrorCode;
import com.companyx.insuranceclaims.exception.InvalidPaginationException;


import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;


@WebMvcTest(ClaimController.class)
public class ClaimControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ClaimService claimService;
	
	@Test
	void createsClaimAndReturnsCreatedResponse() throws Exception {
  		Claim savedClaim = Claim.create(
  				"CLM-API-001",
  				"POL-API-001",
  				"Isabel Santos",
  				LocalDate.of(2026, 9, 10),
  				ClaimType.AUTO,
  				new BigDecimal("1250.75"),
  				"Rear bumper damage"
  				);
  		
  		when(claimService.create(any(Claim.class))).thenReturn(savedClaim);
  		
  		String json  = """
  				 {
  				  "claimNumber": "CLM-API-001",
  				  "policyNumber": "POL-API-001",
  				  "claimantName": "Isabel Santos",
  				  "incidentDate": "2026-09-10",
  				  "claimType": "AUTO",
  				  "claimedAmount": 1250.75,
  				  "description": "Rear bumper damage"
  				}  				
  				""";
  		
  		mockMvc.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(json))
  					.andExpect(status().isCreated())
  					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
  					.andExpect(jsonPath("$.claimNumber").value("CLM-API-001"))
  					.andExpect(jsonPath("$.incidentDate").value("2026-09-10"))
  					.andExpect(jsonPath("$.status").value("SUBMITTED"))
  					;
  		
  		verify(claimService).create(any(Claim.class));
  					
	}
	
	@Test
	void retrievesClaimById() throws Exception {
		Claim claim = Claim.create(
	  			"CLM-API-002",
	  			"POL-API-002",
	  			"Rafael Garcia",
	  			LocalDate.of(2026, 9, 9),
	  			ClaimType.HOME,
	  			new BigDecimal("4200.00"),
	  			"Water damage to kitchen"
				);
		
		when(claimService.get(42L)).thenReturn(claim);
		
		mockMvc.perform(get("/api/claims/{id}", 42L))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.claimNumber").value("CLM-API-002"))
					.andExpect(jsonPath("$.incidentDate").value("2026-09-09"))
		  			.andExpect(jsonPath("$.claimType").value("HOME"))
		  			.andExpect(jsonPath("$.status").value("SUBMITTED"));
		
		verify(claimService).get(42L);
	}	
	
	@Test
	void useDefaultPaginationParameters() throws Exception {
	  	Claim first = Claim.create(
	  			"CLM-API-003",
	  			"POL-API-003",
	  			"Ana Mendoza",
	  			LocalDate.of(2026, 9, 8),
	  			ClaimType.AUTO,
	  			new BigDecimal("1500.00"),
	  			"Front windshield damage");

	  	Claim second = Claim.create(
	  			"CLM-API-004",
	  			"POL-API-004",
	  			"Leo Castillo",
	  			LocalDate.of(2026, 9, 7),
	  			ClaimType.TRAVEL,
	  			new BigDecimal("3200.00"),
	  			"Cancelled international flight");
	  	
	  	ClaimPage page = new ClaimPage(List.of(first, second), 0, 20, 2L, 1);
	  	
	  	when(claimService.list(0, 20)).thenReturn(page);
	  	
	  	mockMvc.perform(get("/api/claims"))
	  			.andExpect(status().isOk())
	  			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	  			.andExpect(jsonPath("$.content[0].claimNumber").value("CLM-API-003"))
	  			.andExpect(jsonPath("$.content[1].claimNumber").value("CLM-API-004"))
	  			.andExpect(jsonPath("$.page").value(0))
	  			.andExpect(jsonPath("$.size").value(20))
	  			.andExpect(jsonPath("$.totalElements").value(2))
	  			.andExpect(jsonPath("$.totalPages").value(1)
	  					);

	  	verify(claimService).list(0, 20);
	}
	
	@Test
	void returnsNotFoundWhenClaimDoesNotExist() throws Exception {
		when(claimService.get(99L)).thenThrow(new ClaimNotFoundException(99L));
		
		mockMvc.perform(get("/api/claims/{id}", 99L))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.code").value("CLAIM_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("Claim not found with id: 99"));
		
		verify(claimService).get(99L);
		
		
	}
	
	@Test
	void returnsConflictWhenClaimNumberAlreadyExists() throws Exception {
		
		when(claimService.create(any(Claim.class))).thenThrow(new DuplicateClaimNumberException("CLM-API-001"));
		
	  	String json = """
	  			{
	  			  "claimNumber": "CLM-API-001",
	  			  "policyNumber": "POL-API-001",
	  			  "claimantName": "Isabel Santos",
	  			  "incidentDate": "2026-09-10",
	  			  "claimType": "AUTO",
	  			  "claimedAmount": 1250.75,
	  			  "description": "Rear bumper damage"
	  			}
	  			""";

	  	mockMvc.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(json))
	  					.andExpect(status().isConflict())
	  					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	  					.andExpect(jsonPath("$.status").value(409))
	  					.andExpect(jsonPath("$.code").value("DUPLICATE_CLAIM_NUMBER"))
	  					.andExpect(jsonPath("$.message").value("Claim number already exists CLM-API-001"));
	  	
	  	verify(claimService).create(any(Claim.class));
		
	}
	
	@Test
	void rejectsInvalidClaimBeforeCallingService() throws Exception {
		
	  	mockMvc.perform(post("/api/claims")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
			.andExpect(jsonPath("$.message").value("Validation failed"))
			.andExpect(jsonPath("$.fieldErrors.claimNumber").isString())
			.andExpect(jsonPath("$.fieldErrors.claimedAmount").isString());

		
		verifyNoInteractions(claimService);

	}
	
	  @Test
	  void returnsBadRequestForInvalidPage() throws Exception {
	  	when(claimService.list(-1, 20)).thenThrow(new InvalidPaginationException(
	  														ApiErrorCode.INVALID_PAGE,
	  														"page must be zero or greater"));

	  	mockMvc.perform(get("/api/claims")
	  					.param("page", "-1"))
	  			.andExpect(status().isBadRequest())
	  			.andExpect(content()
	  					.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	  			.andExpect(jsonPath("$.status").value(400))
	  			.andExpect(jsonPath("$.code")
	  					.value("INVALID_PAGE"))
	  			.andExpect(jsonPath("$.message")
	  					.value("page must be zero or greater"));

	  	verify(claimService).list(-1, 20);
	  }

	  @Test
	  void returnsBadRequestForInvalidPageSize() throws Exception {
	  	when(claimService.list(0, 101)).thenThrow(new InvalidPaginationException(
	  										ApiErrorCode.INVALID_PAGE_SIZE,
	  										"size must be between 1 and 100"));

	  	mockMvc.perform(get("/api/claims")
	  					.param("size", "101"))
	  			.andExpect(status().isBadRequest())
	  			.andExpect(content()
	  					.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	  			.andExpect(jsonPath("$.status").value(400))
	  			.andExpect(jsonPath("$.code")
	  					.value("INVALID_PAGE_SIZE"))
	  			.andExpect(jsonPath("$.message")
	  					.value("size must be between 1 and 100"));

	  	verify(claimService).list(0, 101);
	  }

		
	//This covers two concurrent requests that both pass the service’s preliminary existence check before PostgreSQL rejects one insert
	@Test
	void returnsConflictWithoutDatabaseDetailsForUniquenessViolation() throws Exception {
		
		when(claimService.create(any(Claim.class))).thenThrow(new DataIntegrityViolationException("claims_claim_number_key"));
		
	  	String json = """
	  			{
	  			  "claimNumber": "CLM-API-001",
	  			  "policyNumber": "POL-API-001",
	  			  "claimantName": "Isabel Santos",
	  			  "incidentDate": "2026-09-10",
	  			  "claimType": "AUTO",
	  			  "claimedAmount": 1250.75,
	  			  "description": "Rear bumper damage"
	  			}
	  			""";
	  	
	  	mockMvc.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(json))
	  				.andExpect(status().isConflict())
	  				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
	  				.andExpect(jsonPath("$.status").value(409))
	  				.andExpect(jsonPath("$.code").value("DUPLICATE_CLAIM_NUMBER"))
	  				.andExpect(jsonPath("$.message").value("Claim number already exists"))
	  				.andExpect(content().string(not(containsString("claims_claim_number_key"))));
	  	
	  	verify(claimService).create(any(Claim.class));

	}
	
	@Test
	void retrievesClaimStatusHistory() throws Exception{
		List<ClaimStatusHistoryResponse> history = List.of(
	  			new ClaimStatusHistoryResponse(
	  					7L,
	  					42L,
	  					"CLM-API-HISTORY-042",
	  					ClaimStatus.SUBMITTED,
	  					LocalDateTime.of(2026, 9, 20, 9, 0)),
	  			new ClaimStatusHistoryResponse(
	  					8L,
	  					42L,
	  					"CLM-API-HISTORY-042",
	  					ClaimStatus.SUBMITTED,
	  					LocalDateTime.of(2026, 9, 21, 14, 30))				
				);
		
		when(claimService.getStatusHistory(42L)).thenReturn(history);
		
		mockMvc.perform(get("/api/claims/{id}/status-history", 42L))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$[0].id").value(7))
					.andExpect(jsonPath("$[0].claimId").value(42))
					.andExpect(jsonPath("$[0].claimNumber").value("CLM-API-HISTORY-042"))
					.andExpect(jsonPath("$[0].status").value("SUBMITTED"))
					.andExpect(jsonPath("$[0].changedAt").value("2026-09-20T09:00:00"))
					.andExpect(jsonPath("$[1].id").value(8))
					.andExpect(jsonPath("$[1].changedAt").value("2026-09-21T14:30:00"))
					;
		
		verify(claimService).getStatusHistory(42L);
		
	}
}
