package com.whf.messagerelayer.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.whf.messagerelayer.utils.NativeDataManager;

/**
 * Created by lingxuan on 2017/7/27.
 */
public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
            Log.d("sms", "CALL_STATE_RINGING register content observer");
            context.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false, new CallObserver(context, new Handler()));
        }
    }
}