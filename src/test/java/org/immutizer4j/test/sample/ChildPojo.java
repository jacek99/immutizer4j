package org.immutizer4j.test.sample;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A POJO that extends the parent POJO
 * Used for testing whether we check all fields in an object's inheritance hierarchy
 */
@Data @EqualsAndHashCode(callSuper = true)
public class ChildPojo extends ParentPojo {
    private final int childImmutableInt = 0;
    private int childMutableInt = 0;
}
