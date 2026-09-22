package com.companyx.insuranceclaims.dto;

import java.util.List;

import com.companyx.insuranceclaims.service.ClaimPage;

import lombok.Getter;

@Getter
public class ClaimPageResponse {
	
	private final List<ClaimResponse> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;
	
	private ClaimPageResponse(ClaimPage claimPage) {
		this.content = claimPage.getContent()
							.stream()
							.map(ClaimResponse::from)
							.toList();
		this.page = claimPage.getPage();
		this.size = claimPage.getSize();
		this.totalElements = claimPage.getTotalElements();
		this.totalPages = claimPage.getTotalPages();
	}
	
	public static ClaimPageResponse from(ClaimPage claimPage) {
		return new ClaimPageResponse(claimPage);
	}
}
