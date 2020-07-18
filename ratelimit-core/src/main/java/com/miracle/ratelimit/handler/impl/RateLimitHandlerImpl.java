package com.miracle.ratelimit.handler.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.miracle.ratelimit.controller.RateLimitController;
import com.miracle.ratelimit.controller.factory.ControllerFactory;
import com.miracle.ratelimit.exception.RateLimitException;
import com.miracle.ratelimit.handler.RateLimitControllerCacheService;
import com.miracle.ratelimit.handler.RateLimitHandler;
import com.miracle.ratelimit.support.RateLimitConfig;

public class RateLimitHandlerImpl implements RateLimitHandler {

    @Autowired
    private ControllerFactory controllerFactory;

    @Autowired
    private RateLimitControllerCacheService rateLimitControllerCacheService;

    @Override
    public boolean doCheck(String key, List<RateLimitConfig> rateLimitConfigs, Map<String, Object> params) {
        for (RateLimitConfig rateLimitConfig : rateLimitConfigs) {
            // 根据参数值获取动态key
            List<String> paramNames = rateLimitConfig.getParamNames();

            String finalKey = key;
            if (!CollectionUtils.isEmpty(paramNames)) {
                finalKey = this.getFinalKey(key, paramNames, params);
            }

            // 根据key从guava cache中获取
            RateLimitController rateLimitController = rateLimitControllerCacheService.get(finalKey);
            // 未获取到，创建一个
            if (rateLimitController == null) {
                rateLimitController = controllerFactory.getInstance(key, rateLimitConfig);
            }
            // 再次判断，如果为空，则抛出异常
            if (rateLimitController == null) {
                throw new RateLimitException("get rate limit controller failed");
            }
            // 放入缓存
            rateLimitControllerCacheService.set(key, rateLimitController);
            if (!rateLimitController.check()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据参数值获取动态key
     * @param key 配置中心key
     * @param paramNames 参数名列表
     * @param params 参数值
     * @return
     */
    private String getFinalKey(String key, List<String> paramNames, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(key);

        for (String paramName : paramNames) {
            String paramValue = MapUtils.getString(params, paramName);
            if (StringUtils.isNotBlank(paramValue)) {
                sb.append("$$").append(paramValue);
            }

        }
        return sb.toString();
    }
}