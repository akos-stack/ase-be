package com.bloxico.ase.userservice.web.model.evaluation;

import java.util.List;

public interface ISearchCountryEvaluationDetailsRequest {

    String getSearch();

    List<String> getRegions();

    default boolean includeCountriesWithoutEvaluationDetails() {
        return false;
    }

}
