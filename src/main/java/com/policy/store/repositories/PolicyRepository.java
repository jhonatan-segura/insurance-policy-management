package com.policy.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.policy.store.entities.Policy;


public interface PolicyRepository extends JpaRepository<Policy, Long> {
}