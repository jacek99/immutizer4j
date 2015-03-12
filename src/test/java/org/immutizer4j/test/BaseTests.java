package org.immutizer4j.test;

import lombok.Value;

/**
 * @author Jacek Furmankiewicz
 */
public class BaseTests {

    @Value // thank you Lombok
    private static class CoreTypes {
        private byte primitiveByte;
        private boolean primitiveBoolean;
    }

}
