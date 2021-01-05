package com.bloxico.ase.userservice.validator;

import com.bloxico.ase.userservice.validator.impl.RegularPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RegularPasswordValidator.class)
public @interface RegularPassword {

    String message() default "Password must contain at least 1 uppercase, 1 lowercase letter, 1 number and a special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
