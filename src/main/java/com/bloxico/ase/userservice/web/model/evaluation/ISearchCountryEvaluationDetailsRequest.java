package com.bloxico.ase.userservice.web.model.evaluation;

import java.util.Set;

public interface ISearchCountryEvaluationDetailsRequest {

    String getSearch();

    Set<String> getRegions();

    default boolean includeCountriesWithoutEvaluationDetails() {
        return false;
    }

}
