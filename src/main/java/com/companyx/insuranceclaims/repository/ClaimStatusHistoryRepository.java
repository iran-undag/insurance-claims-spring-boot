package com.companyx.insuranceclaims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.companyx.insuranceclaims.entity.ClaimStatusHistory;

public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, Long> {	
	
	@Query(
			"""
			SELECT h.id AS id,
					c.id AS claimId,
					c.claimNumber AS claimNumber,
					h.status AS status,
					h.changedAt AS changedAt
			FROM ClaimStatusHistory h
			JOIN h.claim c
			WHERE c.id = :claimId
			ORDER BY h.changedAt ASC, h.id ASC
			"""
			)
	List<ClaimStatusHistoryProjection> findByClaimId(@Param("claimId") Long claimId);
}
