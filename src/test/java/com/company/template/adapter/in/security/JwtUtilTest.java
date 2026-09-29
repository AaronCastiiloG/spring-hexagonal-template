package com.company.template.adapter.in.security;

import com.company.template.config.JwtProperties;
import com.company.template.domain.model.Account;
import com.company.template.domain.model.IssuedTokenPair;
import com.company.template.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

	private final JwtProperties properties = new JwtProperties("test-jwt-secret-key-at-least-32-bytes", 15, 7);
	private final JwtConfig jwtConfig = new JwtConfig();
	private final JwtUtil jwtService = new JwtUtil(jwtConfig.jwtEncoder(properties), jwtConfig.jwtDecoder(properties), properties);

	@Test
	void accessTokenCarriesSubjectAndType() {
		Account account = account(Role.USER);

		IssuedTokenPair tokens = jwtService.issue(account);
		Jwt jwt = jwtConfig.jwtDecoder(properties).decode(tokens.accessToken());

		assertThat(jwt.getSubject()).isEqualTo("ana");
		assertThat(jwt.getClaimAsString("type")).isEqualTo("access");
		assertThat(tokens.expiresInSeconds()).isEqualTo(900);
	}

	@Test
	void refreshTokenIsDistinctFromAccessToken() {
		Account account = account(Role.ADMIN);

		IssuedTokenPair tokens = jwtService.issue(account);
		Jwt jwt = jwtConfig.jwtDecoder(properties).decode(tokens.refreshToken());

		assertThat(tokens.refreshToken()).isNotEqualTo(tokens.accessToken());
		assertThat(jwt.getClaimAsString("type")).isEqualTo("refresh");
		assertThat(jwtService.refreshSubject(tokens.refreshToken())).contains("ana");
		assertThat(jwtService.refreshSubject(tokens.accessToken())).isEmpty();
	}

	private Account account(Role role) {
		Account account = new Account();
		account.setUsername("ana");
		account.setRoles(Set.of(role));
		return account;
	}

}
