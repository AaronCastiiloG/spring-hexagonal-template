package com.company.template.adapter.out.persistence.mapper;

import com.company.template.adapter.out.persistence.entity.UserLogEntity;
import com.company.template.domain.model.UserLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserLogPersistenceMapper {

	@Mapping(target = "user", ignore = true)
	UserLogEntity toEntity(UserLog log);

	UserLog toDomain(UserLogEntity entity);

}
