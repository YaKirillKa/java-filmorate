package ru.yandex.practicum.filmorate.constraints;

import ru.yandex.practicum.filmorate.constraintvalidators.AfterDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {

    String date();

    String message() default "The Date is Invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
