package com.company.template.adapter.out.persistence.repository;

import com.company.template.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

	@EntityGraph(attributePaths = "roles")
	Optional<AccountEntity> findByUsername(String username);

}
