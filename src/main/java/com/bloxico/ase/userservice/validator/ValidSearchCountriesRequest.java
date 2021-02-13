package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.SearchCountriesRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SearchCountriesRequestValidator.class)
public @interface ValidSearchCountriesRequest {

    String message() default "Invalid countries search filter.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

