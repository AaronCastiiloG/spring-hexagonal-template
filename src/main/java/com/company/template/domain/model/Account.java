package com.company.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Account {

	private UUID id;
	private String username;
	private String passwordHash;
	private Set<Role> roles = new HashSet<>();
	private LocalDateTime createdAt;

}
