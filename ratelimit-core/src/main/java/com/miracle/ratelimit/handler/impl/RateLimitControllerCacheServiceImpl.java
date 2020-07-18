package com.miracle.ratelimit.handler.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miracle.ratelimit.controller.RateLimitController;
import com.miracle.ratelimit.handler.RateLimitControllerCacheService;

public class RateLimitControllerCacheServiceImpl implements RateLimitControllerCacheService {

    private Cache<String, RateLimitController> caches;
    private int capacity = 1000;
    private int maximumSize = 100000;
    private long cacheExpireSeconds = 300;

    @PostConstruct
    public void init() {
        caches = CacheBuilder.newBuilder().expireAfterWrite(cacheExpireSeconds, TimeUnit.SECONDS)
                .initialCapacity(capacity)
                .maximumSize(maximumSize)
                .build();
    }

    @Override
    public RateLimitController get(String key) {
        return caches.getIfPresent(key);
    }

    @Override
    public void set(String key, RateLimitController rateLimitController) {
        caches.put(key, rateLimitController);
    }

    @Override
    public void clear() {
        caches.invalidateAll();
    }
}