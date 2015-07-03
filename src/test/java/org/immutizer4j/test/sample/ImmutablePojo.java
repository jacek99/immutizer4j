package org.immutizer4j.test.sample;

import lombok.Value;

/**
 * Nice proper immutable POJO
 */
@Value
public class ImmutablePojo {
    private int testInt;
    private double testDouble;
    private String testString;
}
