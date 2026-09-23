package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UpdateClaimStatusRequestValidationTest {
	
	@Test
	void rejectsNullStatus() {
		UpdateClaimStatusRequest request = new UpdateClaimStatusRequest(null);
		
		try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
			
			Validator validator = factory.getValidator();
			
			Set<ConstraintViolation<UpdateClaimStatusRequest>> violations = validator.validate(request);
			
			assertTrue(violations.stream().anyMatch(violation -> "status".equals(violation.getPropertyPath().toString())));
		}
	}
}
