package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;

public class SmsExtraVo implements Serializable{
    Integer simId;
    String simDesc;
    String deviceMark;

    public SmsExtraVo() {
    }

    public SmsExtraVo(Integer simId, String simDesc, String deviceMark) {
        this.simId = simId;
        this.simDesc = simDesc;
        this.deviceMark = deviceMark;
    }

    public Integer getSimId() {
        return simId;
    }

    public void setSimId(Integer simId) {
        this.simId = simId;
    }

    public String getSimDesc() {
        return simDesc;
    }

    public void setSimDesc(String simDesc) {
        this.simDesc = simDesc;
    }

    public String getDeviceMark() {
        return deviceMark;
    }

    public void setDeviceMark(String deviceMark) {
        this.deviceMark = deviceMark;
    }

    @Override
    public String toString() {
        return "SmsExtraVo{" +
                "simId=" + simId +
                ", simDesc='" + simDesc + '\'' +
                ", deviceMark='" + deviceMark + '\'' +
                '}';
    }
}
