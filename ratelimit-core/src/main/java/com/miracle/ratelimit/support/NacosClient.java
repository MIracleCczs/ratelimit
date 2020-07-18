package com.miracle.ratelimit.support;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.miracle.ratelimit.exception.RateLimitException;
import com.miracle.ratelimit.handler.RateLimitConfigCacheService;
import com.miracle.ratelimit.handler.RateLimitControllerCacheService;

public class NacosClient implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(NacosClient.class);

    @Value("${rate-limit.nacos.serverAddr}")
    private String serverAddr;

    @Value("${rate-limit.nacos.group}")
    private String rateLimitGroup;

    @Value("${rate-limit.nacos.dataId}")
    private String dataId;

    @Value("${rate-limit.nacos.timeoutMs}")
    private long timeoutMs;

    @Value("${rate-limit.nacos.namespace}")
    private String namespace;

    private ConfigService configService;

    @Autowired
    private RateLimitConfigCacheService rateLimitConfigCacheService;

    @Autowired
    private RateLimitControllerCacheService rateLimitControllerCacheService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        configService = NacosFactory.createConfigService(properties);

        initRateLimitConfigs();
        registerRateLimitConfigListener();
    }

    /**
     * 初始化记载配置信息
     */
    private void initRateLimitConfigs() {
        loadRateLimitConfigs();
    }

    /**
     * 加载配置
     */
    private void loadRateLimitConfigs() {
        try {
            String config = this.configService.getConfig(dataId, rateLimitGroup, timeoutMs);
            if (StringUtils.isNotBlank(config)) {
                rateLimitConfigCacheService.set(this.convert(config));
            }
        } catch (NacosException e) {
            LOGGER.error("load rate limit config error", e);
        }
    }

    /**
     * 注册监听配置
     */
    private void registerRateLimitConfigListener() {
        try {
            this.configService.addListener(dataId, rateLimitGroup, new RateLimitListener());
        } catch (NacosException e) {
            LOGGER.error("register rate limit config listener error", e);
        }
    }

    /**
     * 将配置进行格式转换
     * @param config
     * @return
     */
    private Map<String, List<RateLimitConfig>> convert(String config) {
        if (StringUtils.isNotBlank(config)) {
            try {
                JSONObject configMap = JSON.parseObject(config);
                return configMap.entrySet()
                        .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> JSON.parseArray(JSON.toJSONString(entry.getValue()), RateLimitConfig.class)));
            } catch (Exception var) {
                throw new RateLimitException("解析参数错误");
            }
        }
        return null;
    }

    /**
     * 监听器实现
     */
    private class RateLimitListener implements Listener {

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String s) {
            // 重加载需要加锁
            synchronized (RateLimitListener.class) {
                // 如果数据有变动，清理后重新加载
                rateLimitConfigCacheService.clear();
                rateLimitControllerCacheService.clear();

                loadRateLimitConfigs();
            }
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("serverAddr", "172.30.68.113:8848");
        properties.put("namespace", "1a20ffe1-c776-4a1f-bae7-93622bac571f");
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            String config = configService.getConfig("test", "DEFAULT_GROUP", 1000);
            System.out.println(config);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

}