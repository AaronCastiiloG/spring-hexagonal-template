package com.company.template.adapter.in.web.controller;

import com.company.template.adapter.in.web.dto.request.LoginRequest;
import com.company.template.adapter.in.web.dto.request.RefreshTokenRequest;
import com.company.template.adapter.in.web.dto.response.TokenResponse;
import com.company.template.application.port.in.IAuthService;
import com.company.template.domain.model.IssuedTokenPair;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final IAuthService authService;

	@PostMapping("/login")
	public TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return toResponse(authService.login(request.getUsername(), request.getPassword()));
	}

	@PostMapping("/refresh")
	public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return toResponse(authService.refresh(request.getRefreshToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.noContent().build();
	}

	private TokenResponse toResponse(IssuedTokenPair tokens) {
		return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.expiresInSeconds());
	}

}
