package com.company.template.adapter.in.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorResponse {

	private final String field;
	private final String message;

}
