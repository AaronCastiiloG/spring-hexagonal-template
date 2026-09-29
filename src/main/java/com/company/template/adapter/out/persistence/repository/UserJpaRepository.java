package com.company.template.adapter.out.persistence.repository;

import com.company.template.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

	@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.logs WHERE u.id = :id")
	Optional<UserEntity> findByIdWithLogs(UUID id);

	boolean existsByName(String name);

	boolean existsByEmail(String email);

	boolean existsByNameAndIdNot(String name, UUID id);

	boolean existsByEmailAndIdNot(String email, UUID id);

}
