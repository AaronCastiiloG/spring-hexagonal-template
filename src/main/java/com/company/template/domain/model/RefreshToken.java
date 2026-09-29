package com.company.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

	private UUID id;
	private Account account;
	private String tokenHash;
	private LocalDateTime expiresAt;
	private boolean revoked;
	private LocalDateTime createdAt;

}
