package com.tim.tsms.transpondsms.utils;

import android.util.Log;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DingdingMsg {

    static String TAG = "DingdingMsg";

    public static void sendMsg(String msg) throws Exception{

        String webhook_token = SettingUtil.get_using_dingding_token();
        if(webhook_token.equals("")){
            return;
        }

        final String msgf = msg;
        String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \""+msg+"\"}}";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                textMsg);

        final Request request = new Request.Builder()
                .url(webhook_token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"onFailure：" + e.getMessage());
                SendHistory.addHistory("DingDing:"+msgf+"onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.d(TAG,"Code：" + String.valueOf(response.code())+responseStr);
                SendHistory.addHistory("DingDing:"+msgf+"Code：" + String.valueOf(response.code())+responseStr);
            }
        });
    }
}
