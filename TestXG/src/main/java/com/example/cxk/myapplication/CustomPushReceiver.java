package com.example.cxk.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by cxk on 2016/5/5.
 */
public class CustomPushReceiver extends XGPushBaseReceiver {
    private int i = 0;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private AllMessageOrderListBean allMessageOrderListBean=new AllMessageOrderListBean();
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.e("收到透传消息", xgPushTextMessage.getContent() + "------" + xgPushTextMessage.getCustomContent() + "---------" + xgPushTextMessage.getTitle());
        allMessageOrderListBean=gson.fromJson(xgPushTextMessage.getContent(),AllMessageOrderListBean.class);
        SharedPreferences sp=context.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("orderId",allMessageOrderListBean.getOrdersId()+"");
        editor.putString("userId",allMessageOrderListBean.getUserId()+"");
        editor.commit();
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction("CLEAR_NOTI_ACTION");
        context.sendBroadcast(resultIntent);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("你有1条消息")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(resultPendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText(xgPushTextMessage.getContent().toString());
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.e("AAAAAAA", "被电击");
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {
        Log.e("AAAAAAA", notifiShowedRlt.toString());
    }

}
