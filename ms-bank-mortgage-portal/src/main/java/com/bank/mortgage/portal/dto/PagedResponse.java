package com.bank.mortgage.portal.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse {
    private List<ApplicationResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

}
