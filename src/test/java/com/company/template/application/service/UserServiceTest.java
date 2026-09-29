package com.company.template.application.service;

import com.company.template.domain.exception.BusinessRuleException;
import com.company.template.domain.exception.ConflictException;
import com.company.template.domain.exception.NotFoundException;
import com.company.template.domain.model.User;
import com.company.template.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository);
	}

	@Test
	void createPersistsUserWithInitialLog() {
		User user = user();
		when(userRepository.existsByName(user.getName())).thenReturn(false);
		when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
		when(userRepository.save(user)).thenReturn(user);

		User created = userService.create(user);

		assertThat(created).isSameAs(user);
		assertThat(created.isActive()).isTrue();
		assertThat(created.getLogs()).hasSize(1);
		assertThat(created.getLogs().get(0).getLogMessage()).isEqualTo("User created");
	}

	@Test
	void createRejectsDuplicateName() {
		User user = user();
		when(userRepository.existsByName(user.getName())).thenReturn(true);

		assertThatThrownBy(() -> userService.create(user))
				.isInstanceOf(ConflictException.class)
				.hasFieldOrPropertyWithValue("code", "USER_ALREADY_EXISTS");
		verify(userRepository, never()).save(any());
	}

	@Test
	void createRejectsDuplicateEmail() {
		User user = user();
		when(userRepository.existsByName(user.getName())).thenReturn(false);
		when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

		assertThatThrownBy(() -> userService.create(user))
				.isInstanceOf(ConflictException.class)
				.hasFieldOrPropertyWithValue("code", "EMAIL_ALREADY_EXISTS");
	}

	@Test
	void getByIdRejectsMissingUser() {
		UUID id = UUID.randomUUID();
		when(userRepository.findByIdWithLogs(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getById(id))
				.isInstanceOf(NotFoundException.class)
				.hasFieldOrPropertyWithValue("code", "USER_NOT_FOUND");
	}

	@Test
	void updateRejectsInactiveUser() {
		UUID id = UUID.randomUUID();
		User user = user();
		user.setActive(false);
		when(userRepository.findByIdWithLogs(id)).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userService.update(id, user()))
				.isInstanceOf(BusinessRuleException.class)
				.hasFieldOrPropertyWithValue("code", "USER_INACTIVE");
	}

	@Test
	void updateCopiesFieldsAndAppendsLog() {
		UUID id = UUID.randomUUID();
		User user = user();
		user.setId(id);
		user.setActive(true);
		User changes = user();
		changes.setName("Ana Ruiz");
		changes.setEmail("ana@example.com");
		when(userRepository.findByIdWithLogs(id)).thenReturn(Optional.of(user));
		when(userRepository.existsByNameAndIdNot("Ana Ruiz", id)).thenReturn(false);
		when(userRepository.existsByEmailAndIdNot("ana@example.com", id)).thenReturn(false);
		when(userRepository.save(user)).thenReturn(user);

		User updated = userService.update(id, changes);

		assertThat(updated.getName()).isEqualTo("Ana Ruiz");
		assertThat(updated.getEmail()).isEqualTo("ana@example.com");
		assertThat(updated.getLogs()).hasSize(1);
		assertThat(updated.getLogs().get(0).getLogMessage()).isEqualTo("User updated");
	}

	@Test
	void deactivateRejectsUserAlreadyInactive() {
		UUID id = UUID.randomUUID();
		User user = user();
		user.setActive(false);
		when(userRepository.findByIdWithLogs(id)).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userService.deactivate(id))
				.isInstanceOf(BusinessRuleException.class)
				.hasFieldOrPropertyWithValue("code", "USER_ALREADY_INACTIVE");
	}

	private User user() {
		User user = new User();
		user.setName("Maria Lopez");
		user.setAddress("Calle 10");
		user.setAge(28);
		user.setEmail("maria@example.com");
		user.setPhoneNumber("3001234567");
		return user;
	}

}
