package com.tim.tsms.transpondsms;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.adapter.SenderAdapter;
import com.tim.tsms.transpondsms.model.SenderModel;
import com.tim.tsms.transpondsms.model.vo.DingDingSettingVo;
import com.tim.tsms.transpondsms.model.vo.EmailSettingVo;
import com.tim.tsms.transpondsms.model.vo.WebNotifySettingVo;
import com.tim.tsms.transpondsms.utils.SenderDingdingMsg;
import com.tim.tsms.transpondsms.utils.SenderMailMsg;
import com.tim.tsms.transpondsms.utils.SenderUtil;
import com.tim.tsms.transpondsms.utils.SenderWebNotifyMsg;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tim.tsms.transpondsms.model.SenderModel.STATUS_ON;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_DINGDING;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_EMAIL;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_WEB_NOTIFY;

public class SenderActivity extends AppCompatActivity {

    private String TAG = "SenderActivity";
    // 用于存储数据
    private List<SenderModel> senderModels = new ArrayList<>();
    private SenderAdapter adapter;
    public static final int NOTIFY = 0x9731;
    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOTIFY:
                    Toast.makeText(SenderActivity.this, msg.getData().getString("DATA"), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        SenderUtil.init(SenderActivity.this);

        // 先拿到数据并放在适配器上
        initSenders(); //初始化数据
        adapter = new SenderAdapter(SenderActivity.this, R.layout.sender_item, senderModels);

        // 将适配器上的数据传递给listView
        ListView listView = findViewById(R.id.list_view_sender);
        listView.setAdapter(adapter);

        // 为ListView注册一个监听器，当用户点击了ListView中的任何一个子项时，就会回调onItemClick()方法
        // 在这个方法中可以通过position参数判断出用户点击的是那一个子项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SenderModel senderModel = senderModels.get(position);
                Log.d(TAG, "onItemClick: "+senderModel);

                switch (senderModel.getType()){
                    case TYPE_DINGDING:
                        setDingDing(senderModel);
                        break;
                    case TYPE_EMAIL:
                        setEmail(senderModel);
                        break;
                    case TYPE_WEB_NOTIFY:
                        setWebNotify(senderModel);
                        break;
                    default:
                        Toast.makeText(SenderActivity.this,"异常的发送方类型！删除",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });


    }
    // 初始化数据
    private void initSenders() {
        senderModels = SenderUtil.getSender(null, null);
        ;
    }

    public void addSender(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SenderActivity.this);
        builder.setTitle("选择发送方类型");
        builder.setItems(R.array.add_sender_menu, new DialogInterface.OnClickListener() {//添加列表
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case TYPE_DINGDING:
                        setDingDing(null);
                        break;
                    case TYPE_EMAIL:
                        setEmail(null);
                        break;
                    case TYPE_WEB_NOTIFY:
                        setWebNotify(null);
                        break;
                    default:
                        Toast.makeText(SenderActivity.this, "暂不支持这种转发！", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        builder.show();
        Log.d(TAG, "setDingDing show" + senderModels.size());
    }


    private void setDingDing(final SenderModel senderModel) {
        DingDingSettingVo dingDingSettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                dingDingSettingVo = JSON.parseObject(jsonSettingStr, DingDingSettingVo.class);
            }
        }
        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_dingding, null);

        final EditText editTextDingdingName = view1.findViewById(R.id.editTextDingdingName);
        if (senderModel != null)
            editTextDingdingName.setText(senderModel.getName());
        final EditText editTextDingdingToken = view1.findViewById(R.id.editTextDingdingToken);
        if (dingDingSettingVo != null)
            editTextDingdingToken.setText(dingDingSettingVo.getToken());
        final EditText editTextDingdingSecret = view1.findViewById(R.id.editTextDingdingSecret);
        if (dingDingSettingVo != null)
            editTextDingdingSecret.setText(dingDingSettingVo.getSecret());

        Button buttondingdingok = view1.findViewById(R.id.buttondingdingok);
        Button buttondingdingdel = view1.findViewById(R.id.buttondingdingdel);
        Button buttondingdingtest = view1.findViewById(R.id.buttondingdingtest);
        alertDialog71
                .setTitle(R.string.setdingdingtitle)
                .setIcon(R.mipmap.dingding)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();
        buttondingdingok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextDingdingName.getText().toString());
                    newSenderModel.setType(TYPE_DINGDING);
                    newSenderModel.setStatus(STATUS_ON);
                    DingDingSettingVo dingDingSettingVonew = new DingDingSettingVo(editTextDingdingToken.getText().toString(), editTextDingdingSecret.getText().toString());
                    newSenderModel.setJsonSetting(JSON.toJSONString(dingDingSettingVonew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
//                    adapter.add(newSenderModel);
                } else {
                    senderModel.setName(editTextDingdingName.getText().toString());
                    senderModel.setType(TYPE_DINGDING);
                    senderModel.setStatus(STATUS_ON);
                    DingDingSettingVo dingDingSettingVonew = new DingDingSettingVo(editTextDingdingToken.getText().toString(), editTextDingdingSecret.getText().toString());
                    senderModel.setJsonSetting(JSON.toJSONString(dingDingSettingVonew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
//                    adapter.update(senderModel,position);
                }


                show.dismiss();


            }
        });
        buttondingdingdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (senderModel != null) {
                    SenderUtil.delSender(senderModel.getId());
                    initSenders();
                    adapter.del(senderModels);
//                    adapter.del(position);

                }
                show.dismiss();
            }
        });
        buttondingdingtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = editTextDingdingToken.getText().toString();
                String secret = editTextDingdingSecret.getText().toString();
                if (token != null && !token.isEmpty()) {
                    try {
                        SenderDingdingMsg.sendMsg(true, token, secret, "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                    } catch (Exception e) {
                        Toast.makeText(SenderActivity.this, "发送失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SenderActivity.this, "token 不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setEmail(final SenderModel senderModel) {
        EmailSettingVo emailSettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                emailSettingVo = JSON.parseObject(jsonSettingStr, EmailSettingVo.class);
            }
        }

        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_email, null);

        final EditText editTextEmailName = view1.findViewById(R.id.editTextEmailName);
        if (senderModel != null) editTextEmailName.setText(senderModel.getName());
        final EditText editTextEmailHost = view1.findViewById(R.id.editTextEmailHost);
        if (emailSettingVo != null) editTextEmailHost.setText(emailSettingVo.getHost());
        final EditText editTextEmailPort = view1.findViewById(R.id.editTextEmailPort);
        if (emailSettingVo != null) editTextEmailPort.setText(emailSettingVo.getPort());
        final EditText editTextEmailFromAdd = view1.findViewById(R.id.editTextEmailFromAdd);
        if (emailSettingVo != null) editTextEmailFromAdd.setText(emailSettingVo.getFromEmail());
        final EditText editTextEmailPsw = view1.findViewById(R.id.editTextEmailPsw);
        if (emailSettingVo != null) editTextEmailPsw.setText(emailSettingVo.getPwd());
        final EditText editTextEmailToAdd = view1.findViewById(R.id.editTextEmailToAdd);
        if (emailSettingVo != null) editTextEmailToAdd.setText(emailSettingVo.getToEmail());

        Button buttonemailok = view1.findViewById(R.id.buttonemailok);
        Button buttonemaildel = view1.findViewById(R.id.buttonemaildel);
        Button buttonemailtest = view1.findViewById(R.id.buttonemailtest);
        alertDialog71
                .setTitle(R.string.setemailtitle)
                .setIcon(R.drawable.ic_baseline_email_24)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();

        buttonemailok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextEmailName.getText().toString());
                    newSenderModel.setType(TYPE_EMAIL);
                    newSenderModel.setStatus(STATUS_ON);
                    EmailSettingVo emailSettingVonew = new EmailSettingVo(
                            editTextEmailHost.getText().toString(),
                            editTextEmailPort.getText().toString(),
                            editTextEmailFromAdd.getText().toString(),
                            editTextEmailPsw.getText().toString(),
                            editTextEmailToAdd.getText().toString()
                    );
                    newSenderModel.setJsonSetting(JSON.toJSONString(emailSettingVonew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
                } else {
                    senderModel.setName(editTextEmailName.getText().toString());
                    senderModel.setType(TYPE_EMAIL);
                    senderModel.setStatus(STATUS_ON);
                    EmailSettingVo emailSettingVonew = new EmailSettingVo(
                            editTextEmailHost.getText().toString(),
                            editTextEmailPort.getText().toString(),
                            editTextEmailFromAdd.getText().toString(),
                            editTextEmailPsw.getText().toString(),
                            editTextEmailToAdd.getText().toString()
                    );
                    senderModel.setJsonSetting(JSON.toJSONString(emailSettingVonew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
                }


                show.dismiss();


            }
        });
        buttonemaildel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (senderModel != null) {
                    SenderUtil.delSender(senderModel.getId());
                    initSenders();
                    adapter.del(senderModels);
                }
                show.dismiss();
            }
        });
        buttonemailtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = editTextEmailHost.getText().toString();
                String port = editTextEmailPort.getText().toString();
                String fromemail = editTextEmailFromAdd.getText().toString();
                String pwd = editTextEmailPsw.getText().toString();
                String toemail = editTextEmailToAdd.getText().toString();
                if (!host.isEmpty() && !port.isEmpty() && !fromemail.isEmpty() && !pwd.isEmpty() && !toemail.isEmpty()) {
                    try {
                        SenderMailMsg.sendEmail(true,host,port,fromemail,pwd,toemail,"TranspondSms test", "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                    } catch (Exception e) {
                        Toast.makeText(SenderActivity.this, "发送失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SenderActivity.this, "token 不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setWebNotify(final SenderModel senderModel) {
        WebNotifySettingVo webNotifySettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                webNotifySettingVo = JSON.parseObject(jsonSettingStr, WebNotifySettingVo.class);
            }
        }

        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_webnotify, null);

        final EditText editTextWebNotifyName = view1.findViewById(R.id.editTextWebNotifyName);
        if (senderModel != null) editTextWebNotifyName.setText(senderModel.getName());
        final EditText editTextWebNotifyToken = view1.findViewById(R.id.editTextWebNotifyToken);
        if (webNotifySettingVo != null) editTextWebNotifyToken.setText(webNotifySettingVo.getToken());

        Button buttonbebnotifyok = view1.findViewById(R.id.buttonbebnotifyok);
        Button buttonbebnotifydel = view1.findViewById(R.id.buttonbebnotifydel);
        Button buttonbebnotifytest = view1.findViewById(R.id.buttonbebnotifytest);
        alertDialog71
                .setTitle(R.string.setwebnotifytitle)
                .setIcon(R.mipmap.ic_launcher)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();

        buttonbebnotifyok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextWebNotifyName.getText().toString());
                    newSenderModel.setType(TYPE_WEB_NOTIFY);
                    newSenderModel.setStatus(STATUS_ON);
                    WebNotifySettingVo webNotifySettingVoNew = new WebNotifySettingVo(
                            editTextWebNotifyToken.getText().toString()
                    );
                    newSenderModel.setJsonSetting(JSON.toJSONString(webNotifySettingVoNew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
                } else {
                    senderModel.setName(editTextWebNotifyName.getText().toString());
                    senderModel.setType(TYPE_WEB_NOTIFY);
                    senderModel.setStatus(STATUS_ON);
                    WebNotifySettingVo webNotifySettingVoNew = new WebNotifySettingVo(
                            editTextWebNotifyToken.getText().toString()
                    );
                    senderModel.setJsonSetting(JSON.toJSONString(webNotifySettingVoNew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
                }

                show.dismiss();

            }
        });
        buttonbebnotifydel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (senderModel != null) {
                    SenderUtil.delSender(senderModel.getId());
                    initSenders();
                    adapter.del(senderModels);
                }
                show.dismiss();
            }
        });
        buttonbebnotifytest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = editTextWebNotifyToken.getText().toString();
                if (!token.isEmpty()) {
                    try {
                        SenderWebNotifyMsg.sendMsg(true,token,"TranspondSms test", "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                    } catch (Exception e) {
                        Toast.makeText(SenderActivity.this, "发送失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SenderActivity.this, "token 不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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
