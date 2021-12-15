package com.recipebook.domain.values;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author - AvanishKishorPandey
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PageResponse<D> {
    private long totalItems;
    private long totalPages;
    private long currentPage;
    @ToString.Exclude
    private List<D> contents;

    public static <D, E> PageResponse<D> of(final Page<E> pageResult, final Function<E, D> converterFunction) {
        List<D> contents = pageResult.stream().map(converterFunction).collect(Collectors.toList());
        return new PageResponse<>(pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getNumber(), contents);
    }
}
