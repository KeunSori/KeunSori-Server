package com.keunsori.keunsoriserver.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.keunsori.keunsoriserver.global.util.EnumValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {

    Class<? extends Enum<?>> enumClass();
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
