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
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testString : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDouble : NON_FINAL_FIELD"));
        assertTrue(result.toString(), result.toString().contains("org.immutizer4j.test.sample.NonFinalFieldsPojo.testDbl : NON_FINAL_FIELD"));
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

    @Test
    public void testImmutablePojo() {
        ValidationResult result = defaultImmutizer.getValidationResult(ImmutablePojo.class);
        assertEquals(true, result.isValid());
    }

    /**
     * We should not just test a particular object's field, but all of its ancestors as well
     */
    @Test
    public void testAllFieldsInAnObjectHierarchy() {
        ValidationResult result = defaultImmutizer.getValidationResult(ChildPojo.class);

        assertEquals(false,result.isValid());
        // we should get 1 error from the ParentPojo, one from the ChildPojo
        assertEquals(2, result.getErrors().size());

        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.ChildPojo.childMutableInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.ParentPojo.paretMutableInt : NON_FINAL_FIELD"));
    }


    /**
     * When validating fields we should not just walk up the inheritance hierarchy for the root type, but for all
     * the referenced types as well
     */
    @Test
    public void testReferencesToAllFieldsInAnObjectHierarchy() {
        ValidationResult result = defaultImmutizer.getValidationResult(ChildPojoReferencePojo.class);

        assertEquals(false,result.isValid());
        // we should get 1 error from the ParentPojo, one from the ChildPojo
        assertEquals(2, result.getErrors().size());
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.ChildPojo.childMutableInt : NON_FINAL_FIELD"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.ParentPojo.paretMutableInt : NON_FINAL_FIELD"));
    }

    @Test
    public void testRegexPatternReference() {
        ValidationResult result = defaultImmutizer.getValidationResult(RegexPatternReferencePojo.class);

        assertEquals(false,result.isValid());
        // there are many errors in there, which is OK
        assertEquals(20, result.getErrors().size());

        // this is the one we care about, it used to cause StackOverFlow error due to circular reference
        // now it should be reported only once
        assertTrue(result.toString(),
                result.toString().contains("java.util.regex.Pattern$Node.next : NON_FINAL_FIELD"));
    }

    /**
     * Ensure the base types can be sent as a root message themselves
     */
    @Test
    public void testSafeTypes() {
        assertEquals(true, defaultImmutizer.getValidationResult(String.class).isValid());
        assertEquals(true, defaultImmutizer.getValidationResult(Integer.class).isValid());
        assertEquals(true, defaultImmutizer.getValidationResult(int.class).isValid());
    }
}
