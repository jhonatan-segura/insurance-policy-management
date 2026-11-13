package com.policy.store.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.policy.store.entities.PolicyType;

public interface PolicyTypeRepository
        extends JpaRepository<PolicyType, Long> {
}