package org.immutizer4j.test.sample;

import lombok.Data;

@Data
public class NonFinalFields {
    private int testInt;
    private Integer testInteger;
    private String testString;
    private Double testDouble;
    private double testDbl;
}
