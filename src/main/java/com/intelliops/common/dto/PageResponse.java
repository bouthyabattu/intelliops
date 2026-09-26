package com.intelliops.common.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated response wrapper for list endpoints.
 */
@Getter
@Builder
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;
    private final boolean first;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return from(page);
    }
}
