package com.company.template.domain.port;

import com.company.template.domain.model.PageSlice;
import com.company.template.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	User save(User user);

	PageSlice<User> findAll(int page, int size);

	Optional<User> findByIdWithLogs(UUID id);

	boolean existsByName(String name);

	boolean existsByEmail(String email);

	boolean existsByNameAndIdNot(String name, UUID id);

	boolean existsByEmailAndIdNot(String email, UUID id);

	void delete(User user);

}
