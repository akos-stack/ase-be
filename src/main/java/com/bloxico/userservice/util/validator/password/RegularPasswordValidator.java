package com.bloxico.userservice.util.validator.password;


import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RegularPasswordValidator implements ConstraintValidator<RegularPassword, String> {


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        return validatePassword(password);
    }

    private boolean validatePassword(String password) {

        if (password == null) {
            return false;
        }

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new UppercaseCharacterRule(1),
                new DigitCharacterRule(1),
                new SpecialCharacterRule(1)));

        RuleResult result = validator.validate(new PasswordData(password));

        return result.isValid();
    }
}
