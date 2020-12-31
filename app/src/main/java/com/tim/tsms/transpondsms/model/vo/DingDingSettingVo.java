package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;

public class DingDingSettingVo implements Serializable {
    private String token;
    private String secret;

    public DingDingSettingVo() {
    }

    public DingDingSettingVo(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
