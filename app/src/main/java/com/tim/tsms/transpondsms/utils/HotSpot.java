package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.dx.stock.ProxyBuilder;

import java.lang.reflect.InvocationHandler;
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
            Toast.makeText(context,"热点配置丢失spotName："+spotName+"spotPWD:"+spotPWD,Toast.LENGTH_LONG).show();
            Log.d(TAG,"热点配置丢失spotName："+spotName+"spotPWD:"+spotPWD);
            return;
        }else{
            Toast.makeText(context,"开启热点配置 spotName："+spotName+"spotPWD:"+spotPWD,Toast.LENGTH_LONG).show();
            Log.d(TAG,"开启热点配置 spotName："+spotName+"spotPWD:"+spotPWD);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//判断android系统版本,高于8.0
            Log.d(TAG,"android系统版本,高于8.0");
            createWifiHotspotV8(context);
         } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//  大于等于24即为7.0及以上执行内容
            Log.d(TAG,"android系统版本7.0及以上执行内容");

            createWifiHotspotV7(context,spotName,spotPWD);
        } else {
            Log.d(TAG,"android系统版本7.0以下执行内容");

            //  低于24即为7.0以下执行内容
        }


    }

    /**
     * android7.0以上开启手机热点
     */
    private static void createWifiHotspotV7(Context context,String spotName,String spotPWD) {
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

    /**
     * android8.0以上开启手机热点
     */
    private static void createWifiHotspotV8(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        try {
            Class classOnStartTetheringCallback = Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
            Method startTethering = connectivityManager.getClass().getDeclaredMethod("startTethering", int.class, boolean.class, classOnStartTetheringCallback);
            Object proxy = ProxyBuilder.forClass(classOnStartTetheringCallback).handler(new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    return null;
                }
            }).build();
            startTethering.invoke(connectivityManager, 0, false, proxy);
        } catch (Exception e) {
            Log.e(TAG,"打开热点失败");
            e.printStackTrace();
        }
    }

}
