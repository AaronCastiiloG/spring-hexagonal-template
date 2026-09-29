package com.company.template.domain.port;

import com.company.template.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	RefreshToken save(RefreshToken refreshToken);

}
