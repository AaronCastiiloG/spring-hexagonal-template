package com.company.template.adapter.out.persistence;

import com.company.template.adapter.out.persistence.entity.AccountEntity;
import com.company.template.adapter.out.persistence.entity.RefreshTokenEntity;
import com.company.template.adapter.out.persistence.mapper.RefreshTokenPersistenceMapper;
import com.company.template.adapter.out.persistence.repository.AccountJpaRepository;
import com.company.template.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import com.company.template.domain.model.RefreshToken;
import com.company.template.domain.port.RefreshTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

	private final RefreshTokenJpaRepository refreshTokenJpaRepository;
	private final AccountJpaRepository accountJpaRepository;
	private final RefreshTokenPersistenceMapper refreshTokenPersistenceMapper;

	public RefreshTokenRepositoryAdapter(
			RefreshTokenJpaRepository refreshTokenJpaRepository,
			AccountJpaRepository accountJpaRepository,
			RefreshTokenPersistenceMapper refreshTokenPersistenceMapper
	) {
		this.refreshTokenJpaRepository = refreshTokenJpaRepository;
		this.accountJpaRepository = accountJpaRepository;
		this.refreshTokenPersistenceMapper = refreshTokenPersistenceMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findByTokenHash(String tokenHash) {
		return refreshTokenJpaRepository.findByTokenHash(tokenHash).map(refreshTokenPersistenceMapper::toDomain);
	}

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		if (refreshToken.getId() != null) {
			RefreshTokenEntity entity = refreshTokenJpaRepository.findById(refreshToken.getId())
					.orElseThrow(() -> new IllegalStateException("Refresh token was not found"));
			entity.setRevoked(refreshToken.isRevoked());
			return refreshTokenPersistenceMapper.toDomain(entity);
		}

		RefreshTokenEntity entity = refreshTokenPersistenceMapper.toEntity(refreshToken);
		AccountEntity account = accountJpaRepository.getReferenceById(refreshToken.getAccount().getId());
		entity.setAccount(account);
		return refreshTokenPersistenceMapper.toDomain(refreshTokenJpaRepository.save(entity));
	}

}
