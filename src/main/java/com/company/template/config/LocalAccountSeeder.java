package com.company.template.config;

import com.company.template.domain.model.Account;
import com.company.template.domain.model.Role;
import com.company.template.domain.port.AccountRepository;
import com.company.template.domain.port.PasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalAccountSeeder implements ApplicationRunner {

	private final AccountRepository accountRepository;
	private final PasswordHasher passwordHasher;
	private final SeedProperties seedProperties;

	@Override
	public void run(ApplicationArguments args) {
		if (!seedProperties.enabled()) {
			return;
		}
		createIfMissing(seedProperties.adminUsername(), seedProperties.adminPassword(), Role.ADMIN);
		createIfMissing(seedProperties.userUsername(), seedProperties.userPassword(), Role.USER);
	}

	private void createIfMissing(String username, String password, Role role) {
		if (accountRepository.findByUsername(username).isPresent()) {
			return;
		}
		Account account = new Account();
		account.setUsername(username);
		account.setPasswordHash(passwordHasher.hash(password));
		account.setRoles(new HashSet<>(Set.of(role)));
		accountRepository.save(account);
		log.info("Seeded local account {}", username);
	}

}
