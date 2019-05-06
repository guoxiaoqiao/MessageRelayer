package com.whf.messagerelayer.receiver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;
import com.whf.messagerelayer.utils.NativeDataManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by lingxuan on 2017/7/27.
 */
public class CallObserver extends android.database.ContentObserver {

    private final Context context;
    private final boolean recallSetting;
    private final DateFormat simpleDateFormat = SimpleDateFormat.getDateTimeInstance();

    public CallObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.recallSetting = new NativeDataManager(this.context).getRecallSetting();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor query = null;
        try {
            if (!this.recallSetting) {
                return;
            }
            if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CALL_LOG") != 0) {
                return;
            }

            query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");

            if (query == null) {
                return;
            }

            if (query.moveToNext() &&
                    Integer.parseInt(query.getString(query.getColumnIndex("type"))) == CallLog.Calls.MISSED_TYPE) {
                Intent serviceIntent = buildIntentFromQuery(query);
                context.startService(serviceIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (query != null) {
                query.close();
            }
            //TODO: 用一次注销一次???
            context.getContentResolver().unregisterContentObserver(this);
        }
    }

    @NonNull
    private Intent buildIntentFromQuery(Cursor query) {
        String mobile = query.getString(query.getColumnIndex("number"));
        long date = query.getLong(query.getColumnIndex("date"));
        String location = query.getString(query.getColumnIndex("geocoded_location"));
        Intent serviceIntent = new Intent(context, SmsService.class);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT, "未接来电:" + mobile + " " + simpleDateFormat.format(date) + " " + location);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE, mobile);
        return serviceIntent;
    }
}
