package org.immutizer4j.test;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for all functionality related to arrays
 * @author Jacek Furmankiewicz
 */
public class ArrayTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testArrays() {

        // default immutizer should disallow arrays
        ValidationResult result = defaultImmutizer.getValidationResult(ArrayPojo.class);

        assertEquals(false, result.isValid());
        assertEquals(2, result.getErrors().size());

        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ArrayPojo.nonMutableArray : MUTABLE_ARRAY"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ArrayPojo.mutableArray : MUTABLE_ARRAY"));
    }

}
