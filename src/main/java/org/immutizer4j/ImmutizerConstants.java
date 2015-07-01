package org.immutizer4j;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Commonly used constants
 *
 * @author Jacek Furmankiewicz
 */
class ImmutizerConstants {

    /**
     * For performance, automatically interns it in the JVM,
     * lowers memory usage, GC pressure
     */
    public static final String FIELD_SEPARATOR = ".";
    public static final String MSG_SEPARATOR = " : ";
    public static final String NEWLINE = "\n ";

    /**
     * Types the immutizer recognizes by default as immutable (if flagged as final)
     */
    static Set<Class<?>> KNOWN_TYPES =
            ImmutableSet.<Class<?>>builder()
                    // primitive types
                    .add(byte.class,boolean.class,short.class,int.class,long.class,float.class,double.class,char.class)
                    // object types
                    .add(Byte.class, Boolean.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
                            Character.class, String.class)
                    // Guava Immutable collections
                    .add(ImmutableCollection.class)
                    .build();
}
