package com.bloxico.ase.userservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

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

    @ApiParam(name = "sort")
    String sort;

    @ApiParam(name = "order")
    String order;

    @JsonIgnore
    @SuppressWarnings("ConstantConditions")
    public Pageable toPageable() {
        if (sort != null || order != null) {
            var unsafeSortProperty = String.format("(%s)", sort);
            return org.springframework.data.domain.PageRequest.of(
                    page, size,
                    "desc".equals(order)
                            ? JpaSort.unsafe(DESC, unsafeSortProperty)
                            : JpaSort.unsafe(ASC, unsafeSortProperty));
        }
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

}
