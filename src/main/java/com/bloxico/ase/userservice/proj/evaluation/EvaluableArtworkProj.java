package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class EvaluableArtworkProj {

    @JsonProperty("artwork_id")
    Long artworkId;

    @JsonProperty("artwork_title")
    String artworkTitle;

    @JsonProperty("evaluations_limit")
    int evaluationsLimit;

    @JsonProperty("evaluations_taken")
    long evaluationsTaken;

}
