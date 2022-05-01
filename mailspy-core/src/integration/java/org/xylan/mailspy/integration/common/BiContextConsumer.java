package org.xylan.mailspy.integration.common;

/**
 * Functional interface to use with {@link AbstractIntegrationTest#run(BiContextConsumer)}.
 * Main difference from built-in {@link java.util.function.BiConsumer} is the throws declaration, which is needed to handle MockMvc checked exceptions
 * in a global manner.
 * @param <A> Arbitrary argument type A.
 * @param <B> Arbitrary argument type B.
 */
@FunctionalInterface
public interface BiContextConsumer<A, B> {

    /**
     * Consume arguments.
     * @param a Arbitrary argument a.
     * @param b Arbitrary argument b.
     * @throws Throwable Arbitrary throwable thrown.
     */
    void accept(A a, B b) throws Throwable;

}
