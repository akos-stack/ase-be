package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.ConfigRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ConfigRequestValidator.class)
public @interface ValidConfigRequest {

    String message() default "Invalid config request.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
