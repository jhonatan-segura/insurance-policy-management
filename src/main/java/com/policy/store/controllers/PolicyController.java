package com.policy.store.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.policy.store.dtos.PolicyDto;
import com.policy.store.dtos.RiskDto;
import com.policy.store.entities.Risk;
import com.policy.store.mappers.PolicyMapper;
import com.policy.store.mappers.RiskMapper;
import com.policy.store.repositories.PolicyRepository;
import com.policy.store.repositories.PolicyStatusRepository;
import com.policy.store.repositories.RiskRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/polizas")
public class PolicyController {
   @Value("${app.security.api-key}")
   private String expectedApiKey;
   private final PolicyRepository policyRepository;
   private final PolicyMapper policyMapper;
   private final RiskMapper riskMapper;
   private final PolicyStatusRepository policyStatusRepository;
   private final RiskRepository riskRepository;

   @GetMapping
   public Iterable<PolicyDto> getAllPolicies(
         @RequestHeader(name = "x-api-key") String apiKey,
         @RequestParam(required = false, defaultValue = "") Byte categoryId) {
      if (!expectedApiKey.equals(apiKey)) {
         throw new ResponseStatusException(
               HttpStatus.UNAUTHORIZED,
               "Invalid API Key");
      }

      return policyRepository.findAll()
            .stream()
            .map(policyMapper::toDto)
            .toList();
   }

   @GetMapping("/{id}/riesgos")
   public ResponseEntity<List<RiskDto>> getPolicyRisks(@PathVariable Long id) {
      var policy = policyRepository.findById(id).orElse(null);
      if (policy == null) {
         return ResponseEntity.notFound().build();
      }

      List<RiskDto> risks = policy.getRisks()
            .stream()
            .map(riskMapper::toDto)
            .toList();

      return ResponseEntity.ok(risks);
   }

   @PostMapping("/{id}/renovar")
   public ResponseEntity<PolicyDto> renewPolicy(
         @PathVariable Long id,
         UriComponentsBuilder uriComponentsBuilder) {
      var policy = policyRepository.findById(id).orElse(null);
      if (policy == null) {
         return ResponseEntity.notFound().build();
      }

      BigDecimal ipc = new BigDecimal("5.10");

      // Incrementar canon y prima
      policy.setRentalAmount(
            applyIpc(policy.getRentalAmount(), ipc));

      policy.setPremium(
            applyIpc(policy.getPremium(), ipc));

      var activeStatus = policyStatusRepository.findByName("RENOVADA")
            .orElseThrow(() -> new RuntimeException("Status RENOVADA no encontrado"));
      policy.setStatus(activeStatus);

      // Actualizar fechas de vigencia
      policy.setStartDate(LocalDate.now());
      policy.setEndDate(LocalDate.now().plusYears(1));

      policyRepository.save(policy);

      PolicyDto responseDto = policyMapper.toDto(policy);

      var uri = uriComponentsBuilder
            .path("/api/policies/{id}")
            .buildAndExpand(policy.getId())
            .toUri();

      return ResponseEntity.created(uri).body(responseDto);
   }

   private BigDecimal applyIpc(
         BigDecimal currentValue,
         BigDecimal ipcPercentage) {

      BigDecimal factor = BigDecimal.ONE.add(
            ipcPercentage.divide(
                  new BigDecimal("100"),
                  6,
                  RoundingMode.HALF_UP));

      return currentValue
            .multiply(factor)
            .setScale(2, RoundingMode.HALF_UP);
   }

   @PostMapping("/{id}/cancelar")
   @Transactional
   public ResponseEntity<PolicyDto> cancelPolicy(
         @PathVariable Long id) {

      var policy = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                  HttpStatus.NOT_FOUND,
                  "Póliza no encontrada"));

      if ("CANCELADA".equalsIgnoreCase(policy.getStatus().getName())) {
         throw new ResponseStatusException(
               HttpStatus.CONFLICT,
               "La póliza ya está cancelada");
      }

      var cancelledStatus = policyStatusRepository
            .findByName("CANCELADA")
            .orElseThrow(() -> new RuntimeException(
                  "Estado CANCELLED no encontrado"));

      policy.setStatus(cancelledStatus);

      var savedPolicy = policyRepository.save(policy);

      return ResponseEntity.ok(policyMapper.toDto(savedPolicy));
   }

   @PostMapping("/{id}/riesgos")
   @Transactional
   public ResponseEntity<RiskDto> addRisk(
         @PathVariable Long id,
         @RequestBody RiskDto riskDto,
         UriComponentsBuilder uriBuilder) {

      var policy = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                  HttpStatus.NOT_FOUND,
                  "Póliza no encontrada"));

      if (!"COLLECTIVE".equalsIgnoreCase(
            policy.getType().getName())) {

         throw new ResponseStatusException(
               HttpStatus.BAD_REQUEST,
               "Solo se pueden agregar riesgos a pólizas colectivas");
      }

      var risk = Risk.builder()
            .name(riskDto.getName())
            .description(riskDto.getDescription())
            .insuredValue(riskDto.getInsuredValue())
            .active(true)
            .build();

      policy.addRisk(risk);

      policyRepository.save(policy);

      RiskDto responseDto = riskMapper.toDto(risk);

      var uri = uriBuilder
            .path("/api/riesgos/{id}")
            .buildAndExpand(risk.getId())
            .toUri();

      return ResponseEntity.created(uri).body(responseDto);
   }
}
