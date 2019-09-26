package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class SendHistory {
    static String TAG = "DingdingMsg";
    static Context context;

    public static void init(Context context1){
        context = context1;
    }

    public static void addHistory(String msg){
        //不保存转发消息
        if(!SettingUtil.saveMsgHistory()) return;
        //保存
        SharedPreferences sp = context.getSharedPreferences(Define.SP_MSG,Context.MODE_PRIVATE);
        Set<String> msg_set_default = new HashSet<>();
        Set<String> msg_set;
        msg_set = sp.getStringSet(Define.SP_MSG_SET_KEY,msg_set_default);
        Log.d(TAG,"msg_set：" + msg_set.toString());
        Log.d(TAG,"msg_set：" + Integer.toString(msg_set.size()));
        //只保留条
        if(msg_set.size()>SettingUtil.getMsgHistoryCount()){
            msg_set.clear();
        }
        msg_set.add(msg);
        sp.edit().putStringSet(Define.SP_MSG_SET_KEY,msg_set).apply();
    }

    public static String getHistory(){
        SharedPreferences sp = context.getSharedPreferences(Define.SP_MSG,Context.MODE_PRIVATE);
        Set<String> msg_set = new HashSet<>();
        msg_set = sp.getStringSet(Define.SP_MSG_SET_KEY,msg_set);
        Log.d(TAG,"msg_set.toString()"+msg_set.toString());
        String getMsg ="";
        for (String str : msg_set){
            getMsg += str+"\n";
        }
        return getMsg;
    }

}
