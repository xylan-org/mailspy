/*
 * Copyright (c) 2022 xylan.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
