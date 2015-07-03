package org.immutizer4j.test.sample;

import lombok.Value;

/**
 * A POJO that references ChildPojo
 * We want to ensure that when it walks down the object graph, it goes up the inheritance hierarchy not just for the
 * root object but for every referenced type as well
 */
@Value
public class ChildPojoReferencePojo {
    // final refernce to a mutable POJO with mutable fields in both parent and concrete type
    private ChildPojo childPojo;
}
