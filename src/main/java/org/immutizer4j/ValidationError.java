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
    private String fieldName;
    private ViolationType violationType;

    @Override
    public String toString() {
        return ThreadLocals.STRINGBUILDER.get()
                .append(type.getName())
                .append(ImmutizerConstants.FIELD_SEPARATOR)
                .append(fieldName)
                .append(ImmutizerConstants.MSG_SEPARATOR)
                .append(violationType)
                .toString();
    }
}
