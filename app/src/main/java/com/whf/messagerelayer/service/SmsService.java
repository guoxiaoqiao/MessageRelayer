package com.whf.messagerelayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.whf.messagerelayer.bean.Contact;
import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.utils.EmailRelayerManager;
import com.whf.messagerelayer.utils.NativeDataManager;
import com.whf.messagerelayer.utils.SmsRelayerManager;
import com.whf.messagerelayer.utils.db.DataBaseManager;

import java.util.ArrayList;
import java.util.Set;

public class SmsService extends IntentService {

    private NativeDataManager mNativeDataManager;
    private DataBaseManager mDataBaseManager;

    public SmsService() {
        super("SmsService");
    }

    public SmsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNativeDataManager = new NativeDataManager(this);
        mDataBaseManager = new DataBaseManager(this);

        Log.i("info", "vis intent");

        String mobile = intent.getStringExtra(Constant.EXTRA_MESSAGE_MOBILE);
        String content = intent.getStringExtra(Constant.EXTRA_MESSAGE_CONTENT);
        Set<String> keySet = mNativeDataManager.getKeywordSet();
        ArrayList<Contact> contactList = mDataBaseManager.getAllContact();
        //无转发规则
        if (keySet.size() == 0) {
            return;
        }

        if (keySet.size() != 0) {// 仅支持关键字规则
            for (String key : keySet) {
                // 如果配置了 * 则讲所有信息都转发
                if (key.contains("*")) {
                    Log.i("info", "vis match rule:all");
                    Log.i("info", content.toString());
                    Log.i("info", mobile.toString());
                    relayMessage(content, mobile);
                    return;
                }
                if (content.contains(key)) {
                    relayMessage(content, mobile);
                    return;
                }
            }
        }
    }

    private void relayMessage(String content, String mobile) {
        // 不去支持前缀后缀了
        /*
        String suffix = mNativeDataManager.getContentSuffix();
        String prefix = mNativeDataManager.getContentPrefix();
        if(suffix!=null){
            content = content+suffix;
        }
        if(prefix!=null){
            content = prefix+content;
        }
        */

        // 仅支持邮件转发
        if (mNativeDataManager.getEmailRelay()) {
            EmailRelayerManager.relayEmail(mNativeDataManager, content, mobile);
        }
    }

    @Override
    public void onDestroy() {
        mDataBaseManager.closeHelper();
        super.onDestroy();
    }
}
