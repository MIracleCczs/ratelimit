package com.miracle.ratelimit.support;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {
    private RequestUtil() {
    }

    public static Map<String, Object> getParam() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String[]> parameter = request.getParameterMap();
        Map<String, Object> rParams = new HashMap<>();
        for (Map.Entry<String, String[]> m : parameter.entrySet()) {
            String key = m.getKey();
            Object[] obj = parameter.get(key);
            rParams.put(key, (obj == null || obj.length > 1) ? obj : obj[0]);
        }
        return rParams;
    }
}
