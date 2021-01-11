package com.bloxico.ase.userservice.validator.impl;

import com.bloxico.ase.userservice.validator.RegularPassword;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class RegularPasswordValidator implements ConstraintValidator<RegularPassword, String> {

    private static final PasswordValidator VALIDATOR =
            new PasswordValidator(
                    List.of(new UppercaseCharacterRule(1),
                            new DigitCharacterRule(1),
                            new SpecialCharacterRule(1)));

    @Override
    public boolean isValid(String password, ConstraintValidatorContext __) {
        return password != null
            && VALIDATOR
                .validate(new PasswordData(password))
                .isValid();
    }

}
