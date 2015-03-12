package org.immutizer4j;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Commonly used constants
 *
 * @author Jacek Furmankiewicz
 */
class ImmutizerConstants {

    /**
     * Types the immutizer recognizes by default as immutable (if flagged as final)
     */
    static Set<Class<?>> KNOWN_TYPES =
            ImmutableSet.<Class<?>>builder()
                    // primitive types
                    .add(byte.class,boolean.class,short.class,int.class,long.class,float.class,double.class,char.class)
                    // object types
                    .add(Byte.class,Boolean.class,Short.class,Integer.class,Long.class,Float.class,Double.class,
                            Character.class,String.class)
                    // Guava Immutable collections
                    .add(ImmutableCollection.class)
                    .build();

}
