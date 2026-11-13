package com.policy.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.policy.store.entities.Risk;

public interface RiskRepository extends JpaRepository<Risk, Long> {
}