package com.company.template.application.service;

import com.company.template.application.port.in.IAuthService;
import com.company.template.domain.exception.UnauthorizedException;
import com.company.template.domain.model.Account;
import com.company.template.domain.model.IssuedTokenPair;
import com.company.template.domain.model.RefreshToken;
import com.company.template.domain.port.AccountRepository;
import com.company.template.domain.port.PasswordHasher;
import com.company.template.domain.port.RefreshTokenRepository;
import com.company.template.domain.port.TokenIssuer;
import com.company.template.domain.service.TokenHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService implements IAuthService {

	static final String INVALID_CREDENTIALS = "Username or password is incorrect";
	static final String INVALID_REFRESH = "The refresh token is invalid or expired";

	private final AccountRepository accountRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordHasher passwordHasher;
	private final TokenIssuer tokenIssuer;
	private final TokenHasher tokenHasher = new TokenHasher();

	public AuthService(
			AccountRepository accountRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordHasher passwordHasher,
			TokenIssuer tokenIssuer
	) {
		this.accountRepository = accountRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordHasher = passwordHasher;
		this.tokenIssuer = tokenIssuer;
	}

	@Override
	@Transactional
	public IssuedTokenPair login(String username, String password) {
		Account account = accountRepository.findByUsername(username)
				.orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", INVALID_CREDENTIALS));
		if (!passwordHasher.matches(password, account.getPasswordHash())) {
			throw new UnauthorizedException("INVALID_CREDENTIALS", INVALID_CREDENTIALS);
		}
		return issueTokens(account);
	}

	@Override
	@Transactional
	public IssuedTokenPair refresh(String refreshToken) {
		String subject = tokenIssuer.refreshSubject(refreshToken)
				.orElseThrow(() -> new UnauthorizedException("INVALID_REFRESH_TOKEN", INVALID_REFRESH));
		RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHasher.sha256(refreshToken))
				.orElseThrow(() -> new UnauthorizedException("INVALID_REFRESH_TOKEN", INVALID_REFRESH));
		if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new UnauthorizedException("INVALID_REFRESH_TOKEN", INVALID_REFRESH);
		}
		if (!stored.getAccount().getUsername().equals(subject)) {
			throw new UnauthorizedException("INVALID_REFRESH_TOKEN", INVALID_REFRESH);
		}
		stored.setRevoked(true);
		refreshTokenRepository.save(stored);
		return issueTokens(stored.getAccount());
	}

	@Override
	@Transactional
	public void logout(String refreshToken) {
		if (tokenIssuer.refreshSubject(refreshToken).isEmpty()) {
			return;
		}
		refreshTokenRepository.findByTokenHash(tokenHasher.sha256(refreshToken))
				.ifPresent(stored -> {
					stored.setRevoked(true);
					refreshTokenRepository.save(stored);
				});
	}

	private IssuedTokenPair issueTokens(Account account) {
		IssuedTokenPair issued = tokenIssuer.issue(account);

		RefreshToken stored = new RefreshToken();
		stored.setAccount(account);
		stored.setTokenHash(tokenHasher.sha256(issued.refreshToken()));
		stored.setExpiresAt(issued.refreshExpiresAt());
		stored.setRevoked(false);
		refreshTokenRepository.save(stored);

		return issued;
	}

}
