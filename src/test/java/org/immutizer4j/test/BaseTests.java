package org.immutizer4j.test;

import static org.junit.Assert.*;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.FinalParentWithNonFinalFieldsPojo;
import org.immutizer4j.test.sample.ImmutableCollectionPojo;
import org.immutizer4j.test.sample.MutableCollectionPojo;
import org.immutizer4j.test.sample.NonFinalFieldsPojo;
import org.junit.Test;

/**
 * @author Jacek Furmankiewicz
 */
public class BaseTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testNonFinalFields() {
        ValidationResult result = defaultImmutizer.verify(NonFinalFieldsPojo.class);

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
        ValidationResult result = defaultImmutizer.verify(FinalParentWithNonFinalFieldsPojo.class);

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
        ValidationResult result = defaultImmutizer.verify(MutableCollectionPojo.class);

        assertEquals(false, result.isValid());
        assertEquals(1, result.getErrors().size());

        assertEquals("org.immutizer4j.test.sample.MutableCollectionPojo.listInt : MUTABLE_TYPE", result.toString());
    }

    @Test
    public void testFinalImmutableCollection() {
        ValidationResult result = defaultImmutizer.verify(ImmutableCollectionPojo.class);

        // should be fine,  no errors
        assertEquals(true,result.isValid());
        assertEquals("OK",result.toString());
    }

}
