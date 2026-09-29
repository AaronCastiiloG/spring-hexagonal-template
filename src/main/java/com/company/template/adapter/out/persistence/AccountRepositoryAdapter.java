package com.company.template.adapter.out.persistence;

import com.company.template.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.company.template.adapter.out.persistence.repository.AccountJpaRepository;
import com.company.template.domain.model.Account;
import com.company.template.domain.port.AccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class AccountRepositoryAdapter implements AccountRepository {

	private final AccountJpaRepository accountJpaRepository;
	private final AccountPersistenceMapper accountPersistenceMapper;

	public AccountRepositoryAdapter(
			AccountJpaRepository accountJpaRepository,
			AccountPersistenceMapper accountPersistenceMapper
	) {
		this.accountJpaRepository = accountJpaRepository;
		this.accountPersistenceMapper = accountPersistenceMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Account> findByUsername(String username) {
		return accountJpaRepository.findByUsername(username).map(accountPersistenceMapper::toDomain);
	}

	@Override
	public Account save(Account account) {
		return accountPersistenceMapper.toDomain(
				accountJpaRepository.save(accountPersistenceMapper.toEntity(account))
		);
	}

}
