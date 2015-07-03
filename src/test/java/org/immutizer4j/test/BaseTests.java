package org.immutizer4j.test;

import static org.junit.Assert.*;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.*;
import org.junit.Test;

/**
 * Tests for all base functionality
 * @author Jacek Furmankiewicz
 */
public class BaseTests {

    private Immutizer defaultImmutizer = new Immutizer();

    /**
     * If we can't be immutable, no one can
     */
    @Test
    public void eatYourOwnDogfood() {
        ValidationResult result = defaultImmutizer.getValidationResult(ValidationResult.class);
        assertEquals(true, result.isValid());

        // Immutizer itself is NOT immutable, since it uses generics with wildcards on internal fields
        // but this is a good test to ensure we handle this scenario
        result = defaultImmutizer.getValidationResult(Immutizer.class);
        assertEquals(false,result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("org.immutizer4j.Immutizer.safeTypes : GENERIC_TYPE_WITH_WILDCARD",result.toString());
    }

    @Test
    public void testNonFinalFields() {
        ValidationResult result = defaultImmutizer.getValidationResult(NonFinalFieldsPojo.class);

        // we should have gotten 5 errors
        assertEquals(false, result.isValid());
        assertEquals(5, result.getErrors().size());
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInteger : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDbl : NON_FINAL_FIELD"));
   }

    @Test
    public void testFinalObjectWithNonFinalFields() {
        ValidationResult result = defaultImmutizer.getValidationResult(FinalParentWithNonFinalFieldsPojo.class);

        // we should have gotten 5 errors
        assertEquals(false, result.isValid());
        assertEquals(5, result.getErrors().size());

        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testInteger : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDbl : NON_FINAL_FIELD"));
    }

    @Test
    public void testFinalMutableCollection() {
        ValidationResult result = defaultImmutizer.getValidationResult(MutableCollectionPojo.class);

        assertEquals(false, result.isValid());
        assertEquals(1, result.getErrors().size());

        assertEquals("org.immutizer4j.test.sample.MutableCollectionPojo.listInt : MUTABLE_TYPE", result.toString());
    }

    @Test
    public void testFinalImmutableCollection() {
        ValidationResult result = defaultImmutizer.getValidationResult(ImmutableCollectionPojo.class);

        // should be fine,  no errors
        assertEquals(true,result.isValid());
        assertEquals("OK",result.toString());
    }

    @Test
    public void testMutableElementWithinImmutableCollection() {
        ValidationResult result = defaultImmutizer.getValidationResult(ImmutableCollectionWithMutableElementPojo.class);

        //it should detect that though both collections are immutable, the type stored in one of them is not
        assertEquals(false, result.isValid());
        assertEquals("org.immutizer4j.test.sample.ImmutableCollectionWithMutableElementPojo.mutableElementList : MUTABLE_TYPE_STORED_IN_COLLECTION",result.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testVerifyInstanceForNull() {
        Integer val = null;
        defaultImmutizer.verify(val);
    }

    @Test(expected = NullPointerException.class)
    public void testVerifyClassForNull() {
        Class val = null;
        defaultImmutizer.verify(val);
    }

    @Test(expected = NullPointerException.class)
    public void testGetValidationResultForNull() {
        defaultImmutizer.getValidationResult(null);
    }

}
