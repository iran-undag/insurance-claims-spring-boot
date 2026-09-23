package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.companyx.insuranceclaims.entity.ClaimStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTest
public class UpdateClaimStatusRequestJsonTest {
	private final ObjectMapper objectMapper;
	
	@Autowired
	UpdateClaimStatusRequestJsonTest(ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
	}
	
	
	@Test
	void deserializesStatusThroughCreator() throws Exception{
		String json = """
				{
  				  "status": "UNDER_REVIEW"
  				}
				""";
		
		UpdateClaimStatusRequest request = objectMapper.readValue(json, UpdateClaimStatusRequest.class);
		
		assertEquals(ClaimStatus.UNDER_REVIEW, request.getStatus());
	}
}
