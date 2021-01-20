package com.bloxico.ase.userservice.web.model.quotationpackage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class CreateQuotationPackageRequest {

    @NotBlank
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    String name;

    @NotBlank
    @JsonProperty("description")
    @ApiModelProperty(required = true)
    String description;

    @NotBlank
    @JsonProperty("image_path")
    @ApiModelProperty(required = true)
    String imagePath;

    @NotNull
    @JsonProperty("price")
    @ApiModelProperty(required = true)
    BigDecimal price;

    @NotNull
    @JsonProperty("number_of_evaluations")
    @ApiModelProperty(required = true)
    Integer numberOfEvaluations;

    @NotNull
    @JsonProperty("active")
    @ApiModelProperty(required = true)
    Boolean active;

}
