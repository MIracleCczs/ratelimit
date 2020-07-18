package com.miracle.ratelimit.controller.impl;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;
import com.miracle.ratelimit.controller.RateLimitController;

/**
 * 单机流控控制器
 */
public class StandAloneRateLimitController implements RateLimitController {

    private RateLimiter rateLimiter;
    /**
     * 是否直接以阻塞方式执行，默认不阻塞，为true会线程等待直到拿到令牌
     */
    private boolean sync = false;
    /**
     * 阻塞等待时间，0表示不阻塞，取不到直接返回
     */
    private Long waitLimit = 0L;

    /**
     *
     * @param permitsPerSecond 流控量
     * @param warmupPeriod 预热时间
     * @param sync 是否同步等待
     * @param waitLimit 等待超时时间
     */
    public StandAloneRateLimitController(double permitsPerSecond, Long warmupPeriod, Boolean sync, Long waitLimit) {
        if (sync != null) {
            this.sync = sync;
        }
        if (waitLimit != null) {
            this.waitLimit = waitLimit;
        }
        if (warmupPeriod != null) {
            /*WarmingUp方式*/
            rateLimiter = RateLimiter.create(permitsPerSecond, warmupPeriod, TimeUnit.MILLISECONDS);
        } else {
            /*Bursty方式*/
            rateLimiter = RateLimiter.create(permitsPerSecond);
        }
    }

    /**
     * 使用rateLimiter进行判断
     */
    @Override
    public boolean check() {
        if (rateLimiter == null) {
            return true;
        }
        if (!sync) {
            /*非阻塞模式*/
            return rateLimiter.tryAcquire(1, waitLimit, TimeUnit.MILLISECONDS);
        } else {
            /*阻塞模式*/
            rateLimiter.acquire(1);
            return true;
        }

    }
}