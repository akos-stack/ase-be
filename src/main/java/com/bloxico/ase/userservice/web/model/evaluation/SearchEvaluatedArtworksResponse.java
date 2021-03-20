package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.proj.evaluation.EvaluatedArtworkProj;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchEvaluatedArtworksResponse {

    @JsonProperty("page")
    ResponsePage<EvaluatedArtworkProj> page;

    public SearchEvaluatedArtworksResponse(Page<EvaluatedArtworkProj> page) {
        this.page = new ResponsePage<>(page);
    }

}
