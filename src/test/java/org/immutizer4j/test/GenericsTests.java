package org.immutizer4j.test;

import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.ArrayPojo;
import org.immutizer4j.test.sample.ImmutableArrayPojo;
import org.immutizer4j.test.sample.ImmutableArrayTypePojo;
import org.immutizer4j.test.sample.generics.BadConcreteGenericsContainer;
import org.immutizer4j.test.sample.generics.GoodConcreteGenericsContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for all functionality related to generics
 * All classes that implemented interfaces with generics get ALL their type erased all the way down
 * to java.lang.Object
 * Hence, we have no choice but to reject them, there is no way for us to get to the actual type
 *
 * @author Jacek Furmankiewicz
 */
public class GenericsTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testGenericsReferenceToMutableTypes() {
        ValidationResult result = defaultImmutizer.getValidationResult(BadConcreteGenericsContainer.class);

        assertEquals(false,result.isValid());
        assertEquals(3, result.getErrors().size());

        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.BadConcreteGenericsContainer.a : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.BadConcreteGenericsContainer.typeB : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.BadConcreteGenericsContainer.c : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
    }

    @Test
    public void testGenericsReferenceToImmutableTypes() {
        ValidationResult result = defaultImmutizer.getValidationResult(GoodConcreteGenericsContainer.class);

        assertEquals(false,result.isValid());
        assertEquals(3, result.getErrors().size());

        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.GoodConcreteGenericsContainer.a : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.GoodConcreteGenericsContainer.typeB : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
        assertTrue(result.toString(),
                result.toString().contains("org.immutizer4j.test.sample.generics.GoodConcreteGenericsContainer.c : UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE"));
    }

}
