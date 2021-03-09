package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.model.LogModel;
import com.tim.tsms.transpondsms.model.RuleModel;
import com.tim.tsms.transpondsms.model.SenderModel;
import com.tim.tsms.transpondsms.model.vo.DingDingSettingVo;
import com.tim.tsms.transpondsms.model.vo.EmailSettingVo;
import com.tim.tsms.transpondsms.model.vo.QYWXGroupRobotSettingVo;
import com.tim.tsms.transpondsms.model.vo.SmsVo;
import com.tim.tsms.transpondsms.model.vo.WebNotifySettingVo;

import java.util.List;

import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_DINGDING;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_EMAIL;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_QYWX_GROUP_ROBOT;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_WEB_NOTIFY;

public class SimUtil {
    private static String TAG = "SimUtil";
    //获取卡槽ID
    public static int capturedSimSlot(Bundle bundle) {
        int whichSIM = -1;
        if (bundle.containsKey("subscription")) {
            whichSIM = bundle.getInt("subscription");
        }
        if (whichSIM >= 0 && whichSIM < 5) {
            /*In some device Subscription id is return as subscriber id*/
            //TODO：不确定能不能直接返回
            Log.d(TAG,"whichSIM >= 0 && whichSIM < 5："+whichSIM);
            return whichSIM;
        }

        if (bundle.containsKey("simId")) {
            whichSIM = bundle.getInt("simId");
        } else if (bundle.containsKey("com.android.phone.extra.slot")) {
            whichSIM = bundle.getInt("com.android.phone.extra.slot");
        } else {
            String keyName = "";
            for (String key : bundle.keySet()) {
                if (key.contains("sim"))
                    keyName = key;
            }
            if (bundle.containsKey(keyName)) {
                whichSIM = bundle.getInt(keyName);
            }
        }
        return whichSIM;
    }
    public static void send_msg(String msg){
        if(SettingUtil.using_dingding()){
            try {
                SenderDingdingMsg.sendMsg(msg);
            }catch (Exception e){
                Log.d(TAG,"发送出错："+e.getMessage());
            }

        }
        if(SettingUtil.using_email()){
//            SenderMailMsg.send(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY),"转发",msg);
        }

    }
    public static void send_msg_list(Context context,List<SmsVo> smsVoList){
        Log.i(TAG, "send_msg_list size: "+smsVoList.size());
        for (SmsVo smsVo:smsVoList){
            SimUtil.send_msg(context,smsVo);
        }
    }
    public static void send_msg(Context context,SmsVo smsVo){
        Log.i(TAG, "send_msg smsVo:"+smsVo);
        RuleUtil.init(context);
        LogUtil.init(context);

        List<RuleModel> rulelist = RuleUtil.getRule(null,null);
        if(!rulelist.isEmpty()){
            SenderUtil.init(context);
            for (RuleModel ruleModel:rulelist
            ) {
                //规则匹配发现需要发送
                try{
                    if(ruleModel.checkMsg(smsVo)){
                        List<SenderModel> senderModels = SenderUtil.getSender(ruleModel.getSenderId(),null);
                        for (SenderModel senderModel:senderModels
                        ) {
                            LogUtil.addLog(new LogModel(smsVo.getMobile(),smsVo.getContent(),senderModel.getId()));
                            SimUtil.senderSendMsgNoHandError(smsVo,senderModel);
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, "send_msg: fail checkMsg:",e);
                }


            }

        }
    }
    public static void sendMsgByRuleModelSenderId(final Handler handError, RuleModel ruleModel,SmsVo smsVo, Long senderId) throws Exception {
        if(senderId==null){
            throw new Exception("先新建并选择发送方");
        }

        if(!ruleModel.checkMsg(smsVo)){
            throw new Exception("短信未匹配中规则");
        }

        List<SenderModel> senderModels = SenderUtil.getSender(senderId,null);
        if(senderModels.isEmpty()){
            throw new Exception("未找到发送方");
        }

        for (SenderModel senderModel:senderModels
        ) {
            SimUtil.senderSendMsg(handError,smsVo,senderModel);
        }
    }
    public static void senderSendMsgNoHandError(SmsVo smsVo,SenderModel senderModel) {
        SimUtil.senderSendMsg(null,smsVo,senderModel);
    }
    public static void senderSendMsg(Handler handError,SmsVo smsVo, SenderModel senderModel) {

        Log.i(TAG, "senderSendMsg smsVo:"+smsVo+"senderModel:"+senderModel);
        switch (senderModel.getType()){
            case TYPE_DINGDING:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    DingDingSettingVo dingDingSettingVo = JSON.parseObject(senderModel.getJsonSetting(), DingDingSettingVo.class);
                    if(dingDingSettingVo!=null){
                        try {
                            SenderDingdingMsg.sendMsg(handError, dingDingSettingVo.getToken(), dingDingSettingVo.getSecret(),dingDingSettingVo.getAtMobils(),dingDingSettingVo.getAtAll(), smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: dingding error "+e.getMessage() );
                        }

                    }
                }

                break;
            case TYPE_EMAIL:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    EmailSettingVo emailSettingVo = JSON.parseObject(senderModel.getJsonSetting(), EmailSettingVo.class);
                    if(emailSettingVo!=null){
                        try {
                            SenderMailMsg.sendEmail(handError, emailSettingVo.getHost(),emailSettingVo.getPort(),emailSettingVo.getSsl(),emailSettingVo.getFromEmail(),
                                    emailSettingVo.getPwd(),emailSettingVo.getToEmail(),smsVo.getMobile(),smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: SenderMailMsg error "+e.getMessage() );
                        }

                    }
                }

                break;
            case TYPE_WEB_NOTIFY:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    WebNotifySettingVo webNotifySettingVo = JSON.parseObject(senderModel.getJsonSetting(), WebNotifySettingVo.class);
                    if(webNotifySettingVo!=null){
                        try {
                            SenderWebNotifyMsg.sendMsg(handError,webNotifySettingVo.getToken(),webNotifySettingVo.getSecret(),smsVo.getMobile(),smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: SenderWebNotifyMsg error "+e.getMessage() );
                        }

                    }
                }

                break;
            case TYPE_QYWX_GROUP_ROBOT:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    QYWXGroupRobotSettingVo qywxGroupRobotSettingVo = JSON.parseObject(senderModel.getJsonSetting(), QYWXGroupRobotSettingVo.class);
                    if(qywxGroupRobotSettingVo!=null){
                        try {
                            SenderQyWxGroupRobotMsg.sendMsg(handError,qywxGroupRobotSettingVo.getWebHook(),smsVo.getMobile(),smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: SenderQyWxGroupRobotMsg error "+e.getMessage() );
                        }

                    }
                }

                break;
            default:
                break;
        }
    }

}
