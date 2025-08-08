package com.bank.mortgage.portal.dto;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApplicationMapper {

    public ApplicationResponseDto toResponseDto(Application application, User applicant, LoanType loanType) {
        if (application == null) {
            return null;
        }

        return ApplicationResponseDto.builder()
                .id(application.getId())
                .nationalId(applicant.getNationalId())
                .firstName(applicant.getFirstName())
                .lastName(applicant.getLastName())
                .email(applicant.getEmail())
                //.phoneNumber(applicant.getNationalId())
                .monthlyIncome(application.getMonthlyIncome())
                .loanAmount(application.getLoanAmount())
                .status(application.getStatus().name())
                .decision(mapDecision(application.getDecision()))
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .version(application.getVersion())
                .build();
    }

    /*public List<ApplicationResponseDto> toResponseDtoList(List<Application> applications) {
        if (applications == null) {
            return null;
        }

        return applications.stream()
                .map(application -> toResponseDto(application, applicant, loanType))
                .collect(Collectors.toList());
    }*/

    private List<DocumentResponseDto> mapDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return null;
        }

        return documents.stream()
                .map(this::mapDocument)
                .collect(Collectors.toList());
    }

    private DocumentResponseDto mapDocument(Document document) {
        if (document == null) {
            return null;
        }

        return DocumentResponseDto.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .s3PresignedUrl(document.getS3PresignedUrl())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    private DecisionResponseDto mapDecision(Decision decision) {
        if (decision == null) {
            return null;
        }

        String officerName = decision.getOfficer() != null ?
                decision.getOfficer().getFirstName() + " " + decision.getOfficer().getLastName() : null;

        return DecisionResponseDto.builder()
                .id(decision.getId())
                .officerName(officerName)
                .status(decision.getStatus().name())
                .comments(decision.getComments())
                .decisionDate(decision.getDecisionDate())
                .build();
    }
}