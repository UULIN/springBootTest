package com.qunhe.instdeco.counterslow.common.util;

import com.google.common.collect.ImmutableList;
import com.qunhe.instdeco.counterslow.common.func.ThrowingSupplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author tumei
 */

@Slf4j
public class Retryer {
    private final Integer maxRetryCount;
    private final List<Long> durationList;
    private static final Integer DEFAULT_RETRY_COUNT = 5;
    private static final List<Long> DEFAULT_DURATION_LIST = ImmutableList.of(
            TimeUnit.SECONDS.toMillis(5),
            TimeUnit.SECONDS.toMillis(30),
            TimeUnit.MINUTES.toMillis(5),
            TimeUnit.MINUTES.toMillis(30),
            TimeUnit.HOURS.toMillis(2)
    );

    /**
     * @param maxRetryCount Integer
     * @param durationList List<Long>
     */
    private Retryer(final Integer maxRetryCount, final List<Long> durationList) {
        if (durationList.size() < maxRetryCount) {
            throw new RuntimeException("durationList is not enough for maxRetryCount");
        }
        if (maxRetryCount < 1) {
            throw new RuntimeException("invalid maxRetryCount");
        }
        this.maxRetryCount = maxRetryCount;
        this.durationList = durationList;
    }

    @SuppressWarnings("unused")
    public static Retryer newSimpleRetryer() {
        return newRetryer(DEFAULT_RETRY_COUNT, DEFAULT_DURATION_LIST);
    }

    @SuppressWarnings("unused")
    public static Retryer newRetryerWithCount(final Integer maxRetryCount) {
        return newRetryer(maxRetryCount, DEFAULT_DURATION_LIST);
    }

    @SuppressWarnings("unused")
    public static Retryer newRetryerWithDuration(final List<Long> durationList) {
        return newRetryer(DEFAULT_RETRY_COUNT, durationList);
    }

    @SuppressWarnings("WeakerAccess")
    public static Retryer newRetryer(final Integer maxRetryCount, final List<Long> durationList) {
        return new Retryer(maxRetryCount, durationList);
    }

    @SneakyThrows
    public <T> T execWithRetry(ThrowingSupplier<T> supplier) {
        int currentExecCount = 0;
        while (currentExecCount < maxRetryCount) {
            try {
                return supplier.get();
            } catch (Exception err) {
                log.error("execWithRetry {}, {}", supplier, currentExecCount, err);
                val nextWaitDuration = durationList.get(currentExecCount);
                currentExecCount++;
                // 用sleep其实不太优雅, 如果请求很多, 会有很多sleep线程阻塞
                // 不过这里只有定时任务会调用这个, 最大并行度也就1, 暂且就酱紫吧
                Thread.sleep(nextWaitDuration);
            }
        }
        throw new RuntimeException(String.format("execWithRetry %s fail after %d times", supplier.toString(), maxRetryCount));
    }
}
