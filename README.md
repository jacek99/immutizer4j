# immutizer4j

A small library to validate immutability of Java object graphs

This library can validate an entire Java object graph and verify all of its elements are truly immutable.
To be immutable all fields must be final and the only collections allowed are Google Guava Immutable Collections.

It can be extended with custom types that you can flag as immutable (e.g. a different immutable collection
library than Guava).

**Requires Java 8.**

# Use cases

Use this whenever you do have shared state (e.g. in-memory caches in ConcurrentHashMap) and you want to avoid
the **mutable** part of the *mutable shared state* sin.

Another good use case is for objects that are passed as messages between threads in JVM actor frameworks, such as Akka, Lois, etc.

# Functionality

* walks down the entire object graph
* ensures all fields in every type are final
* ensures only immutable collections are used
* ensures only immutable types are stored within collections (yes, it works around generics type erasure)

# Usage

You need to create an instance of *Immutizer* and store it in a static variable. You have the option of adding
additional custom types to the whitelist of object types if necessary at that time (see further down).

```java
private final static Immutizer immutizer = new Immutizer();

// verify the type of an object instance
immutizer.verify(instance);

// verify the type of an object
immutizer.verify(MyPojo.class);

// get the validation result without throwing an exception
ValidationResult result = immutizer.getValidationResult(MyPojo.class);
if (!result.isValid()) {
    // do something custom
}
```

That's it. 

A good place to put this is in the constructor of every in-memory cache you have to ensure only immutable objects
can get stored in it. As soon as a developer modifies the object graph to violate immutability it should throw an
exception on startup.

Or add it within your JUnit tests for every single object type that is cached in memory, passed as a message, etc.

# Adding custom immutable types

You can add additional types that Immutizer will treat as immutable directly in the constructor:

```java
private final static Immutizer immutizer = new Immutizer(MyCustomImmutableCollection.class);
```

But honestly, you should probably just look at Guava immutable collections, they should cover all reasonable
use cases: <https://github.com/google/guava/wiki/ImmutableCollectionsExplained>.

# Performance

Any type is validated only once. Each subsequent request returns a cached *immutable* (of course) validation result.
Therefore, overhead should be non-existant.

# Creating immutable classes with Lombok

We recommend using **Lombok** to easily create immutable objects in Java using **@Value**:

<http://projectlombok.org/features/Value.html>

It is used throughout this project for any immutable POJOs. 

# Dependencies

* SLF4J API (MIT License <http://slf4j.org/license.html>)
* Google Guava (Apache license <https://github.com/google/guava/blob/master/CONTRIBUTING.md>)

# License

This software is licensed under the BSD license.

# TODO Items

* handle checking of types within a collection (to deal with type erasure in Java generics)
* treat arrays as warnings (or optionally be strict and disallow them altogether)


