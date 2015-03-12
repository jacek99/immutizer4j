package org.immutizer4j;

import com.google.common.collect.MapMaker;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import lombok.extern.slf4j.Slf4j;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentMap;

/**
 * Performs object graph check for immutability
 *
 * @author Jacek Furmankiewicz
 */
@Slf4j
public class Immutizer {

    private static final ConcurrentMap<Class<?>,Void> validationCache =
            new MapMaker()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(100)
            .makeMap();

    /**
     * Performs type validation for immutability across the entire object graph
     * Results are cached, so the overhead of the reflection scar are incurred
     * only once
     * @param entity Entity to check
     * @return Validation result
     * @throws org.immutizer4j.ValidationException
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

    private void performValidation(Class<?> entity) {

    }

    



}
