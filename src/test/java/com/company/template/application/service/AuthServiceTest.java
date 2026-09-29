package com.company.template.application.service;

import com.company.template.domain.exception.UnauthorizedException;
import com.company.template.domain.model.Account;
import com.company.template.domain.model.IssuedTokenPair;
import com.company.template.domain.model.RefreshToken;
import com.company.template.domain.model.Role;
import com.company.template.domain.port.AccountRepository;
import com.company.template.domain.port.PasswordHasher;
import com.company.template.domain.port.RefreshTokenRepository;
import com.company.template.domain.port.TokenIssuer;
import com.company.template.domain.service.TokenHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private PasswordHasher passwordHasher;

	@Mock
	private TokenIssuer tokenIssuer;

	private final TokenHasher tokenHasher = new TokenHasher();
	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(accountRepository, refreshTokenRepository, passwordHasher, tokenIssuer);
	}

	@Test
	void loginRejectsUnknownUsername() {
		when(accountRepository.findByUsername("ana")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login("ana", "password123"))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessage(AuthService.INVALID_CREDENTIALS)
				.hasFieldOrPropertyWithValue("code", "INVALID_CREDENTIALS");
	}

	@Test
	void loginRejectsWrongPasswordWithTheSameError() {
		Account account = account("ana");
		when(accountRepository.findByUsername("ana")).thenReturn(Optional.of(account));
		when(passwordHasher.matches("password123", account.getPasswordHash())).thenReturn(false);

		assertThatThrownBy(() -> authService.login("ana", "password123"))
				.isInstanceOf(UnauthorizedException.class)
				.hasMessage(AuthService.INVALID_CREDENTIALS)
				.hasFieldOrPropertyWithValue("code", "INVALID_CREDENTIALS");
	}

	@Test
	void loginIssuesTokens() {
		Account account = account("ana");
		when(accountRepository.findByUsername("ana")).thenReturn(Optional.of(account));
		when(passwordHasher.matches("password123", account.getPasswordHash())).thenReturn(true);
		when(tokenIssuer.issue(account)).thenReturn(issued("access", "refresh"));

		IssuedTokenPair response = authService.login("ana", "password123");

		assertThat(response.accessToken()).isEqualTo("access");
		assertThat(response.refreshToken()).isEqualTo("refresh");
		assertThat(response.expiresInSeconds()).isEqualTo(900);
		ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
		verify(refreshTokenRepository).save(saved.capture());
		assertThat(saved.getValue().getTokenHash()).isEqualTo(tokenHasher.sha256("refresh"));
		assertThat(saved.getValue().isRevoked()).isFalse();
	}

	@Test
	void refreshRotatesTokenAndRevokesPrevious() {
		Account account = account("ana");
		String current = "current-refresh";
		RefreshToken stored = new RefreshToken();
		stored.setAccount(account);
		stored.setRevoked(false);
		stored.setExpiresAt(LocalDateTime.now().plusDays(1));
		stored.setTokenHash(tokenHasher.sha256(current));
		when(tokenIssuer.refreshSubject(current)).thenReturn(Optional.of("ana"));
		when(refreshTokenRepository.findByTokenHash(tokenHasher.sha256(current))).thenReturn(Optional.of(stored));
		when(tokenIssuer.issue(account)).thenReturn(issued("new-access", "new-refresh"));

		IssuedTokenPair response = authService.refresh(current);

		assertThat(stored.isRevoked()).isTrue();
		assertThat(response.accessToken()).isEqualTo("new-access");
		verify(refreshTokenRepository).save(stored);
	}

	@Test
	void logoutRevokesStoredRefreshToken() {
		String current = "current-refresh";
		RefreshToken stored = new RefreshToken();
		stored.setRevoked(false);
		when(tokenIssuer.refreshSubject(current)).thenReturn(Optional.of("ana"));
		when(refreshTokenRepository.findByTokenHash(tokenHasher.sha256(current))).thenReturn(Optional.of(stored));

		authService.logout(current);

		assertThat(stored.isRevoked()).isTrue();
		verify(refreshTokenRepository).save(stored);
	}

	@Test
	void refreshRejectsRevokedToken() {
		Account account = account("ana");
		String current = "current-refresh";
		RefreshToken stored = new RefreshToken();
		stored.setAccount(account);
		stored.setRevoked(true);
		stored.setExpiresAt(LocalDateTime.now().plusDays(1));
		when(tokenIssuer.refreshSubject(current)).thenReturn(Optional.of("ana"));
		when(refreshTokenRepository.findByTokenHash(tokenHasher.sha256(current))).thenReturn(Optional.of(stored));

		assertThatThrownBy(() -> authService.refresh(current))
				.isInstanceOf(UnauthorizedException.class)
				.hasFieldOrPropertyWithValue("code", "INVALID_REFRESH_TOKEN");
	}

	private IssuedTokenPair issued(String accessToken, String refreshToken) {
		return new IssuedTokenPair(accessToken, refreshToken, 900, LocalDateTime.now().plusDays(7));
	}

	private Account account(String username) {
		Account account = new Account();
		account.setUsername(username);
		account.setPasswordHash("hash");
		account.setRoles(Set.of(Role.USER));
		return account;
	}

}
