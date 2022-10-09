package ru.yandex.practicum.filmorate.constraintvalidators;

import ru.yandex.practicum.filmorate.constraints.ValuesAllowed;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValuesAllowedValidator implements ConstraintValidator<ValuesAllowed, String> {

    private List<String> expectedValues;
    private String message;

    @Override
    public void initialize(ValuesAllowed valuesAllowed) {
        expectedValues = Arrays.asList(valuesAllowed.values());
        message = valuesAllowed.message().concat(expectedValues.toString());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = expectedValues.contains(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
