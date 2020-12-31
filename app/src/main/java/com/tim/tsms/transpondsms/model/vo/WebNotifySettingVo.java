package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;

public class WebNotifySettingVo implements Serializable {
    private String token;

    public WebNotifySettingVo() {
    }

    public WebNotifySettingVo(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
