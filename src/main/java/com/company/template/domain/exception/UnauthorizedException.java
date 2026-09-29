package com.company.template.domain.exception;

public class UnauthorizedException extends ApiException {

	public UnauthorizedException(String code, String message) {
		super(code, message);
	}

}
