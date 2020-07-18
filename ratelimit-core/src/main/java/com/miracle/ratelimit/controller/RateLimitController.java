package com.miracle.ratelimit.controller;

/**
 * 流控控制器
 */
public interface RateLimitController {

    /**
     * 执行流控
     * @return
     */
    boolean check();
}
