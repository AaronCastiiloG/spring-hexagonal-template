package com.company.template.adapter.out.persistence.mapper;

import com.company.template.adapter.out.persistence.entity.UserEntity;
import com.company.template.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = UserLogPersistenceMapper.class,
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserPersistenceMapper {

	@Mapping(target = "logs", ignore = true)
	UserEntity toNewEntity(User user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "logs", ignore = true)
	void updateEntity(User user, @MappingTarget UserEntity entity);

	User toDomain(UserEntity entity);

}
