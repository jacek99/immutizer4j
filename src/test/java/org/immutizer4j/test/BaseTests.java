package org.immutizer4j.test;

import static org.junit.Assert.*;

import com.sun.javafx.geom.transform.Identity;
import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.FinalParentWithNonFinalFields;
import org.immutizer4j.test.sample.ImmutableCollection;
import org.immutizer4j.test.sample.MutableCollection;
import org.immutizer4j.test.sample.NonFinalFields;
import org.junit.Test;

import java.util.Optional;

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

    @Test
    public void testFinalMutableCollection() {
        ValidationResult result = defaultImmutizer.verify(MutableCollection.class);

        assertEquals(false, result.isValid());
        assertEquals(1, result.getErrors().size());

        assertEquals("org.immutizer4j.test.sample.MutableCollection.listInt : MUTABLE_TYPE", result.toString());

    }

    @Test
    public void testFinalImmutableCollection() {
        ValidationResult result = defaultImmutizer.verify(ImmutableCollection.class);

        // should be fine,  no errors
        assertEquals(true,result.isValid());
    }

}
