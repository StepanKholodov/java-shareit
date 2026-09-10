package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Допускает {@code null} (поле не передано — при частичном обновлении PATCH
 * это означает "не менять"), но отклоняет пустую или состоящую из пробелов
 * строку — в отличие от {@code @NotBlank}, которая отклоняет и {@code null}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {

    String message() default "Значение не может быть пустой строкой";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
