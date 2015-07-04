package org.immutizer4j.test.sample;

import lombok.Value;
import org.immutizer4j.ImmutableArray;

/**
 * A POJO that uses ImmutableArray to wrap both mutable and immutable object types
 * and ensure we correctly process both
 */
@Value
public class ImmutableArrayPojo {
    // this one is OK, as ImmutablePojo is obviously immutable
    private ImmutableArray<ImmutablePojo> immutablePojoArray;
    // this one is not OK, the ImmutableArray refers to a type that is mutable itself
    private ImmutableArray<NonFinalFieldsPojo> mutablePojoArray;
}
