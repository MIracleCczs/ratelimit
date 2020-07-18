package com.miracle.ratelimit.component;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.miracle.ratelimit.aspect.RateLimitAspect;
import com.miracle.ratelimit.controller.factory.ControllerFactory;
import com.miracle.ratelimit.controller.factory.DefaultControllerFactory;
import com.miracle.ratelimit.handler.RateLimitHandler;
import com.miracle.ratelimit.handler.impl.RateLimitConfigCacheServiceImpl;
import com.miracle.ratelimit.handler.impl.RateLimitControllerCacheServiceImpl;
import com.miracle.ratelimit.handler.impl.RateLimitHandlerImpl;
import com.miracle.ratelimit.support.AbstractCacheClient;
import com.miracle.ratelimit.support.NacosClient;

@Component
public abstract class RateLimitManager {

    @Bean
    public RateLimitAspect rateLimitAspect() {
        return new RateLimitAspect();
    }

    @Bean
    public RateLimitHandler rateLimitHandler() {
        return new RateLimitHandlerImpl();
    }

    @Bean
    public ControllerFactory controllerFactory() {
        return new DefaultControllerFactory();
    }

    @Bean
    public NacosClient nacosClient() {
        return new NacosClient();
    }

    @Bean
    public RateLimitConfigCacheServiceImpl rateLimitConfigCacheService() {
        return new RateLimitConfigCacheServiceImpl();
    }

    @Bean
    public RateLimitControllerCacheServiceImpl rateLimitControllerCacheService() {
        return new RateLimitControllerCacheServiceImpl();
    }

    @Bean
    public abstract AbstractCacheClient cacheClient();
}