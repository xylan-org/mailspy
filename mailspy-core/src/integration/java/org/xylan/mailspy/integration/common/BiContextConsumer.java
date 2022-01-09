package org.xylan.mailspy.integration.common;

@FunctionalInterface
public interface BiContextConsumer<A, B> {

    void accept(A a, B b) throws Throwable;

}
