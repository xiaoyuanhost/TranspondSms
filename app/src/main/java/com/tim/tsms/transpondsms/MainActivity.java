package com.tim.tsms.transpondsms;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;



import com.tim.tsms.transpondsms.BroadCastReceiver.TSMSBroadcastReceiver;
import com.tim.tsms.transpondsms.utils.SendHistory;
import com.tim.tsms.transpondsms.utils.SendUtil;
import com.tim.tsms.transpondsms.utils.UpdateAppHttpUtil;
import com.tim.tsms.transpondsms.utils.aUtil;
import com.umeng.analytics.MobclickAgent;
import com.vector.update_app.UpdateAppManager;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private TSMSBroadcastReceiver smsBroadcastReceiver;
    private TextView textv_msg;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textv_msg=(TextView) findViewById(R.id.textv_msg);

        textv_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
        textv_msg.setText(SendHistory.getHistory());

        checkPermission();

//        intentFilter=new IntentFilter();
//        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        intentFilter.addAction(MessageBroadcastReceiver.ACTION_DINGDING);
//        smsBroadcastReceiver=new SMSBroadcastReceiver();
//        //动态注册广播
//        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        //取消注册广播
        unregisterReceiver(smsBroadcastReceiver);
    }

    public void sendMsg(View view){
        try{
//            6位数随机数
//            DingdingMsg.sendMsg(Integer.toString((int) (Math.random()*9+1)*100000));
            SendUtil.send_msg(Integer.toString((int) (Math.random()*9+1)*100000));
//            SendMailUtil.send("1547681531@qq.com","s","2");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void toSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void showMsg(View view){
        Log.d(TAG,"showMsg");
        String showMsg =SendHistory.getHistory();
        textv_msg.setText(showMsg);
    }
    
    //按返回键不退出回到桌面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    private void  checkPermission()
    {
        // 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
        PackageManager pm = getPackageManager();
        boolean permission_receive_boot = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", this.getPackageName()));
        boolean permission_readsms = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_SMS", this.getPackageName()));

        if (!(
                permission_receive_boot
                && permission_readsms
        )) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_SMS,
            }, 0x01);
        }
    }

    private void checkNewVersion(){
        String geturl = "http://api.allmything.com/api/version/hasnew?versioncode=";

        try {
            geturl+= aUtil.getVersionCode(MainActivity.this);

            Log.i("SettingActivity",geturl);
            new UpdateAppManager
                    .Builder()
                    //当前Activity
                    .setActivity(MainActivity.this)
                    //更新地址
                    .setUpdateUrl(geturl)
                    //实现httpManager接口的对象
                    .setHttpManager(new UpdateAppHttpUtil())
                    .build()
                    .update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.check_new_version:
                checkNewVersion();
                return true;
            case R.id.to_setting:
                toSetting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
