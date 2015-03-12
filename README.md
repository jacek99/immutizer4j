# immutizer4j
Library to validate immutability of Java object graphs

This library can validate an entire Java object graph and verify all of its elements are truly immutable.
To be immutable all fields must be final and the only collections allowed are Google Guava Immutable Collections.

We recommend using Lombok to easily create immutable objects in Java using @RequiredArgsConstructror

http://projectlombok.org/features/Constructor.html


