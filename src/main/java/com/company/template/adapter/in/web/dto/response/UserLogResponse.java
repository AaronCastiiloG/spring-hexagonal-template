package com.company.template.adapter.in.web.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserLogResponse {

	private UUID id;
	private LocalDateTime createdAt;
	private String logMessage;

}
