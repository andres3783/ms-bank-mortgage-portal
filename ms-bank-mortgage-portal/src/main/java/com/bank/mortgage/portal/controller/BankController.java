package com.bank.mortgage.portal.controller;

import com.bank.mortgage.portal.dto.*;
import com.bank.mortgage.portal.service.ApplicationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mortgage Applications", description = "APIs for managing mortgage applications")
@SecurityRequirement(name = "bearerAuth")
public class BankController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create new mortgage application", description = "Submit a new mortgage application")
    @PreAuthorize("hasAuthority('ROLE_APPLICANT')")
    public ResponseEntity<ApplicationResponseDto> createApplication(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication) throws JsonProcessingException {

        log.info("Creating new application for user: {}", authentication.getName());

        ApplicationResponseDto response = applicationService.createApplication(request, authentication);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID", description = "Fetch a specific mortgage application by its ID")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('APPLICANT') and @applicationService.isOwner(#id, authentication.name))")
    public ResponseEntity<ApplicationResponseDto> getApplication(
            @Parameter(description = "Application ID") @PathVariable UUID id,
            Authentication authentication) {
        log.info("Fetching application {} for user: {}", id, authentication.getName());
        ApplicationResponseDto response = applicationService.getApplication(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(summary = "List applications", description = "List and filter mortgage applications with pagination")
    @PreAuthorize("hasRole('OFFICER') or hasRole('APPLICANT')")
    public ResponseEntity<PagedResponse> listApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @PageableDefault(size = 20, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        log.info("Listing applications with filters - status: {}, nationalId: {}, user: {}", status, nationalId, authentication.getName());
        PagedResponse response = applicationService.listApplications(status, nationalId, createdFrom, createdTo, pageable, authentication);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/decisions")
    @Operation(summary = "Update application decision", description = "Applicant updates decision before finalization")
    @PreAuthorize("hasRole('APPLICANT') and @applicationService.isOwner(#id, authentication.name) and @applicationService.canUpdate(#id)")
    public ResponseEntity<ApplicationResponseDto> updateApplication(
            @PathVariable UUID id,
            @Valid @RequestBody DecisionDto request,
            Authentication authentication) throws JsonProcessingException {
        log.info("Updating application {} for user: {}", id, authentication.getName());
        ApplicationResponseDto response = applicationService.updateApplication(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete application", description = "Delete a mortgage application (applicant only, before decision)")
    @PreAuthorize("hasRole('APPLICANT') and @applicationService.isOwner(#id, authentication.name) and @applicationService.canDelete(#id)")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable UUID id,
            Authentication authentication) throws JsonProcessingException {
        log.info("Deleting application {} for user: {}", id, authentication.getName());
        applicationService.deleteApplication(id, authentication);
        return ResponseEntity.noContent().build();
    }


}