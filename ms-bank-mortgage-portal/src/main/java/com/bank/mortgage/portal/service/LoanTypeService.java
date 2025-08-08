package com.bank.mortgage.portal.service;

import com.bank.mortgage.portal.dto.LoanType;
import com.bank.mortgage.portal.dto.LoanTypeDto;
import com.bank.mortgage.portal.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanTypeService {

    private final LoanTypeRepository loanTypeRepository;

    public List<LoanTypeDto> getActiveLoanTypes() {
        return loanTypeRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private LoanTypeDto toDto(LoanType loanType) {
        return LoanTypeDto.builder()
                .id(loanType.getId())
                .name(loanType.getName())
                .description(loanType.getDescription())
                .minAmount(loanType.getMinAmount())
                .maxAmount(loanType.getMaxAmount())
                .minInterestRate(loanType.getMinInterestRate())
                .maxInterestRate(loanType.getMaxInterestRate())
                .minTermMonths(loanType.getMinTermMonths())
                .maxTermMonths(loanType.getMaxTermMonths())
                .processingFeeRate(loanType.getProcessingFeeRate())
                .build();
    }
}