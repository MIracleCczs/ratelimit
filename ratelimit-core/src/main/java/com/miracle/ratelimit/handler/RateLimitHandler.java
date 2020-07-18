package com.miracle.ratelimit.handler;

import java.util.List;
import java.util.Map;

import com.miracle.ratelimit.support.RateLimitConfig;

public interface RateLimitHandler {

    boolean doCheck(String key, List<RateLimitConfig> rateLimitConfigs, Map<String, Object> params);
}