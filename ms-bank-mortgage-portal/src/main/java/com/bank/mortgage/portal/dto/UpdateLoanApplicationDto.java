package com.bank.mortgage.portal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLoanApplicationDto {

    @NotNull(message = "Loan type is required")
    private String  loanTypeId;

    @NotNull(message = "National Id is required")
    private String nationalId;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "50000.00", message = "Loan amount must be at least KES 50,000")
    @DecimalMax(value = "100000000.00", message = "Loan amount cannot exceed KES 100,000,000")
    private BigDecimal loanAmount;

    @Size(max = 255, message = "Loan purpose cannot exceed 255 characters")
    private String loanPurpose;
}