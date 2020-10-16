package com.bloxico.userservice.util.validator.email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return validateEmail(email);
    }

    /**
     * Email is valid if: There is @ within email, and there is only one @.
     *
     * @param email - email that is requiring validation
     * @return
     */
    private boolean validateEmail(String email) {

        if (email == null) {
            return false;
        }

        int count = email.length() - email.replace("@", "").length();
        return email.contains("@") && count == 1;
    }
}
