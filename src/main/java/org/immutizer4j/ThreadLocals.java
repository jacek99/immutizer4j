package org.immutizer4j;

import java.util.StringJoiner;

/**
 * Internal thread local variables for max performance
 *
 * @author Jacek Furmankiewicz
 */
class ThreadLocals {

    /**
     * Thread-local StringBuilder, avoids the overhead of instantiating a new one every time
     * It gets reset upon every get() operation
     */
    public static final ThreadLocal<StringBuilder> STRINGBUILDER = new ThreadLocal() {
        @Override
        public StringBuilder initialValue() {
            return new StringBuilder(50);
        }

        @Override
        public Object get() {
            StringBuilder bld = (StringBuilder) super.get();
            bld.setLength(0);
            return bld;
        }
    };

}
