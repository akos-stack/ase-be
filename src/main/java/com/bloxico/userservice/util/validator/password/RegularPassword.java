package com.bloxico.userservice.util.validator.password;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegularPasswordValidator.class)
public @interface RegularPassword {
    String message() default "Password must contain at least 1 uppercase, 1 lowercase letter, 1 number and a special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
