package org.immutizer4j;

import lombok.Value;

import java.lang.reflect.Field;

/**
 * Validation error
 *
 * @author Jacek Furmankiewicz
 */
@Value
public class ValidationError {
    private Class<?> type;
    private Field field;
    private ViolationReason reason;
}
