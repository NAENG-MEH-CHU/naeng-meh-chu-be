package org.example.support;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankList {
    String message() default "List elements must not be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}



