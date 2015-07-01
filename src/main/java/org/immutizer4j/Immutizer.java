package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
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
        // assumes no custom types
        this(new Class[]{});
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

        // eat your own dogfood or go home
        verify(ValidationResult.class);
    }

    /**
     * Validates instance of type and throws exception if violation found
     * @throws ImmutabilityViolationException
     */
    public void verify(Object instance) {
        if (instance != null) {
            verify(instance.getClass());
        } else {
            throw new NullPointerException("instance was null");
        }
    }

    /**
     * Validates type and throws exception if violation found
     * @throws ImmutabilityViolationException
     */
    public void verify(Class<?> clazz) {
        if (clazz != null) {
            ValidationResult result = getValidationResult(clazz);
            if (!result.isValid()) {
                throw new ImmutabilityViolationException(result);
            }
        } else {
            throw new NullPointerException("clazz was null");
        }
    }

    /**
     * Performs type validation for immutability across the entire object graph
     * Results are cached, so the overhead of the reflection scar are incurred
     * only once
     * @param clazz Entity to check
     * @return Validation result
     */
    public ValidationResult getValidationResult(Class<?> clazz) {
        if (clazz != null) {

            if (validationCache.containsKey(clazz)) {
                // all good, we verified this type before
                return validationCache.get(clazz);
            } else {
                ValidationResult result = performValidation(clazz, ThreadLocals.STRINGBUILDER.get()
                        .append(clazz.getSimpleName()));
                //remember that it was fine
                validationCache.putIfAbsent(clazz, result);
                return result;
            }

        } else {
            throw new NullPointerException("clazz was null");
        }

    }

    // performs actual walk down the graph hierarchy
    private ValidationResult performValidation(Class<?> entity, StringBuilder currentPath) {
        Class<?> current = entity;
        ValidationResult result = new ValidationResult(ImmutableSet.<ValidationError>of());
        while (current != null && !current.equals(Object.class)) {

            Field[] fields = current.getDeclaredFields();
            for(Field field : fields) {
                result = validateField(field, result, Optional.<Field>empty());
            }

            // move up the class hierarchy level
            current = current.getSuperclass();
        }

        return result;
    }

    // performs all the validations for a single field
    private ValidationResult validateField(Field field, ValidationResult result, Optional<Field> parent) {

        if (!Modifier.isStatic(field.getModifiers())) {

            // basic final check
            if (!Modifier.isFinal(field.getModifiers())){
                result = addError(field, ViolationType.NON_FINAL_FIELD, result);
            }

            // collections
            if (Collection.class.isAssignableFrom(field.getType()) && !isSafeType(field)) {
                result = addError(field, ViolationType.MUTABLE_TYPE, result);
            }

            // for custom types, recursively check its own fields
            if (!isSafeType(field)) {

                for(Field childField : field.getType().getDeclaredFields()) {
                    log.debug("Validating {}.{}", childField.getDeclaringClass().getSimpleName(), childField.getName());
                    result = validateField(childField, result, Optional.of(field));
                }
            }

        }

        return result;

    }

    // validates if the field type can be safely assigned to any of the
    private boolean isSafeType(Field field) {
        for(Class<?> clazz : safeTypes) {
            if (clazz.isAssignableFrom(field.getType())) {
                return true;
            }
        }
        return false;
    }

    // standard handler for reporting errors, returns a new immutable ValidationResult instance
    private ValidationResult addError(Field field, ViolationType violationType, ValidationResult result) {
        ValidationError error = new ValidationError(field.getDeclaringClass(), field.getName(), violationType);
        log.error("Immutability violation: {}", error);
        return result.addError(error);
    }

}
