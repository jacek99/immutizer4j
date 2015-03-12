package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Performs object graph check for immutability
 *
 * @author Jacek Furmankiewicz
 */
@Slf4j
public class Immutizer {

    // additional types that we were told are immutable
    private final Set<Class<?>> safeTypes;

    private static final ConcurrentMap<Class<?>,Void> validationCache =
            new MapMaker()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(100)
            .makeMap();

    public Immutizer() {
        safeTypes = ImmutizerConstants.KNOWN_TYPES;
    }

    /**
     * Constructor
     * @param safeTypes Additional safe types (e.g. Joda DateTime objects, etc) for us to recognize
     */
    public Immutizer(Class<?> ...safeTypes) {
        this.safeTypes = ImmutableSet.<Class<?>>builder()
                .addAll(ImmutizerConstants.KNOWN_TYPES)
                .addAll(Sets.newHashSet(safeTypes))
                .build();
    }

    /**
     * Performs type validation for immutability across the entire object graph
     * Results are cached, so the overhead of the reflection scar are incurred
     * only once
     * @param entity Entity to check
     * @return Validation result
     * @throws ImmutabilityViolationException
     */
    public void verify(Class<?> entity) {
        if (validationCache.containsKey(entity)) {
            // all good, we verified this type before
            return;
        } else {
            performValidation(entity);
            //remember that it was fine
            validationCache.putIfAbsent(entity,null);
        }
    }

    // performs actual walk down the graph hierarchy
    private void performValidation(Class<?> entity) {
        Class<?> current = entity;
        while (current != null) {

            Field[] fields = entity.getDeclaredFields();
            for(Field field : fields) {
                validateField(field);
            }

            // move up the class hierarchy level
            current = entity.getSuperclass();
        }
    }

    // performs all the validations for a single field
    private void validateField(Field field) {

        // basic final check
        if (!Modifier.isFinal(field.getModifiers())){
            throw new ImmutabilityViolationException(field.getDeclaringClass(),field,ViolationReason.FIELD_NOT_FINAL);
        }

        // check for mutable types
        if (!safeTypes.contains(field.getType())) {

            // it's not in the list of concrete types, but maybe assignable t o
            if (!isSafeType(field)) {

                // type is not in the list of known immutable types
                // however it can be automatically recognized as immutable if it is final (which it is by this point)
                // and all of its fields are immutable

                performValidation(field.getType());
            }
        }
    }

    // validates if the field type can be safely assigned to any of the
    private boolean isSafeType(Field field) {
        for(Class<?> clazz : safeTypes) {
            if (field.getType().isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

}
