package org.immutizer4j;

import lombok.Getter;
import lombok.Value;

/**
 * Custom runtime exception to indicate violation
 */
public class ImmutabilityViolationException extends RuntimeException {

    @Getter
    private ValidationResult validationResult;

    public ImmutabilityViolationException(ValidationResult result) {
        super(result.toString());
        this.validationResult  = result;
    }
}
