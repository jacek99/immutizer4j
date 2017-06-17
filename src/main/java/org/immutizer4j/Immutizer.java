package org.immutizer4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs object graph check for immutability
 *
 * @author Jacek Furmankiewicz
 */
@Slf4j
public class Immutizer {

    // may allow arrays to pass or not (no by default)
    private final boolean strict;

    // additional types that we were told are immutable
    private final ImmutableSet<Class<?>> safeTypes;

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
    public ValidationResult getValidationResult(@NonNull Class<?> clazz) {
        ValidationResult result = validationCache.get(clazz);
        if (result == null) {
            result = validationCache.computeIfAbsent(clazz, c -> validateType(c));
        }
        return result;
    }

    // performs actual walk down the graph hierarchy starting from the root object
    private ValidationResult validateType(Class<?> type) {
        if (!isSafeType(type)) {
            return validateType(type, new ValidationResult(ImmutableSet.<ValidationError>of()));
        } else {
            // safe types do not need to be tested
            return new ValidationResult(ImmutableSet.<ValidationError>of());
        }
    }

    // performs actual walk down the graph hierarchy within an existing validation process
    private ValidationResult validateType(Class<?> type, ValidationResult result) {
        Class<?> current = type;
        while (current != null && !current.equals(Object.class)) {

            Field[] fields = current.getDeclaredFields();
            for(Field field : fields) {
                result = validateField(field, result);
            }

            // move up the class hierarchy level
            current = current.getSuperclass();
        }

        return result;
    }

    // performs all the validations for a single field
    private ValidationResult validateField(Field field, ValidationResult result) {

        if (!Modifier.isStatic(field.getModifiers())) {

            // basic final check
            if (!Modifier.isFinal(field.getModifiers())){
                result = addError(field, ViolationType.NON_FINAL_FIELD, result);
            }

            result = handleCollections(field, result);
            result = handleArrays(field, result);

            // for custom types, recursively check its own fields
            Class<?> actualType = getActualType(field,result);
            result = validateIfGenericsReference(field,actualType,result);

            if (!isSafeType(actualType) && !isRecursive(field)) {
                result = validateType(actualType, result);
            }
        }

        return result;

    }

    // common logic for handling collection tyoes
    private ValidationResult handleCollections(Field field, ValidationResult result) {
        if (Collection.class.isAssignableFrom(field.getType())) {

            // check if collection is immutable to begin with
            if (!isSafeType(field.getType())) {
                result = addError(field, ViolationType.MUTABLE_TYPE, result);
            }

            // check if the type stored in the collection is immutable (works around type erasure)
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            for(Type type : parameterizedType.getActualTypeArguments()) {
                try {
                    Class<?> genericType = Class.forName(type.getTypeName());

                    ValidationResult nestedResult = getValidationResult(genericType);
                    if (!nestedResult.isValid()) {
                        result = addError(field, ViolationType.MUTABLE_TYPE_STORED_IN_COLLECTION, result);
                    }
                } catch (ClassNotFoundException e) {
                    result = addError(field, ViolationType.GENERIC_TYPE_WITH_WILDCARD, result);
                }
            }
        }

        return result;
    }

    // arrays (can be allowed if we are not running in strict mode)
    private ValidationResult handleArrays(Field field, ValidationResult result) {
        if (field.getType().isArray() && strict) {
            result = addError(field,ViolationType.MUTABLE_ARRAY,result);
        }
        return result;
    }

    // validates if the field type can be safely assigned to any of the
    private boolean isSafeType(Class<?> type) {
        for(Class<?> clazz : safeTypes) {
            if (clazz.equals(type) || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    // checks if a field refers recursively to the containing/declaring class
    private boolean isRecursive(Field field) {
        return field.getType().equals(field.getDeclaringClass());
    }

    // standard handler for reporting errors, returns a new immutable ValidationResult instance
    private ValidationResult addError(Field field, ViolationType violationType, ValidationResult result) {
        // log it as long as it is not marked as @ImmutizerIgnore
        ValidationError error = new ValidationError(field.getDeclaringClass(), field.getName(), violationType);
        log.error("Immutability violation: {}", error);
        return result.addError(error);
    }

    /**
     * Jumps hoops around various data in Java reflection to find the actual underlying type
     */
    @SneakyThrows // checked exception begone!
    private Class<?> getActualType(Field field, ValidationResult result) {
        // for custom types, recursively check its own fields
        // some reflection magic for dealing with arrays vs regular types
        Class<?> actualType = (field.getType().isArray()) ? field.getType().getComponentType() : field.getType();

        return actualType;
    }

    @SneakyThrows // checked exception begone!
    public ValidationResult validateIfGenericsReference(Field field, Class<?> actualType, ValidationResult result) {
        if (Object.class.equals(actualType)) {

            // let's see if we are dealing with a reference to a generics type
            // this info is hidden by the JVM in the internal private Field.signature field
            // the class name is embedded in there, but alas, without the package name (OK, Oracle, that is just stupid)
            Field signatureField = field.getClass().getDeclaredField(ImmutizerConstants.SIGNATURE_FIELD);
            signatureField.setAccessible(true); // private is for losers, real programmers get to it anyway

            // try to extract actual class name via
            String sig = (String) signatureField.get(field);
            // T = var, [T = array of vars
            if ((sig.startsWith("T") || sig.startsWith("[T")) && sig.endsWith(";")) {
                // OK, reference to a generic type
                // unfortunately there is no information on the package of the type so we cannot get to it
                // need to flag this is a violation
                result = addError(field, ViolationType.UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE, result);
            }
        }

        return result;
    }

}
