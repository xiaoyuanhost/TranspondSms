package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public class HotSpot {
    private static String TAG = "HotSpot";
    /**
     * 创建Wifi热点
     */
    public static void createWifiHotspot(Context context) {
        String spotName = SettingUtil.getHotSpotName();
        String spotPWD = SettingUtil.getHotSpotPWD();
        if(spotName.isEmpty() || spotPWD.isEmpty() || spotPWD.length()<8){
            Log.d(TAG,"热点配置丢失spotName："+spotName+"spotPWD:"+spotPWD);
        }else{
            Log.d(TAG,"开启热点配置 spotName："+spotName+"spotPWD:"+spotPWD);
            createWifiHotspotV1(context,spotName,spotPWD);
        }

    }

    private static void createWifiHotspotV1(Context context,String spotName,String spotPWD) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = spotName;
        config.preSharedKey = spotPWD;
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedKeyManagement.set(aUtil.isMIUI() ? 6 : 4);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                Log.d(TAG,"热点已开启");
            } else {
                Log.d(TAG,"创建热点失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"创建热点失败"+e.getMessage());
        }
    }

}
