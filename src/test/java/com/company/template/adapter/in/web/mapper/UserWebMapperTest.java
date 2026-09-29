package com.company.template.adapter.in.web.mapper;

import com.company.template.adapter.in.web.dto.request.UserCreateRequest;
import com.company.template.adapter.in.web.dto.request.UserUpdateRequest;
import com.company.template.adapter.in.web.dto.response.UserResponse;
import com.company.template.domain.model.User;
import com.company.template.domain.model.UserLog;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserWebMapperTest {

	private final UserWebMapper userMapper = mapper();

	private static UserWebMapper mapper() {
		UserWebMapperImpl mapper = new UserWebMapperImpl();
		ReflectionTestUtils.setField(mapper, "userLogWebMapper", new UserLogWebMapperImpl());
		return mapper;
	}

	@Test
	void mapsEveryResponseField() {
		UUID userId = UUID.randomUUID();
		UUID logId = UUID.randomUUID();
		LocalDateTime createdAt = LocalDateTime.of(2026, 1, 2, 3, 4);
		LocalDateTime updatedAt = LocalDateTime.of(2026, 2, 3, 4, 5);

		UserLog log = new UserLog();
		log.setId(logId);
		log.setCreatedAt(createdAt);
		log.setLogMessage("User created");

		User user = new User();
		user.setId(userId);
		user.setName("Maria Lopez");
		user.setAddress("Calle 10");
		user.setActive(true);
		user.setAge(28);
		user.setEmail("maria@example.com");
		user.setPhoneNumber("3001234567");
		user.setCreatedAt(createdAt);
		user.setUpdatedAt(updatedAt);
		user.setLogs(List.of(log));

		UserResponse response = userMapper.toResponse(user);

		assertThat(response.getId()).isEqualTo(userId);
		assertThat(response.getName()).isEqualTo("Maria Lopez");
		assertThat(response.getAddress()).isEqualTo("Calle 10");
		assertThat(response.isActive()).isTrue();
		assertThat(response.getAge()).isEqualTo(28);
		assertThat(response.getEmail()).isEqualTo("maria@example.com");
		assertThat(response.getPhoneNumber()).isEqualTo("3001234567");
		assertThat(response.getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
		assertThat(response.getLogs()).hasSize(1);
		assertThat(response.getLogs().get(0).getId()).isEqualTo(logId);
		assertThat(response.getLogs().get(0).getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getLogs().get(0).getLogMessage()).isEqualTo("User created");
	}

	@Test
	void mapsRequestWithoutServerFields() {
		UserCreateRequest request = new UserCreateRequest();
		request.setName("Maria Lopez");
		request.setAddress("Calle 10");
		request.setAge(28);
		request.setEmail("maria@example.com");
		request.setPhoneNumber("3001234567");

		User user = userMapper.toDomain(request);

		assertThat(user.getId()).isNull();
		assertThat(user.getName()).isEqualTo("Maria Lopez");
		assertThat(user.getAddress()).isEqualTo("Calle 10");
		assertThat(user.getAge()).isEqualTo(28);
		assertThat(user.getEmail()).isEqualTo("maria@example.com");
		assertThat(user.getPhoneNumber()).isEqualTo("3001234567");
		assertThat(user.isActive()).isFalse();
		assertThat(user.getCreatedAt()).isNull();
		assertThat(user.getUpdatedAt()).isNull();
		assertThat(user.getLogs()).isEmpty();

		UserUpdateRequest update = new UserUpdateRequest();
		update.setName("Ana Ruiz");
		update.setAddress("Calle 20");
		update.setAge(30);
		update.setEmail("ana@example.com");
		update.setPhoneNumber("3007654321");
		User target = userMapper.toDomain(update);

		assertThat(target.getName()).isEqualTo("Ana Ruiz");
		assertThat(target.getEmail()).isEqualTo("ana@example.com");
		assertThat(target.getId()).isNull();
		assertThat(target.isActive()).isFalse();
	}

}
