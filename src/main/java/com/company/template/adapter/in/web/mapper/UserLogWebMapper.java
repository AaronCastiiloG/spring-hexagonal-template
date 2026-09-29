package com.company.template.adapter.in.web.mapper;

import com.company.template.adapter.in.web.dto.response.UserLogResponse;
import com.company.template.domain.model.UserLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserLogWebMapper {

	UserLogResponse toResponse(UserLog log);

}
