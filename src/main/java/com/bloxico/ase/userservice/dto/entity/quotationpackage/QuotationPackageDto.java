package com.bloxico.ase.userservice.dto.entity.quotationpackage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = "name")
public class QuotationPackageDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("image_path")
    private String imagePath;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("number_of_evaluations")
    private int numberOfEvaluations;

    @JsonProperty("active")
    private boolean active;

}
