package org.immutizer4j.test.sample;

import com.google.common.collect.ImmutableList;
import lombok.Value;

import java.util.List;

/**
 * A POJO where all the fields are final, but one of them is a standard mutable collection
 */
@Value
public class ImmutableCollectionPojo {
    private int testInt;
    private Integer testInteger;
    private String testString;
    private Double testDouble;
    private double testDbl;
    // immitable collection, so it should pass without errors
    private ImmutableList<Integer> listInt;
}
