package com.qunhe.instdeco.counterslow.service.crud;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author tumei
 */

@Service
@AllArgsConstructor
public class DynamicConfigService {
    private final StringRedisTemplate redisTemplate;
    private static final String TETRIS_CK_KEY = "PANGTUTU_SERVICE:TETRIS_CK";

    public String getTetrisCk() {
        return redisTemplate.opsForValue().get(TETRIS_CK_KEY);
    }

    public void setTetrisCk(String ck) {
        redisTemplate.opsForValue().set(TETRIS_CK_KEY, ck);
    }
}
