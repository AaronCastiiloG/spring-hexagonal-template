package com.company.template.application.service;

import com.company.template.application.port.in.IUserService;
import com.company.template.domain.exception.BusinessRuleException;
import com.company.template.domain.exception.ConflictException;
import com.company.template.domain.exception.NotFoundException;
import com.company.template.domain.model.PageSlice;
import com.company.template.domain.model.User;
import com.company.template.domain.model.UserLog;
import com.company.template.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService implements IUserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public User create(User user) {
		if (userRepository.existsByName(user.getName())) {
			throw new ConflictException("USER_ALREADY_EXISTS", "A user with this name already exists");
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new ConflictException("EMAIL_ALREADY_EXISTS", "A user with this email already exists");
		}

		user.setActive(true);
		addLog(user, "User created");
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public PageSlice<User> getAll(int page, int size) {
		return userRepository.findAll(page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public User getById(UUID id) {
		return findUser(id);
	}

	@Override
	@Transactional
	public User update(UUID id, User changes) {
		User user = findUser(id);
		if (!user.isActive()) {
			throw new BusinessRuleException("USER_INACTIVE", "Inactive users cannot be updated");
		}
		if (userRepository.existsByNameAndIdNot(changes.getName(), id)) {
			throw new ConflictException("USER_ALREADY_EXISTS", "A user with this name already exists");
		}
		if (userRepository.existsByEmailAndIdNot(changes.getEmail(), id)) {
			throw new ConflictException("EMAIL_ALREADY_EXISTS", "A user with this email already exists");
		}
		user.setName(changes.getName());
		user.setAddress(changes.getAddress());
		user.setAge(changes.getAge());
		user.setEmail(changes.getEmail());
		user.setPhoneNumber(changes.getPhoneNumber());
		addLog(user, "User updated");
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User deactivate(UUID id) {
		User user = findUser(id);
		if (!user.isActive()) {
			throw new BusinessRuleException("USER_ALREADY_INACTIVE", "The user is already inactive");
		}
		user.setActive(false);
		addLog(user, "User deactivated");
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User activate(UUID id) {
		User user = findUser(id);
		if (user.isActive()) {
			throw new BusinessRuleException("USER_ALREADY_ACTIVE", "The user is already active");
		}
		user.setActive(true);
		addLog(user, "User activated");
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		User user = findUser(id);
		userRepository.delete(user);
	}

	private User findUser(UUID id) {
		return userRepository.findByIdWithLogs(id)
				.orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User was not found"));
	}

	private void addLog(User user, String message) {
		UserLog log = new UserLog();
		log.setLogMessage(message);
		user.getLogs().add(log);
	}

}
