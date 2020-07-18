package com.miracle.ratelimit.aspect;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.miracle.ratelimit.annotation.RateLimit;
import com.miracle.ratelimit.handler.RateLimitConfigCacheService;
import com.miracle.ratelimit.handler.RateLimitHandler;
import com.miracle.ratelimit.support.RateLimitConfig;
import com.miracle.ratelimit.support.RateLimitResult;
import com.miracle.ratelimit.support.RequestUtil;

@Aspect
public class RateLimitAspect {

    @Autowired
    private RateLimitConfigCacheService rateLimitConfigCacheService;

    @Autowired
    private RateLimitHandler rateLimitHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitAspect.class);

    public RateLimitAspect() {
        LOGGER.info("init success");
    }

    @Pointcut("@annotation(com.miracle.ratelimit.annotation.RateLimit)")
    public void rateLimitPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("rateLimitPointcut()")
    public Object rateLimitAround(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取注解
        RateLimit rateLimit = getAnnotation(joinPoint);
        if (rateLimit != null) {
            // 配置key
            String key = rateLimit.remoteKey();
            // 获取配置信息
            List<RateLimitConfig> rateLimitConfigs = rateLimitConfigCacheService.get(key);
            // 获取参数信息
            Map<String, Object> params = RequestUtil.getParam();
            // 执行流控校验
            boolean result = rateLimitHandler.doCheck(key, rateLimitConfigs, params);
            if (!result) {
                return new RateLimitResult(false, "当前请求流控命中");
            }
        } else {
            LOGGER.warn("未获取到注解信息");
        }

        return joinPoint.proceed();
    }

    /**
     * 获取方法中的注解信息
     */
    private RateLimit getAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(RateLimit.class);
        }
        return null;
    }

}