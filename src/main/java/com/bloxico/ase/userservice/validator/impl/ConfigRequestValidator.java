package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidConfigRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfigRequestValidator implements ConstraintValidator<ValidConfigRequest, SaveConfigRequest> {

    @Override
    public boolean isValid(SaveConfigRequest request, ConstraintValidatorContext context) {
        var type = request.getType();
        var value = request.getValue();
        if (type.validate(value)) {
            return true;
        }

        context
                .buildConstraintViolationWithTemplate("Invalid config value.")
                .addPropertyNode("value")
                .addConstraintViolation();
        return false;
    }

}
