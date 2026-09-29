package com.company.template.adapter.out.persistence;

import com.company.template.adapter.out.persistence.entity.UserEntity;
import com.company.template.adapter.out.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class UserRepositoryTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

	@Autowired
	private UserJpaRepository userRepository;

	@Test
	void rejectsDuplicateName() {
		userRepository.saveAndFlush(newUser("Maria Lopez", "maria@example.com"));

		assertThatThrownBy(() -> userRepository.saveAndFlush(newUser("Maria Lopez", "other@example.com")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void rejectsDuplicateEmail() {
		userRepository.saveAndFlush(newUser("Maria Lopez", "maria@example.com"));

		assertThatThrownBy(() -> userRepository.saveAndFlush(newUser("Ana Torres", "maria@example.com")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	private UserEntity newUser(String name, String email) {
		UserEntity user = new UserEntity();
		user.setName(name);
		user.setEmail(email);
		user.setPhoneNumber("3001234567");
		user.setAge(30);
		user.setAddress("Calle 10");
		return user;
	}

}
