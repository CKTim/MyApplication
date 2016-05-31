package receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cxk.myapplication.MainActivity;
import com.example.cxk.myapplication.R;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import activity.OrderDetailActivity;

/**
 * Created by cxk on 2016/5/6.
 */
public class CustomPushReceiver extends XGPushBaseReceiver {
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
        try {
            JSONObject jsonObject=new JSONObject(xgPushTextMessage.getContent());
            String orderId=jsonObject.getString("ordersId");
            String msg=jsonObject.getString("msg");
            Intent resultIntent = new Intent(context, OrderDetailActivity.class);
            resultIntent.putExtra("orderId",orderId);
            resultIntent.setAction("CLEAR_NOTI_ACTION");
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("通知")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(resultPendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(msg);
            NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(1, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.e("AAAAAAA", "被电击");
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
