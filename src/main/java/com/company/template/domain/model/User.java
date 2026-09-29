package com.company.template.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {

	private UUID id;
	private String name;
	private String address;
	private boolean active;
	private int age;
	private String email;
	private String phoneNumber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<UserLog> logs = new ArrayList<>();

}
