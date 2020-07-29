package com.tim.tsms.transpondsms.utils;

import android.content.Context;
import android.util.Log;

public class InitUtil {
    private static String TAG = "InitUtil";

    private static Context context=null;
    public static void init(Context context1){
        Log.d(TAG,"TMSG init");
        if(context==null){
            context = context1;
            Log.d(TAG,"init context");
        }
        SettingUtil.init(context);

    }

}
