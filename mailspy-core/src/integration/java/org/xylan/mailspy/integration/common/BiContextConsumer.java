package org.xylan.mailspy.integration.common;

/**
 * Functional interface to use with {@link BaseIntegrationTest#run(BiContextConsumer)}.
 * Main difference from built-in {@link java.util.function.BiConsumer} is the throws declaration, which is needed to handle MockMvc checked exceptions
 * in a global manner.
 * @param <A> Arbitrary argument type A.
 * @param <B> Arbitrary argument type B.
 */
@FunctionalInterface
public interface BiContextConsumer<A, B> {

    /**
     * Consume arguments.
     * @param argument1 Arbitrary argument argument1.
     * @param argument2 Arbitrary argument argument2.
     * @throws Throwable Arbitrary throwable thrown.
     */
    void accept(A argument1, B argument2) throws Throwable;

}
