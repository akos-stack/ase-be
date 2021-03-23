package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.proj.evaluation.EvaluableArtworkProj;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchEvaluableArtworksResponse {

    @JsonProperty("page")
    ResponsePage<EvaluableArtworkProj> page;

    public SearchEvaluableArtworksResponse(Page<EvaluableArtworkProj> page) {
        this.page = new ResponsePage<>(page);
    }

}
