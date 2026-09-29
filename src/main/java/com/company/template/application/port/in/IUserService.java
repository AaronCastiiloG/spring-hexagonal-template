package com.company.template.application.port.in;

import com.company.template.domain.model.PageSlice;
import com.company.template.domain.model.User;

import java.util.UUID;

public interface IUserService {

	User create(User user);

	PageSlice<User> getAll(int page, int size);

	User getById(UUID id);

	User update(UUID id, User changes);

	User deactivate(UUID id);

	User activate(UUID id);

	void delete(UUID id);

}
