package org.immutizer4j.test.sample;

import lombok.Value;

/**
 * Arrays are tricky
 * Technically mutable (you can always change the element stored at an index)
 */
@Value
public class ArrayPojo {
    private String[] nonMutableArray;
    private NonFinalFieldsPojo[] mutableArray;
}
