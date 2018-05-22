package com.whf.messagerelayer.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;
import com.whf.messagerelayer.utils.FormatMobile;
import com.whf.messagerelayer.utils.NativeDataManager;

public class MessageReceiver extends BroadcastReceiver {

    private NativeDataManager mNativeDataManager;
    public MessageReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mNativeDataManager = new NativeDataManager(context);
        if(mNativeDataManager.getReceiver()){
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] sms = new SmsMessage[pdus.length];

                for(int i = 0;i<pdus.length;i++){
                    sms[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                startSmsService(context, sms);
            }
        }
    }

    private ComponentName startSmsService(Context context, SmsMessage[] sms) {
        String mobile = sms[0].getOriginatingAddress();//发送短信的手机号码

        if(FormatMobile.hasPrefix(mobile)){
            mobile = FormatMobile.formatMobile(mobile);
        }
        String content = "";
        for (SmsMessage sm : sms) {
            content += sm.getMessageBody();// 获取短信内容
        }

        Intent serviceIntent = new Intent(context, SmsService.class);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT,content);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE,mobile);
        return context.startService(serviceIntent);
    }
}
