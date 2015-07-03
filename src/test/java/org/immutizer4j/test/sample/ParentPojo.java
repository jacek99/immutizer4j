package org.immutizer4j.test.sample;

import lombok.Data;

/**
 * A POJO that will be used as an ancestor for other POJOs
 * Contains a mix of mutable and non-mutable fields
 */
@Data
public class ParentPojo {
    private final int parentImmutableInt = 0;
    private int paretMutableInt = 0;
}
