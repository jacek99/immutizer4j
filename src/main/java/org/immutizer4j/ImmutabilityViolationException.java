package org.immutizer4j;

import lombok.Value;

/**
 * Custom runtime exception to indicate violation
 */
@Value
public class ImmutabilityViolationException extends RuntimeException {

    private ValidationResult validationResult;

    public ImmutabilityViolationException(ValidationResult result) {
        super(result.toString());
        this.validationResult  = result;
    }
}
