package com.miracle.ratelimit.handler;

import com.miracle.ratelimit.controller.RateLimitController;

public interface RateLimitControllerCacheService {

    RateLimitController get(String key);

    void set(String key, RateLimitController rateLimitController);

    void clear();
}