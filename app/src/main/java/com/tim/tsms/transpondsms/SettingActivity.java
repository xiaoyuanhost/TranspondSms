package com.tim.tsms.transpondsms;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tim.tsms.transpondsms.utils.Define;
import com.tim.tsms.transpondsms.utils.SettingUtil;
import com.tim.tsms.transpondsms.utils.UpdateAppHttpUtil;
import com.tim.tsms.transpondsms.utils.aUtil;
import com.vector.update_app.UpdateAppManager;


public class SettingActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_setting);

        SwitchPreference emailSwitch = (SwitchPreference)findPreference("option_email_on");
        SwitchPreference withrebootSwitch = (SwitchPreference)findPreference("option_withreboot");
        emailSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean)newValue){
                    setEmail();
                }
                return true;
            }
        });
        checkWithReboot(withrebootSwitch);

        Preference versionnowPreference = (Preference)findPreference("option_versionnow");
        versionnowPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    new UpdateAppManager
                            .Builder()
                            //当前Activity
                            .setActivity(SettingActivity.this)
                            //更新地址
                            .setUpdateUrl("http://api.allmything.com/api/version/hasnew?versioncode="+aUtil.getVersionCode(SettingActivity.this))
                            //实现httpManager接口的对象
                            .setHttpManager(new UpdateAppHttpUtil())
                            .build()
                            .update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

    }

    //检查重启广播接受器状态并设置
    private void checkWithReboot(SwitchPreference withrebootSwitch){
        //获取组件
        final ComponentName cm = new ComponentName(this.getPackageName(), this.getPackageName()+".RebootBroadcastReceiver");
        final PackageManager pm = getPackageManager();
        int state = pm.getComponentEnabledSetting(cm);
        if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {

            withrebootSwitch.setChecked(true);
        }
        withrebootSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object isChecked) {
                int newState = (Boolean)isChecked ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                pm.setComponentEnabledSetting(cm, newState,PackageManager.DONT_KILL_APP);
                return true;
            }
        });
    }

    private void setEmail(){
        final AlertDialog.Builder alertDialog71 = new AlertDialog.Builder(SettingActivity.this);
        View view1 = View.inflate(SettingActivity.this, R.layout.activity_alter_dialog_setview_email, null);

        final EditText editTextEmailHost = view1.findViewById(R.id.editTextEmailHost);
        editTextEmailHost.setText(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_HOST_KEY));
        final EditText editTextEmailPort = view1.findViewById(R.id.editTextEmailPort);
        editTextEmailPort.setText(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_PORT_KEY));
        final EditText editTextEmailFromAdd = view1.findViewById(R.id.editTextEmailFromAdd);
        editTextEmailFromAdd.setText(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_FROMADD_KEY));
        final EditText editTextEmailPsw = view1.findViewById(R.id.editTextEmailPsw);
        editTextEmailPsw.setText(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_PSW_KEY));
        final EditText editTextEmailToAdd = view1.findViewById(R.id.editTextEmailToAdd);
        editTextEmailToAdd.setText(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY));

        Button bu = view1.findViewById(R.id.buttonemailok);
        alertDialog71
                .setTitle(R.string.setemailtitle)
                .setIcon(R.mipmap.ic_launcher)
                .setView(view1)
                .create();
        final AlertDialog show = alertDialog71.show();
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingUtil.set_send_util_email(editTextEmailHost.getText().toString(),editTextEmailPort.getText().toString(),editTextEmailFromAdd.getText().toString(),editTextEmailPsw.getText().toString(),editTextEmailToAdd.getText().toString());
                show.dismiss();
            }
        });
    }
}
