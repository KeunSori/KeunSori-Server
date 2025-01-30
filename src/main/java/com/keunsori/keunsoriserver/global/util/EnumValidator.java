package com.keunsori.keunsoriserver.global.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com.keunsori.keunsoriserver.global.annotation.ValidEnum;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private String message;
    private Class<? extends Enum> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        message = constraintAnnotation.message();
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = Arrays.stream(enumClass.getFields()).map(Field::getName)
                .anyMatch(name -> name.equals(value.toUpperCase()));

        return isValid;
    }
}
