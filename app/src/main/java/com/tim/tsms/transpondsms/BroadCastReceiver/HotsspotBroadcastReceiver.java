package com.tim.tsms.transpondsms.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.tim.tsms.transpondsms.utils.HotSpot;
import com.tim.tsms.transpondsms.utils.InitUtil;
import com.tim.tsms.transpondsms.utils.SettingUtil;

public class HotsspotBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "HotsspotBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String receiveAction = intent.getAction();
        Log.d(TAG,"onReceive intent "+receiveAction);

        if (receiveAction.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {//便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
            int state = intent.getIntExtra("wifi_state", 0);
            if (state == 10) {
                Log.d(TAG,"热点状态：正在关闭");
            } else if (state == 11) {
                Log.d(TAG,"热点状态：已关闭");
                if(SettingUtil.getStartHotSpot()) {
                    Log.d(TAG,"热点保活10s后开启热点");
                    Toast.makeText(context,"热点保活10s后开启热点",Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(10000);
                    }catch (InterruptedException i){
                        Log.d(TAG,"InterruptedException"+i.getMessage());
                    }
                    Log.d(TAG,"热点保活后开启热点");
                    Toast.makeText(context,"热点保活开启热点",Toast.LENGTH_LONG).show();
                    HotSpot.createWifiHotspot(context) ;
                }
            } else if (state == 12) {
                Log.d(TAG,"热点状态：正在开启");
            } else if (state == 13) {
                Log.d(TAG,"热点状态：已开启");
            }
        }

    }

}
