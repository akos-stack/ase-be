package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidSaveConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SaveConfigRequestValidator implements ConstraintValidator<ValidSaveConfigRequest, SaveConfigRequest> {

    @Override
    public boolean isValid(SaveConfigRequest request, ConstraintValidatorContext __) {
        return request.getType().isValid(request.getValue());
    }

}
