package com.whf.messagerelayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;
import com.whf.messagerelayer.utils.NativeDataManager;

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: New every time???
        NativeDataManager mNativeDataManager = new NativeDataManager(context);
        if (!mNativeDataManager.getReceiver())
            return;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null || pdus.length == 0) {
                return;
            }

            // For API level 15
            SmsMessage[] sms = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                sms[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            startSmsService(context, sms);
        }

    }

    private void startSmsService(Context context, SmsMessage[] sms) {
        // Only one?
        String mobile = sms[0].getOriginatingAddress();

        StringBuilder sb = new StringBuilder();
        for (SmsMessage sm : sms) {
            sb.append(sm.getMessageBody());
            sb.append("    ");
        }

        Intent serviceIntent = new Intent(context, SmsService.class);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT, sb.toString());
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE, mobile);

        context.startService(serviceIntent);
    }
}
