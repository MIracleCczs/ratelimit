package com.miracle.ratelimit.controller.factory;

import com.miracle.ratelimit.controller.RateLimitController;
import com.miracle.ratelimit.support.RateLimitConfig;

public interface ControllerFactory {

    /**
     * 根据不同配置创建不同的流控器
     * @param key remote key
     * @param rateLimitConfig 配置 信息
     * @return
     */
    RateLimitController getInstance(String key, RateLimitConfig rateLimitConfig);
}