package com.whf.messagerelayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.whf.messagerelayer.activity.StartActivity;

public class StartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null
                || !intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }
        Intent i = new Intent(context, StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
