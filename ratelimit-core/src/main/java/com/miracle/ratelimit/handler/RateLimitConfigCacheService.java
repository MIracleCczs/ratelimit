package com.miracle.ratelimit.handler;

import java.util.List;
import java.util.Map;

import com.miracle.ratelimit.support.RateLimitConfig;

public interface RateLimitConfigCacheService {

    List<RateLimitConfig> get(String key);

    void set(Map<String, List<RateLimitConfig>> rateLimitConfigsMap);

    void clear();
}