package com.bloxico.ase.userservice.web.model.quotationpackage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class CreateQuotationPackageResponse {

    @JsonProperty("id")
    Long id;

    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("image_path")
    String imagePath;

    @JsonProperty("price")
    BigDecimal price;

    @JsonProperty("number_of_evaluations")
    int numberOfEvaluations;

    @JsonProperty("active")
    boolean active;

}
