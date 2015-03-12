package org.immutizer4j;

/**
 * Validation exception
 *
 * @author Jacek Furmankiewicz
 */
public class ValidationException extends RuntimeException {

    ValidationException(Class<?> entity, String invalidPath) {
        super(String.format("%s::%s is mutable, fails immutability validation",entity,invalidPath));
    }
}
