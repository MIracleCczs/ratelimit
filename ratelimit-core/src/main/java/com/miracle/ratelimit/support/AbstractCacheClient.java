package com.miracle.ratelimit.support;

public abstract class AbstractCacheClient {

    /**
     * redis执行入口
     * @param shaCode
     * @param keyCount
     * @param params
     * @return
     */
    public abstract Long eval(String shaCode, int keyCount, String... params);

    /**
     * lua脚本加载到redis
     * @param script
     * @return
     */
    public abstract String scriptLoad(String script);

}
