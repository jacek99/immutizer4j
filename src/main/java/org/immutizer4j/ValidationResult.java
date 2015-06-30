package org.immutizer4j;

import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validation result object
 *
 * @author Jacek Furmankiewicz
 */
public class ValidationResult {

    private static final String OK = "OK";

    @Getter
    private final Set<ValidationError> errors = new LinkedHashSet<>();

    /**
     * Returns if the validation passed or not
     * @return
     */
    public boolean isValid() {
        return errors.isEmpty();
    };


    @Override
    public String toString() {
        if (isValid()) {
            return OK;
        } else {
            return errors.stream()
                    .map(ValidationError::toString)
                    .collect(Collectors.joining("\n"));
        }
    }
}
