package com.bank.mortgage.portal.dto;

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
public class LoanTypeDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal minInterestRate;
    private BigDecimal maxInterestRate;
    private Integer minTermMonths;
    private Integer maxTermMonths;
    private BigDecimal processingFeeRate;
}
