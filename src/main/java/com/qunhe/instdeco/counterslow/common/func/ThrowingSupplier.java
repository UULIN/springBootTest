package com.qunhe.instdeco.counterslow.common.func;

/**
 * @author tumei
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    /**
     * Throwing get
     *
     * @return T
     * @throws Throwable Throwable
     */
    T get() throws Throwable;
}
