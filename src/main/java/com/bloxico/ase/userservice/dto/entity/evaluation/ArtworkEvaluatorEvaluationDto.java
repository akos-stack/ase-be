package com.bloxico.ase.userservice.dto.entity.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"artworkId", "evaluatorId"}, callSuper = false)
public class ArtworkEvaluatorEvaluationDto {

    @JsonProperty("artwork_id")
    private Long artworkId;

    @JsonProperty("evaluator_id")
    private Long evaluatorId;

    @JsonProperty("country_id")
    private Long countryId;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("selling_price")
    private Integer sellingPrice;

    @JsonProperty("ase_sellable")
    private Boolean aseSellable;

    @JsonProperty("send_offer")
    private Boolean sendOffer;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

}
