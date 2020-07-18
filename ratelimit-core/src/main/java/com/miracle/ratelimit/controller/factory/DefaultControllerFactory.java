package com.miracle.ratelimit.controller.factory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.miracle.ratelimit.controller.RateLimitController;
import com.miracle.ratelimit.controller.impl.GlobalRateLimitController;
import com.miracle.ratelimit.controller.impl.StandAloneRateLimitController;
import com.miracle.ratelimit.exception.RateLimitException;
import com.miracle.ratelimit.support.AbstractCacheClient;
import com.miracle.ratelimit.support.ModelEnum;
import com.miracle.ratelimit.support.RateLimitConfig;

public class DefaultControllerFactory implements ControllerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultControllerFactory.class);

    @Autowired
    private AbstractCacheClient cacheClient;

    /**
     * 解析输入参数构造并初始化具体的流控类
     *
     * @param key             配置的远程key nanos配置的dataId
     * @param rateLimitConfig 配置项
     * @return
     */
    @Override
    public RateLimitController getInstance(String key, RateLimitConfig rateLimitConfig) {

        if (rateLimitConfig != null) {
            String model = StringUtils.upperCase(rateLimitConfig.getModel());
            // 单机流控
            if (StringUtils.equals(model, ModelEnum.STANDALONE.toString())) {
                return new StandAloneRateLimitController(rateLimitConfig.getPermitsPerSecond(),
                        rateLimitConfig.getWarmupPeriod(), rateLimitConfig.getSync(), rateLimitConfig.getTimeout());
            } else if (StringUtils.equals(model, ModelEnum.GLOBAL.toString())) {
                return new GlobalRateLimitController(cacheClient, key, rateLimitConfig.getPermitsPerSecond());
            } else {
                throw new RateLimitException("必须选择校验模式");
            }
        }
        LOGGER.warn("限流器构造失败，配置项为null");
        return null;
    }
}