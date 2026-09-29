package com.company.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserLog {

	private UUID id;
	private LocalDateTime createdAt;
	private String logMessage;

}
