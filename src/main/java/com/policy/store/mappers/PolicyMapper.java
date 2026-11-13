package com.policy.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.policy.store.dtos.PolicyDto;
import com.policy.store.entities.Policy;

@Mapper(componentModel = "spring")
public interface PolicyMapper {
   @Mapping(target = "typeId", source = "type.id")
   @Mapping(target = "typeName", source = "type.name")
   @Mapping(target = "statusId", source = "status.id")
   @Mapping(target = "statusName", source = "status.name")
   PolicyDto toDto(Policy policy);

   Policy toEntity(PolicyDto productDto);

}