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
     * e.g. ImmutableSet&lt;MyPojo&gt;. It is still not 100% immutable in this case.
     */
    MUTABLE_TYPE_STORED_IN_COLLECTION,
    /**
     * Arrays are by default mutable, no way around it in Java (see our ImmutableArray though)
     * However, under certain conditions due to performance or memory requirements we may allow them
     * if you set strict=false in the Immutizer
     */
    MUTABLE_ARRAY,
    /**
     * Used if we tried to find the generic type, but it may use a wildcard like ?
     * in which case we cannot instantiate it
     */
    GENERIC_TYPE_WITH_WILDCARD,
    /**
     * Sometimes we just can't get the type. In this case we flag it as an error since it is not possible
     * for us to safely determine immutability (all we have is java.lang.Object)
     */
    UNABLE_TO_DETERMINE_TYPE_DUE_TO_GENERICS_TYPE_ERASURE

}
