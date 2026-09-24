package com.companyx.insuranceclaims.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.companyx.insuranceclaims.entity.ClaimType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class CreateClaimRequestValidationTest {
	
	@Test
	void rejectsBlankClaimNumber() {
		CreateClaimRequest request = new CreateClaimRequest(
  				" ",
  				"POL-DTO-003",
  				"Taylor Cruz",
  				LocalDate.now(),
  				ClaimType.AUTO,
  				new BigDecimal("100.00"),
  				"Windshield damage"
				);
		
		try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
			
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
			
			assertTrue(violations.stream().anyMatch(violation -> "claimNumber".equals(violation.getPropertyPath().toString())));
		}
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"CLM1","1234567890123456789012345678901"})
	void rejectsClaimNumberOutsideAllowedLength(String claimNumber) {
		CreateClaimRequest request = new CreateClaimRequest(
	  			claimNumber,
	  			"POL-DTO-004",
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage"
				);
				
			try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
				
				Validator validator = factory.getValidator();
				Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
				
				assertTrue(violations.stream().anyMatch(violation -> "claimNumber".equals(violation.getPropertyPath().toString())));
			}
				
	}
	
	@ParameterizedTest
	@ValueSource(strings = {" ","POL1", "1234567890123456789012345678901"})
	void rejectsInvalidPolicyNumber(String policyNumber) {
		CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-005",
	  			policyNumber,
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage"
				);
		
		try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
			
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
			
			assertTrue(violations.stream().anyMatch(violation -> "policyNumber".equals(violation.getPropertyPath().toString())));
			
		}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 101})
	void rejectsInvalidClaimantNameLength(int length) {
		CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-006",
	  			"POL-DTO-006",
	  			"A".repeat(length),
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage"				
				);
		
		try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
			
			assertTrue(violations.stream().anyMatch(violation -> "claimantName".equals(violation.getPropertyPath().toString())));
		}
	}
	
	@Test
	void rejectsFutureIncidentDate() {
		CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-007",
	  			"POL-DTO-007",
	  			"Taylor Cruz",
	  			LocalDate.now().plusDays(1),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage"
				);
		
		try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
			
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
			
			assertTrue(violations.stream().anyMatch(violation -> "incidentDate".equals(violation.getPropertyPath().toString())));
		}
	}
	
	@Test
	void rejectsNullIncidentDate() {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-008",
	  			"POL-DTO-008",
	  			"Taylor Cruz",
	  			null,
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage");

	  	try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
	  		
	  		Validator validator = factory.getValidator();
	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
	  		
	  		assertTrue(violations.stream().anyMatch(violation -> "incidentDate".equals(violation.getPropertyPath().toString())));
	  	}
	}
	
	@Test
	void rejectsNullClaimType() {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-009",
	  			"POL-DTO-009",
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			null,
	  			new BigDecimal("100.00"),
	  			"Windshield damage"
	  			);
	  	
	  	try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
	  		Validator validator = factory.getValidator();
	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
	  		
	  		assertTrue(violations.stream().anyMatch(violation -> "claimType".equals(violation.getPropertyPath().toString())));
	  	}	  	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"0.00", "-0.01"})
	void rejectsNonPositiveClaimedAmount(String amount) {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-010",
	  			"POL-DTO-010",
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal(amount),
	  			"Windshield damage"
	  			);
	  	
	  	try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
	  		Validator validator = factory.getValidator();
	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
	  		
	  		assertTrue(violations.stream().anyMatch(violation -> "claimedAmount".equals(violation.getPropertyPath().toString())));
	  	}	

	}
	
	@Test
	void rejectsNullClaimedAmount() {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-011",
	  			"POL-DTO-011",
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			null,
	  			"Windshield damage"
	  			);
	  	
	  	try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
	  		Validator validator = factory.getValidator();
	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
	  		
	  		assertTrue(violations.stream().anyMatch(violation -> "claimedAmount".equals(violation.getPropertyPath().toString())));
	  		
	  	}
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 501})
	void rejectsInvalidDescriptionLength(int length) {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-012",
	  			"POL-DTO-012",
	  			"Taylor Cruz",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"D".repeat(length)
	  			);
	  	
	  	try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()){
	  		Validator validator = factory.getValidator();
	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);
	  		
	  		assertTrue(violations.stream().anyMatch(violation -> "description".equals(violation.getPropertyPath().toString())));
	  	}
	}
	
	@Test
	void rejectsIncidentLocationLongerThanMaximum() {
	  	CreateClaimRequest request = new CreateClaimRequest(
	  			"CLM-DTO-LOCATION-004",
	  			"POL-DTO-LOCATION-004",
	  			"Updated Client",
	  			LocalDate.now(),
	  			ClaimType.AUTO,
	  			new BigDecimal("100.00"),
	  			"Windshield damage",
	  			"L".repeat(201));

	  	try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {

	  		Validator validator = factory.getValidator();

	  		Set<ConstraintViolation<CreateClaimRequest>> violations = validator.validate(request);

	  		assertTrue(violations.stream().anyMatch(violation -> "incidentLocation".equals(violation.getPropertyPath().toString())));
	  	}

	}
	
	
}
