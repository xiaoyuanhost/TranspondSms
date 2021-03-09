package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsVo implements Serializable{
    String mobile;
    String content;
    Date date;
    SmsExtraVo smsExtraVo;

    public SmsVo() {
    }

    public SmsVo(String mobile, String content, Date date, SmsExtraVo smsExtraVo) {
        this.mobile = mobile;
        this.content = content;
        this.date = date;
        this.smsExtraVo = smsExtraVo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SmsExtraVo getSmsExtraVo() {
        return smsExtraVo;
    }

    public void setSmsExtraVo(SmsExtraVo smsExtraVo) {
        this.smsExtraVo = smsExtraVo;
    }

    public String getSmsVoForSend(){
        String extraStr="";
        if(smsExtraVo!=null){
            if(smsExtraVo.getDeviceMark()!=null && !smsExtraVo.getDeviceMark().isEmpty()){
                extraStr+="来自 "+smsExtraVo.getDeviceMark()+" ";
            }
            if(smsExtraVo.getSimDesc()!=null && !smsExtraVo.getSimDesc().isEmpty()){
                extraStr+="卡： "+smsExtraVo.getSimDesc()+ " \n";
            }
        }
        return mobile + "\n" +
               content + "\n" +
                extraStr +
               new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    @Override
    public String toString() {
        return "SmsVo{" +
                "mobile='" + mobile + '\'' +
                ", content='" + content + '\'' +
                ", smsExtraVo=" + smsExtraVo +
                ", date=" + date +
                '}';
    }
}
