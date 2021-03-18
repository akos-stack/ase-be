package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ArtworkEvaluatedProj {

    @JsonProperty("art_name")
    String artName;

    @JsonProperty("artist")
    String artist;

    @JsonProperty("selling_price")
    BigDecimal sellingPrice;

    @JsonProperty("sold_for")
    BigDecimal soldFor;

}
