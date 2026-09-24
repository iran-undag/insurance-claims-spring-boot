package com.companyx.insuranceclaims.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.companyx.insuranceclaims.exception.InvalidClaimStatusTransitionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claims")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Claim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "claim_number", nullable = false, unique = true, length = 30)
	private String claimNumber;

	@Column(name = "policy_number", nullable = false, length = 30)
	private String policyNumber;

	@Column(name = "claimant_name", nullable = false, length = 100)
	private String claimantName;

	@Column(name = "incident_date", nullable = false)
	private LocalDate incidentDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "claim_type", nullable = false, length = 20)
	private ClaimType claimType;

	@Column(name = "claimed_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal claimedAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ClaimStatus status;

	@Column(nullable = false, length = 500)
	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Version
	@Column(nullable = false)
	private Long version;

	@Column(name = "incident_location", length = 200)
	private String incidentLocation;

	private Claim(String claimNumber, String policyNumber, String claimantName, LocalDate incidentDate,
			ClaimType claimType, BigDecimal claimedAmount, String description, String incidentLocation) {
		this.claimNumber = claimNumber;
		this.policyNumber = policyNumber;
		this.claimantName = claimantName;
		this.incidentDate = incidentDate;
		this.claimType = claimType;
		this.claimedAmount = claimedAmount;
		this.description = description;
		this.incidentLocation = incidentLocation;
		this.status = ClaimStatus.SUBMITTED;
		this.createdAt = LocalDateTime.now();

	}

	public static Claim create(String claimNumber, String policyNumber, String claimantName, LocalDate incidentDate,
			ClaimType claimType, BigDecimal claimedAmount, String description) {

		return new Claim(claimNumber, policyNumber, claimantName, incidentDate, claimType, claimedAmount, description,
				null);
	}

	public static Claim create(String claimNumber, String policyNumber, String claimantName, LocalDate incidentDate,
			ClaimType claimType, BigDecimal claimedAmount, String description, String incidentLocation) {

		return new Claim(claimNumber, policyNumber, claimantName, incidentDate, claimType, claimedAmount, description,
				incidentLocation);

	}

	public void transitionTo(ClaimStatus newStatus) {
		if (!status.canTransitionTo(newStatus)) {
			throw new InvalidClaimStatusTransitionException(status, newStatus);
		}

		this.status = newStatus;
	}
}
