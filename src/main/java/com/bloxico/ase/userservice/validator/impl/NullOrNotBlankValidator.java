package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.NullOrNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext __) {
        return value == null || !value.isBlank();
    }

}
