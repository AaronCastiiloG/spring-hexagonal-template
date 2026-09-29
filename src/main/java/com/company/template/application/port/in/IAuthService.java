package com.company.template.application.port.in;

import com.company.template.domain.model.IssuedTokenPair;

public interface IAuthService {

	IssuedTokenPair login(String username, String password);

	IssuedTokenPair refresh(String refreshToken);

	void logout(String refreshToken);

}
