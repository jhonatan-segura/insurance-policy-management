package com.policy.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.policy.store.dtos.RiskDto;
import com.policy.store.entities.Risk;

@Mapper(componentModel = "spring")
public interface RiskMapper {
   RiskDto toDto(Risk risk);
   Risk toEntity(RiskDto riskDto);
}