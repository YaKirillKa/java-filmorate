package ru.yandex.practicum.filmorate.constraintvalidators;

import ru.yandex.practicum.filmorate.constraints.AfterDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {

    String date;

    @Override
    public void initialize(AfterDate afterDate) {
        this.date = afterDate.date();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            LocalDate parsedAfterDate = LocalDate.parse(date);
            return value.isAfter(parsedAfterDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
