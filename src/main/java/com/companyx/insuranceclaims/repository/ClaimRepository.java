package com.companyx.insuranceclaims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.companyx.insuranceclaims.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

	boolean existsByClaimNumber(String claim);
}
