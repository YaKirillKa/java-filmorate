package ru.yandex.practicum.filmorate.constraintvalidators;

import ru.yandex.practicum.filmorate.constraints.AfterDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AfterDateValidator implements ConstraintValidator<AfterDate, String> {

    String date;

    @Override
    public void initialize(AfterDate afterDate) {
        this.date = afterDate.date();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(value);
            LocalDate parsedAfterDate = LocalDate.parse(date);
            return parsedDate.isAfter(parsedAfterDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
