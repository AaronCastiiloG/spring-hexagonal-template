package com.company.template.domain.port;

import com.company.template.domain.model.Account;

import java.util.Optional;

public interface AccountRepository {

	Optional<Account> findByUsername(String username);

	Account save(Account account);

}
