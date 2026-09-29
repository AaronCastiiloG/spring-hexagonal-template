package com.company.template.adapter.out.persistence.repository;

import com.company.template.adapter.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

	@Query("SELECT token FROM RefreshTokenEntity token JOIN FETCH token.account WHERE token.tokenHash = :tokenHash")
	Optional<RefreshTokenEntity> findByTokenHash(@Param("tokenHash") String tokenHash);

}
