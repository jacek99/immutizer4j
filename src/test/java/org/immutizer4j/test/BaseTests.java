package org.immutizer4j.test;

import static org.junit.Assert.*;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.FinalParentWithNonFinalFields;
import org.immutizer4j.test.sample.NonFinalFields;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jacek Furmankiewicz
 */
public class BaseTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testNonFinalFields() {
        ValidationResult result = defaultImmutizer.verify(NonFinalFields.class);

        // we should have gotten 5 errors
        assertEquals(false, result.isValid());
        assertEquals(5, result.getErrors().size());
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testInteger : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testDbl : NON_FINAL_FIELD"));

    }

    @Test
    public void testFinalObjectWithNonFinalFields() {
        ValidationResult result = defaultImmutizer.verify(FinalParentWithNonFinalFields.class);

        // we should have gotten 5 errors
        assertEquals(false, result.isValid());
        assertEquals(5, result.getErrors().size());

        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testInteger : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(),result.toString().contains("org.immutizer4j.test.sample.NonFinalFields.testDbl : NON_FINAL_FIELD"));
    }

}
