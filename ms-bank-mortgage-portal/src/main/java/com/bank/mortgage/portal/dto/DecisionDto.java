package com.bank.mortgage.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionDto {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "Status must be either APPROVED or REJECTED")
    private String status;

    @Size(max = 2000, message = "Comments cannot exceed 2000 characters")
    private String comments;
}
