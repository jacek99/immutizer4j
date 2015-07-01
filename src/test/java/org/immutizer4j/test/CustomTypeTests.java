package org.immutizer4j.test;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for all the logic relating to registering custom immutable types
 * @author Jacek Furmankiewicz
 */
public class CustomTypeTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testCustomImmutableCollection() {
        // default immutizer should reject our custom immutable collection type
        ValidationResult result = defaultImmutizer.getValidationResult(CustomImmutableCollectionPojo.class);
        assertEquals(false, result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("org.immutizer4j.test.sample.CustomImmutableCollectionPojo.immutableIntegers : MUTABLE_TYPE", result.toString());

        // create custom immutizer that whitelists our custom immutable collection type
        Immutizer customImmutizer = new Immutizer(ICustomImmutableCollection.class);
        ValidationResult customResult = customImmutizer.getValidationResult(CustomImmutableCollectionPojo.class);
        assertEquals(true,customResult.isValid());
    }


}
