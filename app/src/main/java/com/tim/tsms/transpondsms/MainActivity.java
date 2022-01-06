package com.tim.tsms.transpondsms;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;


import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.BroadCastReceiver.TSMSBroadcastReceiver;
import com.tim.tsms.transpondsms.adapter.LogAdapter;
import com.tim.tsms.transpondsms.model.vo.LogVo;
import com.tim.tsms.transpondsms.model.vo.SmsExtraVo;
import com.tim.tsms.transpondsms.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReFlashListView.IReflashListener {

    private IntentFilter intentFilter;
    private TSMSBroadcastReceiver smsBroadcastReceiver;
    private String TAG = "MainActivity";
    // logVoList用于存储数据
    private List<LogVo> logVos =new ArrayList<>();
    private LogAdapter adapter;
    private ReFlashListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.init(this);
        // 先拿到数据并放在适配器上
        initTLogs(); //初始化数据
        showList(logVos);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogVo logVo= logVos.get(position-1);
                logDetail(logVo);
//                Toast.makeText(MainActivity.this,String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });

//        textv_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textv_msg.setText(SendHistory.getHistory());

        checkPermission();

//        intentFilter=new IntentFilter();
//        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        intentFilter.addAction(MessageBroadcastReceiver.ACTION_DINGDING);
//        smsBroadcastReceiver=new SMSBroadcastReceiver();
//        //动态注册广播
//        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    // 初始化数据
    private void initTLogs(){
        logVos= LogUtil.getLog(null,null);
    }

    private void showList(List<LogVo> logVosN) {
        Log.d(TAG, "showList: "+logVosN);
        if (adapter == null) {
            // 将适配器上的数据传递给listView
            listView=findViewById(R.id.list_view_log);
            listView.setInterface(this);
            adapter=new LogAdapter(MainActivity.this,R.layout.tlog_item, logVosN);

            listView.setAdapter(adapter);
        } else {
            adapter.onDateChange(logVosN);
        }
    }

    @Override
    public void onReflash() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //获取最新数据
                initTLogs();
                //通知界面显示
                showList(logVos);
                //通知listview 刷新数据完毕；
                listView.reflashComplete();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        //取消注册广播
        unregisterReceiver(smsBroadcastReceiver);
    }

    public void logDetail(LogVo logVo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("详情");
        SmsExtraVo smsExtraVo = JSON.parseObject(logVo.getJsonExtra(), SmsExtraVo.class);
        String extraStr="";
        if(smsExtraVo!=null && smsExtraVo.getSimDesc()!=null){
            extraStr="卡："+smsExtraVo.getSimDesc()+"\n";
        }

        builder.setMessage(logVo.getFrom()+"\n"+logVo.getContent()+"\n"+logVo.getRule()+"\n"+extraStr+logVo.getTime());
        builder.show();
    }

    public void toAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void toSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void toRuleSetting(View view){
        Intent intent = new Intent(this, RuleActivity.class);
        startActivity(intent);
    }

    public void toSendSetting(View view){
        Intent intent = new Intent(this, SenderActivity.class);
        startActivity(intent);
    }

    public void cleanLog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("确定要清空转发记录吗？")
        .setPositiveButton("清空", new DialogInterface.OnClickListener() {// 积极

            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                // TODO Auto-generated method stub
                LogUtil.delLog(null,null);
                initTLogs();
                adapter.add(logVos);
            }
        });
        builder.show();

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
        boolean permission_receive_sms = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.RECEIVE_SMS, this.getPackageName()));

        if (!(
                permission_receive_boot
                        && permission_readsms
                        && permission_receive_sms
        )) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
            }, 0x01);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.to_about:
                toAbout();
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
