package com.whf.messagerelayer.receiver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;

import java.text.SimpleDateFormat;

/**
 * Created by lingxuan on 2017/7/27.
 */
public class CallObserver extends android.database.ContentObserver {
    Context context;

    public CallObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor query = null;
        try {
            if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CALL_LOG") == 0) {
                query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                if (query != null) {
                    if (query.moveToNext() && Integer.parseInt(query.getString(query.getColumnIndex("type"))) == CallLog.Calls.MISSED_TYPE) {
                        Log.d("sms", "miss call:" + query.getString(query.getColumnIndex("number")));
//                        for (int i = 0; i < query.getColumnCount(); ++i) {
//                            Log.d("sms", i + " " + query.getColumnName(i) + " " + query.getString(i));
//                        }
                        String mobile = query.getString(query.getColumnIndex("number"));
                        long date = query.getLong(query.getColumnIndex("date"));
                        String location = query.getString(query.getColumnIndex("geocoded_location"));
                        Intent serviceIntent = new Intent(context, SmsService.class);
                        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT, "未接来电:" + mobile + " " + simpleDateFormat.format(date) + " " + location);
                        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE, mobile);
                        context.startService(serviceIntent);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (query != null) {
                query.close();
            }
            //用一次注销一次
            context.getContentResolver().unregisterContentObserver(this);
        }
    }
}