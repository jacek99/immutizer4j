package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validation result object
 *
 * @author Jacek Furmankiewicz
 */
@Value
public class ValidationResult {

    private static final String OK = "OK";

    private ImmutableSet<ValidationError> errors;

    /**
     * Returns if the validation passed or not
     * @return
     */
    public boolean isValid() {
        return errors.isEmpty();
    };

    /**
     * Creates a new immutable instance that merges existing errors with the new one
     */
    public ValidationResult addError(ValidationError error) {
        ImmutableSet<ValidationError> newErrors = ImmutableSet.<ValidationError>builder()
                .addAll(errors)
                .add(error)
                .build();

        return new ValidationResult(newErrors);
    }

    @Override
    public String toString() {
        if (isValid()) {
            return OK;
        } else {
            return errors.stream()
                    .map(ValidationError::toString)
                    .collect(Collectors.joining(ImmutizerConstants.NEWLINE));
        }
    }

}
