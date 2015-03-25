package org.immutizer4j;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Validation result object
 *
 * @author Jacek Furmankiewicz
 */
public class ValidationResult {

    @Getter
    private final Set<ValidationError> errors = new LinkedHashSet<>();

    /**
     * Returns if the validation passed or not
     * @return
     */
    public boolean isValid() {
        return errors.isEmpty();
    };

}
