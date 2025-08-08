package com.bank.mortgage.portal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {

    private UUID id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private BigDecimal monthlyIncome;
    private BigDecimal loanAmount;
    private String loanPurpose;
    private String employmentType;
    private String status;
    private List<DocumentResponseDto> documents;
    private DecisionResponseDto decision;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long version;
}