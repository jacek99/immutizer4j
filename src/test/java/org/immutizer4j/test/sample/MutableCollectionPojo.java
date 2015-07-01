package org.immutizer4j.test.sample;

import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * A POJO where all the fields are final, but one of them is a standard mutable collection
 */
@Value
public class MutableCollectionPojo {
    private int testInt;
    private Integer testInteger;
    private String testString;
    private Double testDouble;
    private double testDbl;
    // this may be final via @Value, but the collection itself is mutable
    // so it should get kicked out
    private List<Integer> listInt;
}
