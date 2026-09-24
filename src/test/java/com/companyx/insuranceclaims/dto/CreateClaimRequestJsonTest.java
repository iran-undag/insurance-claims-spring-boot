package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.companyx.insuranceclaims.entity.Claim;
import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.companyx.insuranceclaims.entity.ClaimType;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTest
public class CreateClaimRequestJsonTest {
	
	private final ObjectMapper objectMapper;
	
	@Autowired
	CreateClaimRequestJsonTest(ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
	}
	
	@Test
	void deserializesJsonThroughCreator() throws Exception{
		String json = """
				{
  				  "claimNumber": "CLM-DTO-001",
  				  "policyNumber": "POL-DTO-001",
  				  "claimantName": "Alex Rivera",
  				  "incidentDate": "2026-09-01",
  				  "claimType": "AUTO",
  				  "claimedAmount": 1250.75,
  				  "description": "Rear bumper damage"
  				} 
  				
  				""";
		
		CreateClaimRequest request = objectMapper.readValue(json, CreateClaimRequest.class);
		
		assertAll(
				() -> assertEquals("CLM-DTO-001", request.getClaimNumber()),
				() -> assertEquals("POL-DTO-001", request.getPolicyNumber()),
				() -> assertEquals("Alex Rivera", request.getClaimantName()),
				() -> assertEquals(LocalDate.of(2026, 9, 1), request.getIncidentDate()),
				() -> assertEquals(ClaimType.AUTO, request.getClaimType()),
				() -> assertEquals(new BigDecimal("1250.75"), request.getClaimedAmount()),
				() -> assertEquals("Rear bumper damage", request.getDescription())				
				);
	}
	
	@Test
	void mapsRequestToNewClaim() {
		CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-002",
	  			"POL-DTO-002",
	  			"Jamie Santos",
	  			LocalDate.of(2026, 9, 2),
	  			ClaimType.HOME,
	  			new BigDecimal("2400.00"),
	  			"Storm damage to roof"
				);
		
		Claim claim = request.toClaim();
		
		assertAll(
				() -> assertEquals("CLM-DTO-002", claim.getClaimNumber()),
				() -> assertEquals("POL-DTO-002", claim.getPolicyNumber()),
				() -> assertEquals("Jamie Santos", claim.getClaimantName()),
				() -> assertEquals(LocalDate.of(2026, 9, 2), claim.getIncidentDate()),
				() -> assertEquals(ClaimType.HOME, claim.getClaimType()),
				() -> assertEquals(new BigDecimal("2400.00"), claim.getClaimedAmount()),				
				() -> assertEquals(ClaimStatus.SUBMITTED, claim.getStatus()),
				() -> assertEquals("Storm damage to roof", claim.getDescription())				
				);	
		
	}
	
	@Test
	void deserializesOldJsonWithoutIncidentLocation() throws Exception {
		String json = """
					{ 
						"claimNumber": "CLM-DTO-LEGACY-001",
						"policyNumber": "POL-DTO-LEGACY-001",
						"claimantName": "Legacy Client",
						"incidentDate": "2026-09-20",
						"claimType": "AUTO",
						"claimedAmount": 1500.00,
						"description": "Payload from an older client"						
					}			
					""";
		
		CreateClaimRequest request = objectMapper.readValue(json, CreateClaimRequest.class);
		
		assertEquals("CLM-DTO-LEGACY-001", request.getClaimNumber());
		assertNull(request.getIncidentLocation());
	}
	
	@Test
	void mapsIncidentLocationToNewClaim() {
		CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-LOCATION-001",
	  			"POL-DTO-LOCATION-001",
	  			"Elena Cruz",
	  			LocalDate.of(2026, 9, 22),
	  			ClaimType.HOME,
	  			new BigDecimal("2800.00"),
	  			"Storm damage",
	  			"Quezon City"
				);
		
		Claim claim = request.toClaim();
		assertEquals("Quezon City", claim.getIncidentLocation());
	}
	
	@Test
	void deserializesJsonWithIncidentLocation() throws Exception {
	  	String json = """
	  			{
	  			  "claimNumber": "CLM-DTO-LOCATION-003",
	  			  "policyNumber": "POL-DTO-LOCATION-003",
	  			  "claimantName": "Updated Client",
	  			  "incidentDate": "2026-09-22",
	  			  "claimType": "AUTO",
	  			  "claimedAmount": 1600.00,
	  			  "description": "Payload from an updated client",
	  			  "incidentLocation": "Pasig City"
	  			}
	  			""";

	  	CreateClaimRequest request = objectMapper.readValue(json, CreateClaimRequest.class);

	  	assertEquals("Pasig City", request.getIncidentLocation());

	}

}
