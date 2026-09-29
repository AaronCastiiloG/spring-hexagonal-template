package com.company.template.adapter.out.persistence.mapper;

import com.company.template.adapter.out.persistence.entity.AccountEntity;
import com.company.template.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountPersistenceMapper {

	AccountEntity toEntity(Account account);

	Account toDomain(AccountEntity entity);

}
