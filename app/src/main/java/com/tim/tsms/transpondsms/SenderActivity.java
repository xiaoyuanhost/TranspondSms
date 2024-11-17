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
import android.widget.Switch;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.adapter.SenderAdapter;
import com.tim.tsms.transpondsms.model.SenderModel;
import com.tim.tsms.transpondsms.model.vo.PushPlusSettingVo;
import com.tim.tsms.transpondsms.model.vo.SocketSettingVo;
import com.tim.tsms.transpondsms.model.vo.EmailSettingVo;
import com.tim.tsms.transpondsms.utils.sender.SenderPushplusMsg;
import com.tim.tsms.transpondsms.utils.sender.SenderSocketMsg;
import com.tim.tsms.transpondsms.utils.sender.SenderMailMsg;
import com.tim.tsms.transpondsms.utils.sender.SenderUtil;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tim.tsms.transpondsms.model.SenderModel.STATUS_ON;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_EMAIL;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_PUSHPLUS;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_SOCKET;

public class SenderActivity extends AppCompatActivity {

    private String TAG = "SenderActivity";
    // 用于存储数据
    private List<SenderModel> senderModels = new ArrayList<>();
    private SenderAdapter adapter;
    public static final int NOTIFY = 0x9731993;
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
                    case TYPE_EMAIL:
                        setEmail(senderModel);
                        break;
                    case TYPE_SOCKET:
                        setSocket(senderModel);
                        break;
                    case TYPE_PUSHPLUS:
                        setPushPlus(senderModel);
                        break;
                    default:
                        Toast.makeText(SenderActivity.this,"异常的发送方类型！删除",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SenderActivity.this);

                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SenderUtil.delSender(senderModels.get(position).getId());
                        initSenders();
                        adapter.del(senderModels);
                        Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
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
                    case TYPE_EMAIL:
                        setEmail(null);
                        break;
                    case TYPE_SOCKET:
                        setSocket(null);
                        break;
                    case TYPE_PUSHPLUS:
                        setPushPlus(null);
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


    private void setPushPlus(final SenderModel senderModel) {
        PushPlusSettingVo pushplusSettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                pushplusSettingVo = JSON.parseObject(jsonSettingStr, PushPlusSettingVo.class);
            }
        }
        //wangle muqian
        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_pushplus, null);

        final EditText editTextpushplusName = view1.findViewById(R.id.editTextpushplusName);
        if (senderModel != null)
            editTextpushplusName.setText(senderModel.getName());
        final EditText editTextpushplusToken = view1.findViewById(R.id.editTextpushplusToken);
        if (pushplusSettingVo != null)
            editTextpushplusToken.setText(pushplusSettingVo.getToken());
        //由于pushplus不需要下面的输入UI，后面进行删除


        Button buttonpushplusok = view1.findViewById(R.id.buttonpushplusok);
        Button buttonpushplusdel = view1.findViewById(R.id.buttonpushplusdel);
        Button buttonpushplustest = view1.findViewById(R.id.buttonpushplustest);
        alertDialog71
                .setTitle(R.string.setpushplustitle)
                .setIcon(R.mipmap.pushplus)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();
        buttonpushplusok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextpushplusName.getText().toString());
                    newSenderModel.setType(TYPE_PUSHPLUS);
                    newSenderModel.setStatus(STATUS_ON);
                    PushPlusSettingVo pushplusSettingVonew = new PushPlusSettingVo(
                            editTextpushplusToken.getText().toString()
                    );
                    newSenderModel.setJsonSetting(JSON.toJSONString(pushplusSettingVonew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
//                    adapter.add(newSenderModel);
                } else {
                    senderModel.setName(editTextpushplusName.getText().toString());
                    senderModel.setType(TYPE_PUSHPLUS);
                    senderModel.setStatus(STATUS_ON);
                    PushPlusSettingVo pushplusSettingVonew = new PushPlusSettingVo(
                            editTextpushplusToken.getText().toString()
                    );
                    senderModel.setJsonSetting(JSON.toJSONString(pushplusSettingVonew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
//                    adapter.update(senderModel,position);
                }


                show.dismiss();


            }
        });
        buttonpushplusdel.setOnClickListener(new View.OnClickListener() {
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
        buttonpushplustest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = editTextpushplusToken.getText().toString();
                //String secret = editTextDingdingSecret.getText().toString();
                //String atMobiles = editTextDingdingAtMobiles.getText().toString();
                //Boolean atAll = switchDingdingAtAll.isChecked();
                if (token != null && !token.isEmpty()) {
                    try {
                        SenderPushplusMsg.sendMsg(handler, token, "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
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

    private void setSocket(final SenderModel senderModel) {
        SocketSettingVo socketSettingVo = null;
        //try phrase json setting
        if (senderModel != null) {
            String jsonSettingStr = senderModel.getJsonSetting();
            if (jsonSettingStr != null) {
                socketSettingVo = JSON.parseObject(jsonSettingStr, SocketSettingVo.class);
            }
        }
        //wangle muqian
        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SenderActivity.this);
        View view1 = View.inflate(SenderActivity.this, R.layout.activity_alter_dialog_setview_socket, null);

        final EditText editTextsocketName = view1.findViewById(R.id.editTextsocketName);
        if (senderModel != null)
            editTextsocketName.setText(senderModel.getName());
        final EditText editTextsocketToken = view1.findViewById(R.id.editTextsocketToken);
        if (socketSettingVo != null)
            editTextsocketToken.setText(socketSettingVo.getIpAddress());
        final EditText editTextsocketport = view1.findViewById(R.id.editTextsocketSecret);
        if (socketSettingVo != null)
            editTextsocketport.setText(String.valueOf(socketSettingVo.getPort()));
        //由于pushplus不需要下面的输入UI，后面进行删除


        Button buttonsocketok = view1.findViewById(R.id.buttonsocketok);
        Button buttonsocketdel = view1.findViewById(R.id.buttonsocketdel);
        Button buttonsockettest = view1.findViewById(R.id.buttonsockettest);
        alertDialog71
                .setTitle(R.string.setsockettitle)
                .setIcon(R.mipmap.socket)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();
        buttonsocketok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (senderModel == null) {
                    SenderModel newSenderModel = new SenderModel();
                    newSenderModel.setName(editTextsocketName.getText().toString());
                    newSenderModel.setType(TYPE_SOCKET);
                    newSenderModel.setStatus(STATUS_ON);
                    SocketSettingVo socketSettingVonew = new SocketSettingVo(
                            editTextsocketToken.getText().toString(),
                            editTextsocketport.getText().toString()
                            //editTextDingdingAtMobiles.getText().toString(),
                            //switchDingdingAtAll.isChecked()
                    );
                    newSenderModel.setJsonSetting(JSON.toJSONString(socketSettingVonew));
                    SenderUtil.addSender(newSenderModel);
                    initSenders();
                    adapter.add(senderModels);
//                    adapter.add(newSenderModel);
                } else {
                    senderModel.setName(editTextsocketName.getText().toString());
                    senderModel.setType(TYPE_SOCKET);
                    senderModel.setStatus(STATUS_ON);
                    SocketSettingVo socketSettingVonew = new SocketSettingVo(
                            editTextsocketToken.getText().toString(),
                            editTextsocketport.getText().toString()
                            //editTextDingdingSecret.getText().toString(),
                            //editTextDingdingAtMobiles.getText().toString(),
                            //switchDingdingAtAll.isChecked()
                    );
                    senderModel.setJsonSetting(JSON.toJSONString(socketSettingVonew));
                    SenderUtil.updateSender(senderModel);
                    initSenders();
                    adapter.update(senderModels);
//                    adapter.update(senderModel,position);
                }


                show.dismiss();


            }
        });
        buttonsocketdel.setOnClickListener(new View.OnClickListener() {
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
        buttonsockettest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = editTextsocketToken.getText().toString();
                // int port = Integer.parseInt(editTextsocketport.getText().toString());
                String port =  editTextsocketport.getText().toString();
                final int portNumber;
                portNumber = Integer.parseInt(port);
                //String secret = editTextDingdingSecret.getText().toString();
                //String atMobiles = editTextDingdingAtMobiles.getText().toString();
                //Boolean atAll = switchDingdingAtAll.isChecked()
                if (token != null && !token.isEmpty()&& portNumber >0) {
                    try {
                        SenderSocketMsg.sendMsg(handler, token, port,"test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
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

        final Switch switchEmailSSl = view1.findViewById(R.id.switchEmailSSl);
        if (emailSettingVo != null) switchEmailSSl.setChecked(emailSettingVo.getSsl());
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
                            switchEmailSSl.isChecked(),
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
                            switchEmailSSl.isChecked(),
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
                Boolean ssl = switchEmailSSl.isChecked();
                String fromemail = editTextEmailFromAdd.getText().toString();
                String pwd = editTextEmailPsw.getText().toString();
                String toemail = editTextEmailToAdd.getText().toString();
                if (!host.isEmpty() && !port.isEmpty() && !fromemail.isEmpty() && !pwd.isEmpty() && !toemail.isEmpty()) {
                    try {
                        SenderMailMsg.sendEmail(handler,host,port,ssl,fromemail,pwd,toemail,"TranspondSms test", "test@" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
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
