package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.SearchCountryEvaluationDetailsRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SearchCountryEvaluationDetailsRequestValidator.class)
public @interface ValidSearchCountryEvaluationDetailsRequest {

    String message() default "Invalid countries search filter.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
