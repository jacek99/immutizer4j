package org.immutizer4j.test.sample.generics;

/**
 * An interface that allows to test objects that are driven by generics
 * Any of the generic types (A,B,C) could ge mutable or not, so we have to handle these types of combinations
 * and test for them.
 */
public interface IGenericsContainer<A,B,C> {
    // test single character var names
    A getA();

    // test regult Java var name
    B getTypeB();

    C getC();
}
