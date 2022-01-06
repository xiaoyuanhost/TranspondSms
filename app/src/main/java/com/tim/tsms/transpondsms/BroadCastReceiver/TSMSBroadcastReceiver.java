package com.tim.tsms.transpondsms.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.tim.tsms.transpondsms.model.vo.SmsExtraVo;
import com.tim.tsms.transpondsms.model.vo.SmsVo;
import com.tim.tsms.transpondsms.utils.SettingUtil;
import com.tim.tsms.transpondsms.utils.SimUtil;
import com.tim.tsms.transpondsms.utils.sender.SendUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TSMSBroadcastReceiver  extends BroadcastReceiver {
    private String TAG = "TSMSBroadcastReceiver";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";
    @Override
    public void onReceive(Context context, Intent intent) {
        String receiveAction = intent.getAction();
        Log.d(TAG,"onReceive intent "+receiveAction);
        if(SMS_RECEIVED_ACTION.equals(receiveAction) || SMS_DELIVER_ACTION.equals(receiveAction)){
            try {

                SmsExtraVo smsExtraVo=new SmsExtraVo();

                if(SettingUtil.getSwitchAddExtra()){
                    int simId = SimUtil.getSimId(intent.getExtras());
                    smsExtraVo.setSimId(simId);
                    smsExtraVo.setDeviceMark(SettingUtil.getAddExtraDeviceMark());
                    if(simId==1){
                        smsExtraVo.setSimDesc(SettingUtil.getAddExtraSim1());
                    }
                    if(simId==2){
                        smsExtraVo.setSimDesc(SettingUtil.getAddExtraSim2());
                    }
                }

                Object[] object=(Object[]) Objects.requireNonNull(intent.getExtras()).get("pdus");
                if(object!=null){
                    List<SmsVo> smsVoList = new ArrayList<>();
                    String format = intent.getStringExtra("format");
                    Map<String,String> mobileToContent=new HashMap<>();
                    Date date =new Date();
                    for (Object pdus : object) {
                        byte[] pdusMsg=(byte[]) pdus;
                        SmsMessage sms=SmsMessage.createFromPdu(pdusMsg,format);
                        String mobile=sms.getOriginatingAddress();//发送短信的手机号
                        if(mobile==null){
                            continue;
                        }
                        //下面是获取短信的发送时间
                        date=new Date(sms.getTimestampMillis());

                        String content=mobileToContent.get(mobile);
                        if(content==null)content="";

                        content+=sms.getMessageBody();//短信内容
                        mobileToContent.put(mobile,content);

                    }
                    for (String mobile:mobileToContent.keySet()){
                        smsVoList.add(new SmsVo(mobile,mobileToContent.get(mobile),date,smsExtraVo));
                    }
                    Log.d(TAG,"短信："+smsVoList);
                    SendUtil.send_msg_list(context,smsVoList);

                }

            }catch (Throwable throwable){
                Log.e(TAG,"解析短信失败："+throwable.getMessage());
            }

        }

    }

}
