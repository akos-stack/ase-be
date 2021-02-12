package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidSearchRegionsRequest;
import com.bloxico.ase.userservice.web.model.address.SearchRegionsRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SearchRegionsRequestValidator implements ConstraintValidator<ValidSearchRegionsRequest, SearchRegionsRequest> {

    @Override
    public boolean isValid(SearchRegionsRequest request, ConstraintValidatorContext context) {
        var sortOptions = Arrays.asList("id", "name", "number_of_countries", "number_of_evaluators");
        var orderOptions = Arrays.asList("asc", "desc");

        var validSortOption = sortOptions.contains(request.getSort());
        var validOrderOption = orderOptions.contains(request.getOrder());
        var validPageRequest = !request.isPaginated() || request.getPage() >= 0 && request.getSize() >= 1;

        return validSortOption && validOrderOption && validPageRequest;
    }

}
