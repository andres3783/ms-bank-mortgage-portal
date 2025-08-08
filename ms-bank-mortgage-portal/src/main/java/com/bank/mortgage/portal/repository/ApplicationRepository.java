package com.bank.mortgage.portal.repository;

import com.bank.mortgage.portal.dto.Application;
import com.bank.mortgage.portal.dto.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {

    // Find by applicant username
    Page<Application> findByApplicantUsername(String username, Pageable pageable);

    // Find by status
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    // Find by date range
    List<Application> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Check if user owns application
    boolean existsByIdAndApplicantUsername(UUID id, String username);

    // Find applications with decisions
    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.decision WHERE a.status IN :statuses")
    List<Application> findApplicationsWithDecisions(@Param("statuses") List<ApplicationStatus> statuses);

    // Count applications by status
    long countByStatus(ApplicationStatus status);

    // Custom query for complex filtering
    /*@Query("SELECT a FROM Application a JOIN a.applicant u WHERE " +
            "(:nationalId IS NULL OR a.nationalId = :nationalId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:username IS NULL OR u.username = :username)")
    Page<Application> findWithFilters(@Param("nationalId") String nationalId,
                                      @Param("status") ApplicationStatus status,
                                      @Param("username") String username,
                                      Pageable pageable);*/
}