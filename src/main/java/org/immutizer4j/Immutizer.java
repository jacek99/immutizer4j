package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
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

    // may allow arrays to pass or not (no by default)
    private boolean strict = true;

    // additional types that we were told are immutable
    private final Set<Class<?>> safeTypes;

    private final ConcurrentMap<Class<?>,ValidationResult> validationCache =
            new MapMaker()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(100)
            .makeMap();

    /**
     * Constructor. Assumes strict mode
     */
    public Immutizer() {
        // assumes no custom types
        this(true, new Class[]{});
    }

    /**
     * Constructor
     * @param strict Controls if we run in strict mode or not (which allows arrays of immutable types to be allowed)
     */
    public Immutizer(boolean strict) {
        // assumes no custom types
        this(strict,new Class[]{});
    }

    /**
     * Constructor
     * @param safeTypes Additional safe types (e.g. Joda DateTime objects, etc) for us to recognize
     */
    public Immutizer(Class<?> ...safeTypes) {
        this(true, safeTypes);
    }

    /**
     * Constructor
     * @param strict Controls if we run in strict mode or not (which allows arrays of immutable types to be allowed)
     * @param safeTypes Additional safe types (e.g. Joda DateTime objects, etc) for us to recognize
     */
    public Immutizer(boolean strict, Class<?> ...safeTypes) {
        this.strict = strict;
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
                ValidationResult result = performValidation(clazz);
                //remember that it was fine
                validationCache.putIfAbsent(clazz, result);
                return result;
            }

        } else {
            throw new NullPointerException("clazz was null");
        }

    }

    // performs actual walk down the graph hierarchy
    private ValidationResult performValidation(Class<?> entity) {
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

    @SneakyThrows // avoid checked exceptions thanks to Lombok
    // performs all the validations for a single field
    private ValidationResult validateField(Field field, ValidationResult result, Optional<Field> parent) {

        if (!Modifier.isStatic(field.getModifiers())) {

            // basic final check
            if (!Modifier.isFinal(field.getModifiers())){
                result = addError(field, ViolationType.NON_FINAL_FIELD, result);
            }

            // collections
            if (Collection.class.isAssignableFrom(field.getType())) {

                // check if collection is immutable to begin with
                if (!isSafeType(field)) {
                    result = addError(field, ViolationType.MUTABLE_TYPE, result);
                }

                // check if the type stored in the collection is immutable (works around type erasure)
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                for(Type type : parameterizedType.getActualTypeArguments()) {
                    Class<?> genericType = Class.forName(type.getTypeName());

                    ValidationResult nestedResult = getValidationResult(genericType);
                    if (!nestedResult.isValid()) {
                        result = addError(field,ViolationType.MUTABLE_TYPE_STORED_IN_COLLECTION, result);
                    }
                }
            }

            // arrays (can be allowed if we are not running in strict mode)
            if (field.getType().isArray() && strict) {
                
                result = addError(field,ViolationType.MUTABLE_ARRAY,result);
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
            if (clazz.equals(field.getType()) || clazz.isAssignableFrom(field.getType())) {
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
