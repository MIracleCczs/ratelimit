package com.miracle.ratelimit.controller.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;

import com.miracle.ratelimit.controller.RateLimitController;
import com.miracle.ratelimit.exception.RateLimitException;
import com.miracle.ratelimit.support.AbstractCacheClient;

/**
 * 全局流控控制器
 */
public class GlobalRateLimitController implements RateLimitController {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalRateLimitController.class);

    /**
     * lua script sha code
     */
    private static volatile String redisScriptSha;

    private AbstractCacheClient cacheClient;
    /**
     * remote key
     */
    private String key;

    /**
     * 每秒允许请求数
     */
    private Double permitsPerSecond;

    public GlobalRateLimitController(AbstractCacheClient cacheClient, String key, Double permitsPerSecond) {
        this.cacheClient = cacheClient;
        this.key = key;
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public boolean check() {
        if (key == null || permitsPerSecond == 0) {
            return true;
        }
        if (redisScriptSha == null) {
            initShaCodeLazy();
        }
        // 令牌桶的容量，允许在一秒钟内完成的最大请求数 默认是2倍 未做成可配置（TODO）
        String maxPermits = String.valueOf(permitsPerSecond * 2);

        // key
        String tokenKey = key + "}.tokens";
        String timestampKey = key + "}.timestamp";

        Long ret = cacheClient.eval(redisScriptSha, 2, tokenKey, timestampKey, String.valueOf(permitsPerSecond), maxPermits, Instant.now().getEpochSecond() + "", "1");
        return Objects.equals(ret, 1L);
    }

    /**
     * 懒加载
     */
    private void initShaCodeLazy() {
        if (redisScriptSha != null) {
            //已经初始化好了,double check
            return;
        }
        synchronized (this) {
            try {
                String script = new ResourceScriptSource(
                        new ClassPathResource("META-INF/rate-limit.lua")).getScriptAsString();
                redisScriptSha = cacheClient.scriptLoad(script);
            } catch (IOException e) {
                LOGGER.error("load resource [rate-limit.lua] failed", e);
                throw new RateLimitException("load resource [rate-limit.lua] failed");
            }
        }
    }
}