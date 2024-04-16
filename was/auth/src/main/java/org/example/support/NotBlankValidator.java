package org.example.support;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NotBlankValidator implements ConstraintValidator<NotBlankList, List<String>> {

    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.stream().allMatch(s -> s != null && !s.trim().isEmpty());
    }
}
