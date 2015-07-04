package org.immutizer4j.test.sample.generics;

import lombok.Value;

/**
 * A test for various generics containers
 */
@Value
public class GenericsContainer<A,B,C> implements IGenericsContainer<A,B,C> {
    private A a;
    private B typeB;
    private C c;
}
