package com.policy.store.dtos;

import com.policy.store.entities.PolicyStatus;
import com.policy.store.entities.PolicyType;

import lombok.Data;

@Data
public class PolicyDto {
   private Long id;
   private String policyNumber;
   private Long typeId;
   private String typeName;

   private Long statusId;
   private String statusName;
}