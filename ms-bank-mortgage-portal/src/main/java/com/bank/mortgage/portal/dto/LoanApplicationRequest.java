package com.bank.mortgage.portal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotNull(message = "Loan type is required")
    private UUID loanTypeId;

    @NotNull(message = "National Id is required")
    private String nationalId;

    @NotNull(message = "Loan amount is required")
    private BigDecimal loanAmount;

    @Size(max = 255, message = "Loan purpose cannot exceed 255 characters")
    private String loanPurpose;
}