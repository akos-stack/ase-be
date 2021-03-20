package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class EvaluatedArtworkProj {

    @JsonProperty("artwork_title")
    String artworkTitle;

    @JsonProperty("artist")
    String artist;

    @JsonProperty("selling_price")
    BigDecimal sellingPrice;

}
