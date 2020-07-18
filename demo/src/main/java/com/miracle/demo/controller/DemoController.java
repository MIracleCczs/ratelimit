package com.miracle.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miracle.ratelimit.annotation.RateLimit;
import com.miracle.ratelimit.handler.RateLimitConfigCacheService;

@RequestMapping("/demo")
@Controller
public class DemoController {

    @Autowired
    private RateLimitConfigCacheService rateLimitConfigCacheService;

    @RequestMapping("/rate")
    @ResponseBody
    @RateLimit(remoteKey = "test")
    public Object testRate() {
        return "OK";
    }

    @RequestMapping("/config")
    @ResponseBody
    public Object getNacosConfig() {
        return rateLimitConfigCacheService.get("test");
    }

}