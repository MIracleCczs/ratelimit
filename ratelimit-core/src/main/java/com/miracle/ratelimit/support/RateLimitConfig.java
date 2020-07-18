package com.miracle.ratelimit.support;

import java.util.List;

import javax.validation.constraints.NotNull;

public class RateLimitConfig {

    /**
     * 吞吐率：每秒允许请求数
     */
    @NotNull
    private Double permitsPerSecond;

    /**
     * 预热时间：从较低吞吐率到高吞吐率的耗时时间
     */
    private Long warmupPeriod;

    /**
     * 是否同步等待
     */
    private Boolean sync;

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 校验模式 必填
     * 单机和全局[standalone, global]
     */
    @NotNull
    private String model;

    /**
     * 优先级 非必填
     * 从大到小排列，大值优先级高
     * 比如同时设置了多个校验规则，包括单机和全局，则可以选择先校验全局还是先校验单机
     */
    private Integer sort = 0;

    /**
     * 参数名 用于动态流控时根据参数名获取参数值
     */
    private List<String> paramNames;


    public Double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(Double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public Long getWarmupPeriod() {
        return warmupPeriod;
    }

    public void setWarmupPeriod(Long warmupPeriod) {
        this.warmupPeriod = warmupPeriod;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    @Override
    public String toString() {
        return "RateLimitConfig{" +
                "permitsPerSecond=" + permitsPerSecond +
                ", warmupPeriod=" + warmupPeriod +
                ", sync=" + sync +
                ", timeout=" + timeout +
                ", model='" + model + '\'' +
                ", sort=" + sort +
                ", paramNames=" + paramNames +
                '}';
    }
}