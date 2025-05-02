package dev.workforge.app.WorkForge.Service.ServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class DTOValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Validation failed:\n");
            for (ConstraintViolation<T> violation : violations) {
                message.append("- ")
                        .append(violation.getPropertyPath()).append(": ")
                        .append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(message.toString());
        }
    }
}
