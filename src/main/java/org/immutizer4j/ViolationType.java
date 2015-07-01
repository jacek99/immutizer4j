package org.immutizer4j;

/**
 * Violation reason
 *
 * @author Jacek Furmankiewicz
 */
public enum ViolationType {
    /**
     * Fields was not marked as final
     */
    NON_FINAL_FIELD,
    /**
     * Type is mutable, so even if a field of this type is marked as final
     * it's internal state can still be changed. Hence it is still mutable.
     */
    MUTABLE_TYPE,
    /**
     * Used if the type stored within an immutable collection is mutable itself,
     * e.g. ImmutableSet<MyPojo>. It is still not 100% immutable in this case.
     */
    MUTABLE_TYPE_STORED_IN_COLLECTION
}
