package org.immutizer4j.test.sample;

import lombok.Value;

@Value
public class FinalParentWithNonFinalFieldsPojo {
    // @Value will make this one OK
    private int finalInt;
    // @Value will make this one final , but its own fields are NOT final, so it should get kicked out
    private NonFinalFieldsPojo finalChildWithNonFinalFields;
}
