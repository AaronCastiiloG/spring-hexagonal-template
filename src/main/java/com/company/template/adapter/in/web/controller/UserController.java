package com.company.template.adapter.in.web.controller;

import com.company.template.adapter.in.web.dto.request.UserCreateRequest;
import com.company.template.adapter.in.web.dto.request.UserUpdateRequest;
import com.company.template.adapter.in.web.dto.response.UserPageResponse;
import com.company.template.adapter.in.web.dto.response.UserResponse;
import com.company.template.adapter.in.web.mapper.UserWebMapper;
import com.company.template.application.port.in.IUserService;
import com.company.template.domain.model.PageSlice;
import com.company.template.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final IUserService userService;
	private final UserWebMapper userWebMapper;

	@GetMapping
	public ResponseEntity<UserPageResponse> getUsers(@PageableDefault(size = 10) Pageable pageable) {
		PageSlice<User> page = userService.getAll(pageable.getPageNumber(), pageable.getPageSize());
		UserPageResponse response = new UserPageResponse(
				page.content().stream().map(userWebMapper::toResponse).toList(),
				page.totalElements(),
				page.totalPages(),
				page.number(),
				page.size()
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
		return ResponseEntity.ok(userWebMapper.toResponse(userService.getById(id)));
	}

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
		UserResponse response = userWebMapper.toResponse(userService.create(userWebMapper.toDomain(request)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
		UserResponse response = userWebMapper.toResponse(userService.update(id, userWebMapper.toDomain(request)));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/deactivation")
	public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
		return ResponseEntity.ok(userWebMapper.toResponse(userService.deactivate(id)));
	}

	@PostMapping("/{id}/activation")
	public ResponseEntity<UserResponse> activateUser(@PathVariable UUID id) {
		return ResponseEntity.ok(userWebMapper.toResponse(userService.activate(id)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
