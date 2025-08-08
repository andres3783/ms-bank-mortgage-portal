package com.bank.mortgage.portal.repository;

import com.bank.mortgage.portal.dto.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, UUID> {
    List<LoanType> findByIsActiveTrueOrderByName();
}
