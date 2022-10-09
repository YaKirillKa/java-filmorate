package ru.yandex.practicum.filmorate.constraints;

import ru.yandex.practicum.filmorate.constraintvalidators.ValuesAllowedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValuesAllowedValidator.class)
public @interface ValuesAllowed {

    String[] values();

    String message() default "Parameter must be in the list of ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
