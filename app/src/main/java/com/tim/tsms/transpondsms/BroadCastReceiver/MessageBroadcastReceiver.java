package com.tim.tsms.transpondsms.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class MessageBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "MessageBroadcastReceiver";

    public static String EXTRA_DATA = "data";

    public static String ACTION_DINGDING = "com.tim.tsms.transpondsms.action_dingding";
    @Override
    public void onReceive(Context arg0, Intent intent) {
        Log.d(TAG,"onReceive intent "+intent.getAction());
        String action = intent.getAction();
        if(action.equals(ACTION_DINGDING)){
            String sendStatus = intent.getStringExtra(EXTRA_DATA);
            Toast.makeText(arg0,"dingding sendStatus: "+sendStatus,Toast.LENGTH_LONG).show();
        }

    }

}
