/*
 * ServiceResultPlain.java
 * Copyright 2019 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.qunhe.instdeco.counterslow.common;

import com.qunhe.instdeco.counterslow.common.func.ThrowingSupplier;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author tumei
 */
@Data
@Builder
@Slf4j
@SuppressWarnings(value = {"unused", "WeakerAccess"})
public class ServiceResultPlain<T> implements Serializable {
    private static final long serialVersionUID = 4923919067393548576L;
    private Boolean success;
    private T data;
    private String err;
    private Integer code;

    /**
     * 成功返回
     *
     * @return T
     */
    public static <T> ServiceResultPlain<T> ok() {
        return ok(null, 0);
    }

    public static <T> ServiceResultPlain<T> ok(T data) {
        return ok(data, 0);
    }

    public static <T> ServiceResultPlain<T> ok(T data, Integer code) {
        return ServiceResultPlain.<T>builder()
                .success(true)
                .data(data)
                .code(code)
                .build();
    }

    /**
     * 失败返回
     *
     * @return T
     */
    public static <T> ServiceResultPlain<T> fail() {
        return fail(null, -1);
    }

    public static <T> ServiceResultPlain<T> fail(String err) {
        return fail(err, -1);
    }

    public static <T> ServiceResultPlain<T> fail(String err, Integer code) {
        return ServiceResultPlain.<T>builder()
                .success(false)
                .err(err)
                .code(code)
                .build();
    }

    /**
     * 自动返回
     *
     * @param supplier ThrowingSupplier<T>
     * @return T
     */
    public static <T> ServiceResultPlain<T> auto(ThrowingSupplier<T> supplier) {
        try {
            return ok(supplier.get());
        } catch (Throwable e) {
            log.error("auto exec {} fail,", supplier, e);
            return fail(e.getMessage());
        }
    }
}
