package com.tim.tsms.transpondsms.utils;

import android.util.Log;

public class SendUtil {
    private static String TAG = "SendUtil";

    public static void send_msg(String msg){
        if(SettingUtil.using_dingding()){
            try {
                DingdingMsg.sendMsg(msg);
            }catch (Exception e){
                Log.d(TAG,"发送出错："+e.getMessage());
            }

        }
        if(SettingUtil.using_email()){
            SendMailUtil.send(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY),"转发",msg);
        }

    }

}
