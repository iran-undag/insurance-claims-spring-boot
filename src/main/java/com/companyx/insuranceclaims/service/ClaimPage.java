package com.companyx.insuranceclaims.service;

import java.util.List;

import com.companyx.insuranceclaims.entity.Claim;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClaimPage {
	
	private final List<Claim> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;
}
