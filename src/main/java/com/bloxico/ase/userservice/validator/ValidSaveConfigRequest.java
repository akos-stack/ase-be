package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.SaveConfigRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SaveConfigRequestValidator.class)
public @interface ValidSaveConfigRequest {

    String message() default "Invalid config request for the given type.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
