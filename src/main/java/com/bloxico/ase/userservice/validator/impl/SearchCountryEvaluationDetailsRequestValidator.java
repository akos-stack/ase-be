package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidSearchCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SearchCountryEvaluationDetailsRequestValidator
        implements ConstraintValidator<ValidSearchCountryEvaluationDetailsRequest, SearchCountryEvaluationDetailsRequest> {

    @Override
    public boolean isValid(SearchCountryEvaluationDetailsRequest request, ConstraintValidatorContext context) {
        var sortOptions =
                Arrays.asList("country", "region", "price_per_evaluation", "availability_percentage", "total_of_evaluators");
        var orderOptions = Arrays.asList("asc", "desc");

        var validRegionFilter = request.getRegions() == null || !request.getRegions().isEmpty();
        var validSortOption = sortOptions.contains(request.getSort());
        var validOrderOption = orderOptions.contains(request.getOrder());
        var validPageRequest = request.getPage() >= 0 && request.getSize() >= 1;

        return validRegionFilter && validSortOption && validOrderOption && validPageRequest;
    }

}
