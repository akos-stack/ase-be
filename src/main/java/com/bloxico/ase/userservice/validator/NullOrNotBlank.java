package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.NullOrNotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {

    String message() default "Field must be either null or not blank.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
