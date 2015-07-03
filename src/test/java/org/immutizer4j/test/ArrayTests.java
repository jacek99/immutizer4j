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
    private Immutizer nonStrictImmutizer = new Immutizer(false);

    @Test
    public void testMutableArrays() {

        // default immutizer should disallow arrays
        ValidationResult result = defaultImmutizer.getValidationResult(ArrayPojo.class);

        assertEquals(false, result.isValid());
        // 2 errors for mutable array + 5 errors for one of the actual mutable types in the array
        assertEquals(7, result.getErrors().size());

        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ArrayPojo.nonMutableArray : MUTABLE_ARRAY"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ArrayPojo.mutableArray : MUTABLE_ARRAY"));

        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInteger : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDbl : NON_FINAL_FIELD"));
    }

    @Test
    public void testImmutableArrays() {
        // string immutizer should kick out both arrays
        ValidationResult result = defaultImmutizer.getValidationResult(ImmutableArrayTypePojo.class);

        assertEquals(false, result.isValid());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ImmutableArrayTypePojo.immutableType1Array : MUTABLE_ARRAY"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.ImmutableArrayTypePojo.immutableType2Array : MUTABLE_ARRAY"));

        // non-strict mode should allow them
        result = nonStrictImmutizer.getValidationResult(ImmutableArrayTypePojo.class);

        assertEquals(true, result.isValid());
    }

}
