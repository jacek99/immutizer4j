package org.immutizer4j;

import java.lang.reflect.Field;

/**
 * Validation exception
 *
 * @author Jacek Furmankiewicz
 */
public class ImmutabilityViolationException extends RuntimeException {

    ImmutabilityViolationException(Class<?> entity, Field field, ViolationReason reason) {
        super(
                (reason == ViolationReason.FIELD_NOT_FINAL)
                        ? String.format("%s::%s (%s) is not marked as final",entity,field.getName(), field.getType())
                        : String.format("%s::%s (%s) is mutable",entity,field.getName(), field.getType())
        );
    }
}
