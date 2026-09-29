package com.company.template.adapter.in.security;

import com.company.template.config.JwtProperties;
import com.company.template.domain.model.Account;
import com.company.template.domain.model.IssuedTokenPair;
import com.company.template.domain.port.TokenIssuer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtUtil implements TokenIssuer {

	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;
	private final JwtProperties jwtProperties;

	public JwtUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtProperties jwtProperties) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
		this.jwtProperties = jwtProperties;
	}

	@Override
	public IssuedTokenPair issue(Account account) {
		Instant now = Instant.now();
		String accessToken = encode(account, "access", now, now.plus(jwtProperties.accessTokenMinutes(), ChronoUnit.MINUTES));
		String refreshToken = encode(account, "refresh", now, now.plus(jwtProperties.refreshTokenDays(), ChronoUnit.DAYS));
		return new IssuedTokenPair(
				accessToken,
				refreshToken,
				jwtProperties.accessTokenMinutes() * 60,
				LocalDateTime.now().plusDays(jwtProperties.refreshTokenDays())
		);
	}

	@Override
	public Optional<String> refreshSubject(String refreshToken) {
		try {
			Jwt jwt = jwtDecoder.decode(refreshToken);
			if (!"refresh".equals(jwt.getClaim("type"))) {
				return Optional.empty();
			}
			return Optional.ofNullable(jwt.getSubject());
		}
		catch (JwtException exception) {
			return Optional.empty();
		}
	}

	private String encode(Account account, String type, Instant issuedAt, Instant expiresAt) {
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).keyId(JwtConfig.KEY_ID).build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.id(UUID.randomUUID().toString())
				.subject(account.getUsername())
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.claim("type", type)
				.claim("roles", account.getRoles().stream().map(Enum::name).toList())
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

}
