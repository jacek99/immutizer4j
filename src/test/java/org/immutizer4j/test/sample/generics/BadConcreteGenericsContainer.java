package org.immutizer4j.test.sample.generics;

import lombok.Value;

/**
 * A class with concrete generics reference
 * It violates immutability, as one of the types is mutable
 * However, Java erases type into to this degree that we cannot determine it either way, so we have to reject it
 */
@Value
public class BadConcreteGenericsContainer<ImmutablePojo, ImmutablePojo2, NonFinalFieldsPojo>
        implements IGenericsContainer<ImmutablePojo,ImmutablePojo2,NonFinalFieldsPojo> {
    private ImmutablePojo a;
    private ImmutablePojo2 typeB;
    // this one should flag this entire type as a violation
    private NonFinalFieldsPojo c;
}
