package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
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

    private static final ConcurrentMap<Class<?>,ValidationResult> validationCache =
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
     * @param clazz Entity to check
     * @return Validation result
     */
    public ValidationResult verify(Class<?> clazz) {
        if (validationCache.containsKey(clazz)) {
            // all good, we verified this type before
            return validationCache.get(clazz);
        } else {
            ValidationResult result = performValidation(clazz, ThreadLocals.STRINGBUILDER.get()
                    .append(clazz.getSimpleName()));
            //remember that it was fine
            validationCache.putIfAbsent(clazz,result);
            return result;
        }
    }

    // performs actual walk down the graph hierarchy
    private ValidationResult performValidation(Class<?> entity, StringBuilder currentPath) {
        Class<?> current = entity;
        ValidationResult result = new ValidationResult();
        while (current != null && !current.equals(Object.class)) {

            Field[] fields = current.getDeclaredFields();
            for(Field field : fields) {
                validateField(field, result, Optional.<Field>empty());
            }

            // move up the class hierarchy level
            current = current.getSuperclass();
        }

        return result;
    }

    // performs all the validations for a single field
    private void validateField(Field field, ValidationResult result, Optional<Field> parent) {

        if (!Modifier.isStatic(field.getModifiers())) {

            // basic final check
            if (!Modifier.isFinal(field.getModifiers())){
                ValidationError error = new ValidationError(field.getDeclaringClass(), field.getName(), ViolationType.NON_FINAL_FIELD);
                log.error("Immutability violation: {}",error);
                result.getErrors().add(error);
            }

            // for custom types, recursively check its own fields
            if (!ImmutizerConstants.KNOWN_TYPES.contains(field.getType())) {

                for(Field childField : field.getType().getDeclaredFields()) {
                    log.debug("Validating {}.{}", childField.getDeclaringClass().getSimpleName(), childField.getName());
                    validateField(childField, result, Optional.of(field));
                }

            }
        }

//        // check for mutable types
//        if (!safeTypes.contains(field.getType())) {
//
//            // it's not in the list of concrete types, but maybe assignable to it
//            if (!isSafeType(field)) {
//
//                // type is not in the list of known immutable types
//                // however it can be automatically recognized as immutable if it is final (which it is by this point)
//                // and all of its fields are immutable
//
//                performValidation(field.getType());
//            } else {
//
//            }
//        }
//
//        return Optional.empty();
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
