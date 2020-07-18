package com.miracle.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.miracle.ratelimit.component.RateLimitManager;
import com.miracle.ratelimit.support.AbstractCacheClient;

@Component
public class DemoConfig extends RateLimitManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public AbstractCacheClient cacheClient() {
        return new AbstractCacheClient() {
            @Override
            public Long eval(String shaCode, int keyCount, String... scriptParamsAndValues) {
                return (Long) redisTemplate.execute((RedisConnection connection) -> connection.evalSha(shaCode.getBytes(),
                        ReturnType.fromJavaType(Long.class),
                        2,
                        scriptParamsAndValues[0].getBytes(),
                        scriptParamsAndValues[1].getBytes(),
                        scriptParamsAndValues[2].getBytes(),
                        scriptParamsAndValues[3].getBytes(),
                        scriptParamsAndValues[4].getBytes(),
                        scriptParamsAndValues[5].getBytes()
                ));
            }

            @Override
            public String scriptLoad(String script) {
                return redisTemplate.getConnectionFactory().getConnection().scriptLoad(script.getBytes());
            }
        };
    }
}