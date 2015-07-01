package org.immutizer4j.test.sample;

import lombok.Data;

@Data
public class NonFinalFieldsPojo {
    private int testInt;
    private Integer testInteger;
    private String testString;
    private Double testDouble;
    private double testDbl;
}
