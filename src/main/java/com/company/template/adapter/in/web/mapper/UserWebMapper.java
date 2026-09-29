package com.company.template.adapter.in.web.mapper;

import com.company.template.adapter.in.web.dto.request.UserCreateRequest;
import com.company.template.adapter.in.web.dto.request.UserUpdateRequest;
import com.company.template.adapter.in.web.dto.response.UserResponse;
import com.company.template.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = UserLogWebMapper.class,
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserWebMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "logs", ignore = true)
	User toDomain(UserCreateRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "active", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "logs", ignore = true)
	User toDomain(UserUpdateRequest request);

	UserResponse toResponse(User user);

}
