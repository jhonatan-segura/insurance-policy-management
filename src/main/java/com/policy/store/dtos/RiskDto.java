package com.policy.store.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RiskDto {
   private Long id;
   private String name;
   private String description;
   private BigDecimal insuredValue;
   private boolean active;
}