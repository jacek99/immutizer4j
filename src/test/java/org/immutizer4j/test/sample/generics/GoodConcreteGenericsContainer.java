package org.immutizer4j.test.sample.generics;

import lombok.Value;

/**
 * A class with concrete generics reference
 * It is valid, as all 3 generic types it refers to are properly immutable
 * However, Java erases type into to this degree that we cannot determine it either way, so we have to reject it
 * */
@Value
public class GoodConcreteGenericsContainer<ImmutablePojo, ImmutablePojo2, ImmutablePojo3>
        implements IGenericsContainer<ImmutablePojo,ImmutablePojo2, ImmutablePojo3> {
    private ImmutablePojo a;
    private ImmutablePojo2 typeB;
    private ImmutablePojo3 c;
}
