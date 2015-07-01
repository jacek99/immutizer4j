package org.immutizer4j.test.sample;

import lombok.Value;

/**
 * A POJO that has our custom immutable collection as a field.
 * It should fail in the default immutizer, but pass if we create a custom one that recognizes our custom
 * immutable collection interface
 */
@Value
public class CustomImmutableCollectionPojo {
    private int testInt;
    private ICustomImmutableCollection<Integer> immutableIntegers;
}
