package com.bloxico.ase.userservice.proj.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class OngoingEvaluationsProj {

    @JsonProperty("artwork_id")
    Long artworkId;

    @JsonProperty("artwork_title")
    String artworkTitle;

    @JsonProperty("max_evaluations")
    int maxEvaluations;

    @JsonProperty("finished_evaluations")
    long finishedEvaluations;

}
