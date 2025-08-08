package com.bank.mortgage.portal.service;


import com.bank.mortgage.portal.component.JacksonConfig;
import com.bank.mortgage.portal.component.MessageSender;
import com.bank.mortgage.portal.dto.*;
import com.bank.mortgage.portal.exception.BadRequestException;
import com.bank.mortgage.portal.exception.UnauthorizedException;
import com.bank.mortgage.portal.repository.ApplicationRepository;
import com.bank.mortgage.portal.repository.LoanTypeRepository;
import com.bank.mortgage.portal.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final UserRepository userRepository;
    private final LoanTypeRepository loanTypeRepository;

    @Autowired
    private final JacksonConfig jacksonConfig;

    private final MessageSender sender;
    ObjectMapper mapper = new ObjectMapper();
    public ApplicationResponseDto createApplication(@Valid LoanApplicationRequest request, Authentication authentication) throws JsonProcessingException {
        log.info("Creating application for user: {}", authentication.getName());

        // Find user by authentication username
        User applicant = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Validate loan type exists
        LoanType loanType = loanTypeRepository.findById(request.getLoanTypeId())
                .orElseThrow(() -> new BadRequestException("Loan type not found"));

        // Validate loan amount within loan type limits
        if (request.getLoanAmount().compareTo(loanType.getMinAmount()) < 0 ||
                request.getLoanAmount().compareTo(loanType.getMaxAmount()) > 0) {
            throw new BadRequestException(String.format(
                    "Loan amount must be between %s and %s for loan type %s",
                    loanType.getMinAmount(), loanType.getMaxAmount(), loanType.getName()));
        }

        // Create application with all fields from request
        Application application = Application.builder()
                .applicant(applicant)
                .loanType(loanType) // This should now be required
                .loanAmount(request.getLoanAmount())
                .monthlyIncome(BigDecimal.ONE)
                .status(ApplicationStatus.PENDING)
                .build();

        Application saved = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", saved.getId());
//        sender.send("loan.applications", jacksonConfig.objectMapper().writeValueAsString(application));
        return applicationMapper.toResponseDto(saved,applicant,loanType);
    }


    public ApplicationResponseDto updateApplication(UUID id, @Valid DecisionDto request, Authentication auth) throws JsonProcessingException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        if (!ApplicationStatus.PENDING.equals(application.getStatus())) {
            throw new IllegalStateException("Only PENDING applications can be updated.");
        }

        application.makeDecision(request,application, auth.getName());
        application.setUpdatedAt(LocalDateTime.now()); // Not necessary if @UpdateTimestamp works

        applicationRepository.save(application);
//        sender.send("loan.applications", mapper.writeValueAsString(application));
        return applicationMapper.toResponseDto(application, application.getApplicant(), application.getLoanType());
    }




    public void deleteApplication(UUID id, Authentication auth) throws JsonProcessingException {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        if (!"PENDING".equalsIgnoreCase(String.valueOf(application.getStatus()))) {
            throw new IllegalStateException("Only PENDING applications can be deleted.");
        }
//        sender.send("loan.applications", mapper.writeValueAsString(application));
        applicationRepository.delete(application);
    }

    public ApplicationResponseDto getApplication(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        return applicationMapper.toResponseDto(application, application.getApplicant(), application.getLoanType());
    }


    public PagedResponse listApplications(String status, String nationalId,
                                                                  LocalDateTime createdFrom, LocalDateTime createdTo,
                                                                  Pageable pageable, Authentication authentication) {
        // Build the specification or criteria query
        Specification<Application> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (nationalId != null && !nationalId.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("applicant").get("nationalId"), nationalId));
        }

        if (createdFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
        }

        if (createdTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), createdTo));
        }

        // Authorization: if user is applicant, restrict to their own applications
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_APPLICANT"))) {
            String username = authentication.getName(); // assuming username = nationalId or linked field
            spec = spec.and((root, query, cb) -> cb.equal(root.get("applicant").get("username"), username));
        }

        Page<Application> applicationPage = applicationRepository.findAll(spec, pageable);

        List<ApplicationResponseDto> dtoList = applicationPage.getContent()
                .stream()
                .map(app -> applicationMapper.toResponseDto(app, app.getApplicant(), app.getLoanType()))
                .collect(Collectors.toList());

       return PagedResponse.builder()
                .content(dtoList)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(applicationPage.getTotalElements())
                .totalPages(applicationPage.getTotalPages())
                .last( applicationPage.isLast())
                .build();

    }

    public boolean isOwner(UUID id, String username) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        return application.getApplicant().getUsername().equals(username);
    }

    public boolean canDelete(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        return application.getStatus().equals("DRAFT"); // example condition
    }

    public boolean canUpdate(UUID id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
        return app.getStatus() == ApplicationStatus.PENDING;
    }



}