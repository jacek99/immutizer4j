package org.immutizer4j.test.sample;

import com.google.common.collect.ImmutableList;
import lombok.Value;

/**
 * A POJO where all the collections are immutable,
 * but the type stored in one of them is not
 */
@Value
public class ImmutableCollectionWithMutableElementPojo {
    private ImmutableList<Integer> immutableElementList;
    private ImmutableList<NonFinalFieldsPojo> mutableElementList;
}
