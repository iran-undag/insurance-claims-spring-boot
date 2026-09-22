package com.companyx.insuranceclaims.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "claim_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimStatusHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "claim_id", nullable = false)
	private Claim claim;
	
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ClaimStatus status;
	
	@Column(name = "changed_at", nullable = false)
	private LocalDateTime changedAt;
	
	private ClaimStatusHistory(
			Claim claim,
			ClaimStatus status,
			LocalDateTime changedAt) {
		this.claim = claim;
		this.status = status;
		this.changedAt = changedAt;		
	}
	
	public static ClaimStatusHistory record(
			Claim claim,
			ClaimStatus status,
			LocalDateTime changedAt) {
		
		return new ClaimStatusHistory(claim, status, changedAt);		
	}
	
}
