package org.immutizer4j.test;

import lombok.Value;
import org.immutizer4j.Immutizer;
import org.immutizer4j.ValidationResult;
import org.immutizer4j.test.sample.NonFinalFieldClass;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jacek Furmankiewicz
 */
public class BaseTests {

    private Immutizer defaultImmutizer = new Immutizer();

    @Test
    public void testNonFinalFields() {
        ValidationResult result = defaultImmutizer.verify(NonFinalFieldClass.class);

        // we should have gotten 5 errors
        Assert.assertEquals(false,result.isValid());
        Assert.assertEquals(5,result.getErrors().size());
    }

}
