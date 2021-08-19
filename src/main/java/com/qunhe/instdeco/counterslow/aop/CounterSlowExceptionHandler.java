package com.qunhe.instdeco.counterslow.aop;

import com.qunhe.instdeco.counterslow.common.ServiceResultPlain;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tumei
 */
@RestControllerAdvice
@Slf4j
public class CounterSlowExceptionHandler {
    @ExceptionHandler
    public ServiceResultPlain apiExceptionHandler(HttpServletRequest request, Throwable throwable) {
        val method = request.getMethod();
        val path = request.getServletPath();
        log.error("{} {} failed, ", method, path, throwable);
        return ServiceResultPlain.fail(throwable.getMessage());
    }
}
