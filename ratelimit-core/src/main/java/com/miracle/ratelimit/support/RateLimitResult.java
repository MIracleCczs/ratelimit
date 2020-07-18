package com.miracle.ratelimit.support;

public class RateLimitResult {

    private Boolean isPass;

    private String errorMsg;

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public RateLimitResult(Boolean isPass, String errorMsg) {
        this.isPass = isPass;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "RateLimitResult{" +
                "isPass=" + isPass +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}