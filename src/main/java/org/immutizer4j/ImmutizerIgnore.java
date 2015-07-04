package org.immutizer4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An internal marker annotation that tells us it is OK to skip a violating field if it is explicitly marked as
 * immutable safe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface ImmutizerIgnore {}
