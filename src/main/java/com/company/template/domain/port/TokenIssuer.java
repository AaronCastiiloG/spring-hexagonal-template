package com.company.template.domain.port;

import com.company.template.domain.model.Account;
import com.company.template.domain.model.IssuedTokenPair;

import java.util.Optional;

public interface TokenIssuer {

	IssuedTokenPair issue(Account account);

	Optional<String> refreshSubject(String refreshToken);

}
