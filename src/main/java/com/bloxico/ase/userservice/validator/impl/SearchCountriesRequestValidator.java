package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidSearchCountriesRequest;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SearchCountriesRequestValidator
        implements ConstraintValidator<ValidSearchCountriesRequest, SearchCountriesRequest> {

    @Override
    public boolean isValid(SearchCountriesRequest request, ConstraintValidatorContext context) {
        var sortOptions =
                Arrays.asList("id", "name", "region", "price_per_evaluation", "availability_percentage", "total_of_evaluators");
        var orderOptions = Arrays.asList("asc", "desc");

        var validRegionFilter = request.getRegions() == null || !request.getRegions().isEmpty();
        var validSortOption = sortOptions.contains(request.getSort());
        var validOrderOption = orderOptions.contains(request.getOrder());
        var validPageRequest = !request.isPaginated() || request.getPage() >= 0 && request.getSize() >= 1;

        return validRegionFilter && validSortOption && validOrderOption && validPageRequest;
    }

}
