package com.bank.mortgage.portal.repository;

import com.bank.mortgage.portal.dto.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByApplicationId(UUID applicationId);

    List<Document> findByFileType(String fileType);

    @Query("SELECT d FROM Document d WHERE d.application.id = :applicationId ORDER BY d.uploadedAt DESC")
    List<Document> findByApplicationIdOrderByUploadedAtDesc(@Param("applicationId") UUID applicationId);

    void deleteByApplicationId(UUID applicationId);
}