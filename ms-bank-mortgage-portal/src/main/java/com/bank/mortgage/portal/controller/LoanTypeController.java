package com.bank.mortgage.portal.controller;

import com.bank.mortgage.portal.dto.LoanTypeDto;
import com.bank.mortgage.portal.service.LoanTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-types")
@RequiredArgsConstructor
@Tag(name = "Loan Types", description = "Mortgage loan products")
public class LoanTypeController {

    private final LoanTypeService loanTypeService;

    @GetMapping
    @Operation(summary = "Get all active loan types", description = "Fetch all available mortgage loan products")
    public ResponseEntity<List<LoanTypeDto>> getLoanTypes() {
        return ResponseEntity.ok(loanTypeService.getActiveLoanTypes());
    }
}