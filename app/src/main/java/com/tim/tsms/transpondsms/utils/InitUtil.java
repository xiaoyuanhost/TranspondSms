package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.util.Log;

public class InitUtil {
    private static String TAG = "InitUtil";

    private static Context context=null;
    static Boolean hasInit=false;

    public static void init(Context context1){
        Log.d(TAG,"TMSG init");
        synchronized (hasInit){
            if(hasInit)return;
            hasInit=true;
            context = context1;
            Log.d(TAG,"init context");
            SettingUtil.init(context);
        }


    }

}
