package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.ValidEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null
            && email.contains("@")
            && (email.length() - email.replace("@", "").length()) == 1;
    }

}
