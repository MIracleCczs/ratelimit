package com.miracle.ratelimit.handler.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.util.CollectionUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miracle.ratelimit.handler.RateLimitConfigCacheService;
import com.miracle.ratelimit.support.RateLimitConfig;

public class RateLimitConfigCacheServiceImpl implements RateLimitConfigCacheService {

    private Cache<String, List<RateLimitConfig>> configCache;

    @Override
    public List<RateLimitConfig> get(String key) {
        return configCache.getIfPresent(key);
    }

    @Override
    public void set(Map<String, List<RateLimitConfig>> rateLimitConfigsMap) {
        for (Map.Entry<String, List<RateLimitConfig>> entry : rateLimitConfigsMap.entrySet()) {
            String key = entry.getKey();
            List<RateLimitConfig> value = entry.getValue();
            if (!CollectionUtils.isEmpty(value)) {
                value.sort((o1, o2) -> o2.getSort().compareTo(o1.getSort()));
                configCache.put(key, value);
            }
        }
    }

    @Override
    public void clear() {
        configCache.invalidateAll();
    }

    private void initCache() {
        configCache = CacheBuilder.newBuilder()
                .initialCapacity(10)
                .maximumSize(1000)
                .build();
    }

    @PostConstruct
    public void init() {
        if (configCache == null) {
            initCache();
        }
    }

}