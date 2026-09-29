package com.company.template.domain.model;

import java.time.LocalDateTime;

public record IssuedTokenPair(
		String accessToken,
		String refreshToken,
		long expiresInSeconds,
		LocalDateTime refreshExpiresAt
) {
}
