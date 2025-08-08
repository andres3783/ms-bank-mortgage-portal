package com.bank.mortgage.portal.repository;

import com.bank.mortgage.portal.dto.ApplicationStatus;
import com.bank.mortgage.portal.dto.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {

    Optional<Decision> findByApplicationId(UUID applicationId);

    List<Decision> findByOfficerId(UUID officerId);

    List<Decision> findByStatus(ApplicationStatus status);

    List<Decision> findByDecisionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT d FROM Decision d JOIN d.officer o WHERE o.username = :officerUsername")
    List<Decision> findByOfficerUsername(@Param("officerUsername") String officerUsername);

    @Query("SELECT COUNT(d) FROM Decision d WHERE d.officer.id = :officerId AND d.status = :status")
    long countByOfficerIdAndStatus(@Param("officerId") UUID officerId, @Param("status") ApplicationStatus status);
}
