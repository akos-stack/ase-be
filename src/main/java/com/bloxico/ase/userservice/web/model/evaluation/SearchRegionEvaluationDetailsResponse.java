package com.bloxico.ase.userservice.web.model.evaluation;

import com.bloxico.ase.userservice.proj.evaluation.RegionWithCountriesAndEvaluatorsCountProj;
import com.bloxico.ase.userservice.web.model.ResponsePage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchRegionEvaluationDetailsResponse {

    @JsonProperty("page")
    ResponsePage<RegionWithCountriesAndEvaluatorsCountProj> page;

    public SearchRegionEvaluationDetailsResponse(Page<RegionWithCountriesAndEvaluatorsCountProj> page) {
        this.page = new ResponsePage<>(page);
    }

}
