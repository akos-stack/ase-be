package com.bloxico.ase.userservice.web.model;

import com.bloxico.ase.userservice.validator.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class PageRequest {

    @NotNull
    @Min(0)
    @ApiParam(name = "page", required = true)
    Integer page;

    @NotNull
    @Min(1)
    @ApiParam(name = "size", required = true)
    Integer size;

    @NullOrNotBlank
    @ApiParam(name = "sort")
    String sort;

    @ApiParam(name = "order")
    Direction order;

    @JsonIgnore
    @SuppressWarnings("ConstantConditions")
    public Pageable toPageable() {
        return sort == null
                ? org.springframework.data.domain.PageRequest.of(page, size)
                : org.springframework.data.domain.PageRequest.of(page, size,
                Sort.by(order == null ? ASC : order, sort.split(",")));
    }

    @JsonIgnore
    @SuppressWarnings("ConstantConditions")
    public Pageable toPageableUnsafe() {
        return sort == null
                ? org.springframework.data.domain.PageRequest.of(page, size)
                : org.springframework.data.domain.PageRequest.of(page, size,
                JpaSort.unsafe(order == null ? ASC : order, toUnsafeSort(sort)));
    }

    private String[] toUnsafeSort(String sort) {
        return Stream
                .of(sort.split(","))
                .map(s -> "(" + s + ")")
                .toArray(String[]::new);
    }

}
