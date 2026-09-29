package com.company.template.adapter.out.persistence.mapper;

import com.company.template.adapter.out.persistence.entity.RefreshTokenEntity;
import com.company.template.domain.model.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		uses = AccountPersistenceMapper.class,
		unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RefreshTokenPersistenceMapper {

	@Mapping(target = "account", ignore = true)
	RefreshTokenEntity toEntity(RefreshToken refreshToken);

	RefreshToken toDomain(RefreshTokenEntity entity);

}
