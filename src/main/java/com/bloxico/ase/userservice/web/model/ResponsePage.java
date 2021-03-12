package com.bloxico.ase.userservice.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class ResponsePage<T> {

    @JsonProperty("content")
    List<T> content;

    @JsonProperty("size")
    int size;

    @JsonProperty("number_of_pages")
    int numberOfPages;

    @JsonProperty("total_size")
    long totalSize;

    public ResponsePage(Page<T> page) {
        content = page.getContent();
        size = page.getSize();
        numberOfPages = page.getTotalPages();
        totalSize = page.getTotalElements();
    }

}
