package com.qunhe.instdeco.counterslow.configuration;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author tumei
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
    public static final String DEPS_DIFF = "deps_diff";
    public static final String SQL_TOTAL = "sql_total";
    public static final String SQL_DETAIL = "sql_detail";
    public static final String SQL_DIFF = "sql_diff";

    private static final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(30);
    private static final long DEFAULT_MAX_IDLE = TimeUnit.HOURS.toMillis(1);

    private static CacheConfig buildCacheConfig() {
        return buildCacheConfig(DEFAULT_TTL);
    }

    private static CacheConfig buildCacheConfig(long ttl) {
        return new CacheConfig(ttl, DEFAULT_MAX_IDLE);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        val config = ImmutableMap.<String, CacheConfig>builder()
                .put(DEPS_DIFF, buildCacheConfig())
                .put(SQL_TOTAL, buildCacheConfig())
                .put(SQL_DETAIL, buildCacheConfig())
                .put(SQL_DIFF, buildCacheConfig())
                .build();
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
