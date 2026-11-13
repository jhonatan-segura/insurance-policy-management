package com.policy.store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.policy.store.entities.PolicyStatus;

public interface PolicyStatusRepository
      extends JpaRepository<PolicyStatus, Long> {
   Optional<PolicyStatus> findByName(String name);
}