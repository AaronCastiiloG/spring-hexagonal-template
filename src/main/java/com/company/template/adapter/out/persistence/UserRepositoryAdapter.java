package com.company.template.adapter.out.persistence;

import com.company.template.adapter.out.persistence.entity.UserEntity;
import com.company.template.adapter.out.persistence.entity.UserLogEntity;
import com.company.template.adapter.out.persistence.mapper.UserLogPersistenceMapper;
import com.company.template.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.company.template.adapter.out.persistence.repository.UserJpaRepository;
import com.company.template.domain.model.PageSlice;
import com.company.template.domain.model.User;
import com.company.template.domain.model.UserLog;
import com.company.template.domain.port.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class UserRepositoryAdapter implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserPersistenceMapper userPersistenceMapper;
	private final UserLogPersistenceMapper userLogPersistenceMapper;

	public UserRepositoryAdapter(
			UserJpaRepository userJpaRepository,
			UserPersistenceMapper userPersistenceMapper,
			UserLogPersistenceMapper userLogPersistenceMapper
	) {
		this.userJpaRepository = userJpaRepository;
		this.userPersistenceMapper = userPersistenceMapper;
		this.userLogPersistenceMapper = userLogPersistenceMapper;
	}

	@Override
	public User save(User user) {
		if (user.getId() == null) {
			UserEntity entity = userPersistenceMapper.toNewEntity(user);
			attachNewLogs(user, entity);
			return userPersistenceMapper.toDomain(userJpaRepository.save(entity));
		}

		UserEntity entity = userJpaRepository.findById(user.getId())
				.orElseThrow(() -> new IllegalStateException("User was not found"));
		userPersistenceMapper.updateEntity(user, entity);
		attachNewLogs(user, entity);
		return userPersistenceMapper.toDomain(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public PageSlice<User> findAll(int page, int size) {
		Page<UserEntity> result = userJpaRepository.findAll(PageRequest.of(page, size));
		return new PageSlice<>(
				result.getContent().stream().map(userPersistenceMapper::toDomain).toList(),
				result.getTotalElements(),
				result.getTotalPages(),
				result.getNumber(),
				result.getSize()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByIdWithLogs(UUID id) {
		return userJpaRepository.findByIdWithLogs(id).map(userPersistenceMapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return userJpaRepository.existsByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userJpaRepository.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByNameAndIdNot(String name, UUID id) {
		return userJpaRepository.existsByNameAndIdNot(name, id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmailAndIdNot(String email, UUID id) {
		return userJpaRepository.existsByEmailAndIdNot(email, id);
	}

	@Override
	public void delete(User user) {
		userJpaRepository.deleteById(user.getId());
	}

	private void attachNewLogs(User user, UserEntity entity) {
		for (UserLog log : user.getLogs()) {
			if (log.getId() != null) {
				continue;
			}
			UserLogEntity logEntity = userLogPersistenceMapper.toEntity(log);
			logEntity.setUser(entity);
			entity.getLogs().add(logEntity);
		}
	}

}
