package com.example.hk.hungry;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.io.IOException;


public class MainActivity extends AppCompatActivity{
    private Button btn_sure;
    private Bean bean=new Bean();
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private CustomPushReceiver customPushReceiver=new CustomPushReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        XGPushManager.registerPush(context, "business1", new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.e("AAAAA", "注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.e("AAAAA", "注册失败" + i + s);
            }
        });

        if("CLEAR_NOTI_ACTION".equals(getIntent().getAction().toString())){
            NotificationManager mNotifiManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifiManager.cancel(1);
        }

        //
//        customPushReceiver.onTextMessage(this,new XGPushTextMessage());






        btn_sure=(Button)this.findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=getSharedPreferences("data",MODE_PRIVATE);
                String  id=sp.getString("orderId", "null");
                String userId=sp.getString("userId","null");
                Log.e("AAAAAA",id);
                //加密数据
                String string = "confirmOrder@"+userId+"@"+id+"@1";
                String md5String = MySecurityUtil.string2MD5(string);
                bean.setSign(md5String);
                bean.setUserId(userId);
                bean.setOrdersId(id);
                bean.setResult("1");
                //使用gson把数据分装成json数据
                Gson gson = new Gson();
                String json = gson.toJson(bean);
                Log.e("aaaaaaaaaaa", json);
                //encrypt加密
                String encryptJson = MySecurityUtil.encrypt(json);
                //加密后在String2Base64
                String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                String url = "http://202.171.212.154:8080/hh/confirmOrder.action?json=" + encryptJsonToBase64;
                Log.e("AAAAAAAAA", url);
                //开始访问得到返回数据
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("aaaaaaa", "回复onFailure");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        //NOT UI Thread
                        if (response.isSuccessful()) {
                            //将所得到的传回回去

                            //解密数据
                            String base64ToString = MySecurityUtil.Base64ToString(response.body().string());
                            String json = MySecurityUtil.decrypt(base64ToString);
                            Log.e("aaaaaaa", "回复成功" +json);
//                                Message message = Message.obtain();
//                                message.obj = response.body().string();
//                                message.what = Finish;
//                                mhandler.sendMessage(message);
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XGPushManager.unregisterPush(this);
    }

}
