package org.immutizer4j;

import lombok.Value;
import org.immutizer4j.ImmutizerIgnore;

import java.util.Arrays;

/**
 * A workaround for the fact that immutable arrays are not possible in Java
 * Wraps a copy of the source array to ensure it cannot get modified
 *
 * Idea borrowed from Stack Overflow post:
 * http://stackoverflow.com/questions/3700971/immutable-array-in-java
 */
@Value
public class ImmutableArray<T> {

    private T[] array;

    /**
     * Constructor. Creates a copy of the source array
     */
    public ImmutableArray(T[] a){
        array = Arrays.copyOf(a, a.length);
    }

    public T get(int index){
        return array[index];
    }
}
