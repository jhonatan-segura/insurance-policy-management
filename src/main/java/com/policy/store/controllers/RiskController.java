package com.policy.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.policy.store.dtos.RiskDto;
import com.policy.store.mappers.RiskMapper;
import com.policy.store.repositories.RiskRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/riesgos")
@RequiredArgsConstructor
public class RiskController {

   private final RiskRepository riskRepository;
   private final RiskMapper riskMapper;

   @PostMapping("/{id}/cancelar")
   public ResponseEntity<RiskDto> cancelRisk(
         @PathVariable Long id) {
      var risk = riskRepository.findById(id)
            .orElse(null);
      if (risk == null) {
         return ResponseEntity.notFound().build();
      }

      if (Boolean.FALSE.equals(risk.getActive())) {
         throw new ResponseStatusException(
               HttpStatus.CONFLICT,
               "El riesgo ya está cancelado");
      }

      risk.setActive(false);

      var savedRisk = riskRepository.save(risk);

      return ResponseEntity.ok(riskMapper.toDto(savedRisk));
   }
}
