package com.qunhe.instdeco.counterslow.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author tumei
 */
@Slf4j
@SuppressWarnings(value = {"unused", "WeakerAccess"})
public class CljThreadPoolUtil {
    private static final ExecutorService DEFAULT_IO_THREAD_POOL = new ThreadPoolExecutor(
            Math.max(Runtime.getRuntime().availableProcessors() * 2, 16),
            Math.max(Runtime.getRuntime().availableProcessors() * 2, 16),
            0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder()
            .setNameFormat("clj-like-thread-pool-io-%d").build());

    private static final ExecutorService DEFAULT_CPU_THREAD_POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() + 1,
            Runtime.getRuntime().availableProcessors() + 1,
            0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder()
            .setNameFormat("clj-like-thread-pool-cpu-%d").build());

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <R, P> List<R> pmap(Function<P, R> op, List<P> ps, ExecutorService pool) {
        val size = ps.size();
        val resArr = new Object[size];
        val countDownLatch = new CountDownLatch(size);
        Function<Supplier<R>, CompletableFuture<R>> autoSupplyAsync = s -> pool == null
                ? CompletableFuture.supplyAsync(s)
                : CompletableFuture.supplyAsync(s, pool);
        int idx = 0;
        for (final P p : ps) {
            int i = idx++;
            autoSupplyAsync.apply(() -> op.apply(p))
                    .whenComplete((res, ex) -> {
                        resArr[i] = res;
                        countDownLatch.countDown();
                    }).exceptionally(e -> {
                log.error("pmap, op: {}, ps: {}, failed", op.toString(), ps.toString(), e);
                return null;
            });
        }
        countDownLatch.await();
        return Arrays.stream(resArr).map(res -> (R) res).collect(Collectors.toList());
    }

    public static <R, P> List<R> pmap(Function<P, R> op, List<P> ps) {
        return pmap(op, ps, null);
    }

    public static <R, P> List<R> pmap4cpu(Function<P, R> op, List<P> ps) {
        return pmap(op, ps, DEFAULT_CPU_THREAD_POOL);
    }

    public static <R, P> List<R> pmap4io(Function<P, R> op, List<P> ps) {
        return pmap(op, ps, DEFAULT_IO_THREAD_POOL);
    }

}
