package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cxk.myapplication.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bean.AllMessageOrderListBean;
import bean.FooddetailBean;
import utils.HttpUtilsSendOrder;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/15.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rl_edit;
    private RelativeLayout rl_blank_myaddress;
    private TextView tv_phone, tv_name, tv_myaddress, tv_Totalamount;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String ifFirst;
    private Button btn_finishOrder;
    private AllMessageOrderListBean allMessageOrderListBean;
    private String url;
    private RadioButton RadioButton_paypal, RadioButton_Cash;
    private Button btn_opendrawer, btn_openMylove;
    private DrawerLayout drawerLayout;
    //初始化打开paypal的PayPalConfiguration
    public static final PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId("Af7E-23ocVJZ-TA613DqhDuT6q1i5S0hijms8CBgygkeYm27g68b7L_QNb_knoP4cVVMkz5LGt97OdE6");
    //用于存放上传到paypal的订单
    public List<PayPalItem> list = new ArrayList<>();
    private Gson gson = new Gson();
    private static final int FINISH = 0;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String loginName;
    private android.os.Handler mhandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                      Toast.makeText(ProfileActivity.this,"商家不在线",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化控件ID
        initIDS();
        sp = this.getSharedPreferences("address", MODE_PRIVATE);
        editor = sp.edit();
        //获取isfirst的值，如果没有值就赋予为true
        ifFirst = sp.getString("ifFirst", "true");
        if (ifFirst.equals("true")) {
            editor.putString("ifFirst", "true");
        }
        editor.commit();

        //获取传递过来的allMessageOrderListBean
        allMessageOrderListBean = (AllMessageOrderListBean) getIntent().getSerializableExtra("allMessageBean");
        //将总价设置进来
        tv_Totalamount.setText(allMessageOrderListBean.getAmount() + "");
        //需要先开启paypal的服务
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void initIDS() {
        rl_edit = (RelativeLayout) this.findViewById(R.id.rl_edit);
        rl_blank_myaddress = (RelativeLayout) this.findViewById(R.id.rl_blank_myaddress);
        tv_phone = (TextView) this.findViewById(R.id.tv_phone);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_myaddress = (TextView) this.findViewById(R.id.tv_myaddress);
        btn_finishOrder = (Button) this.findViewById(R.id.btn_finishOrder);
        tv_Totalamount = (TextView) this.findViewById(R.id.tv_Totalamount);
        RadioButton_paypal = (RadioButton) this.findViewById(R.id.RadioButton_paypal);
        RadioButton_Cash = (RadioButton) this.findViewById(R.id.RadioButton_cash);
        btn_opendrawer = (Button) this.findViewById(R.id.btn_opendrawer);
        btn_openMylove = (Button) this.findViewById(R.id.btn_openmylove);
        drawerLayout = (DrawerLayout) this.findViewById(R.id.drawerlayout);
        btn_opendrawer.setOnClickListener(this);
        btn_openMylove.setOnClickListener(this);
        btn_finishOrder.setOnClickListener(this);
        rl_edit.setOnClickListener(this);
        rl_blank_myaddress.setOnClickListener(this);
        //获取登录帐号
        SharedPreferences sp = ProfileActivity.this.getSharedPreferences("userMessage", MODE_PRIVATE);
        loginName = sp.getString("loginName", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_edit:
                Intent intent = new Intent(ProfileActivity.this, EditaddressActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_blank_myaddress:
                sp = getSharedPreferences("address", MODE_PRIVATE);
                Intent intent1 = new Intent(ProfileActivity.this, EditaddressActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_finishOrder:
                //判断如果选中现金支付，直接提交订单
                if (RadioButton_Cash.isChecked()) {
                    //加密数据
                    String string = "newOrder@" + sp.getString("name", "") + "@" + sp.getString("phone", "") + "@" + getIntent().getStringExtra("id") + "@" + loginName;
                    String md5String = MySecurityUtil.string2MD5(string);
                    allMessageOrderListBean.setPayment(2);
                    allMessageOrderListBean.setUserId(Integer.parseInt(loginName));
                    allMessageOrderListBean.setEmail(sp.getString("email", ""));
                    allMessageOrderListBean.setName(sp.getString("name", ""));
                    allMessageOrderListBean.setSurname(sp.getString("surname", ""));
                    allMessageOrderListBean.setRoomAddress(sp.getString("detailAddress", ""));
                    allMessageOrderListBean.setTel(sp.getString("phone", ""));
                    allMessageOrderListBean.setDistrictAddress(sp.getString("city", "") + sp.getString("district", ""));
                    allMessageOrderListBean.setSign(md5String);
                    //使用gson把数据分装成json数据
                    Gson gson = new Gson();
                    String json = gson.toJson(allMessageOrderListBean);
                    Log.e("aaaaaaaaaaa", json);
                    //encrypt加密
                    String encryptJson = MySecurityUtil.encrypt(json);
                    //加密后在String2Base64
                    String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                    url = "http://202.171.212.154:8080/hh/newOrder.action?json=" + encryptJsonToBase64;
                    Log.e("AAAAAAAAAAA", url);
                    //开始访问得到返回数据
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.e("aaaaaaa", "onFailure");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            //NOT UI Thread
                            if (response.isSuccessful()) {
                                //解密数据
                                String base64ToString = MySecurityUtil.Base64ToString(response.body().string());
                                String json = MySecurityUtil.decrypt(base64ToString);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    Log.e("aaaaaaaaaa", Integer.parseInt(jsonObject.get("ret_code").toString()) + "");
                                    if (Integer.parseInt(jsonObject.get("ret_code").toString()) == 0) {
                                        if (jsonObject.get("success").toString().equals("true")) {
                                            Intent intent = new Intent(ProfileActivity.this, OrderDetailActivity.class);
                                            intent.putExtra("orderId", jsonObject.get("orderId").toString());
                                            startActivity(intent);
                                        }
                                    } else if (Integer.parseInt(jsonObject.get("ret_code").toString()) == 48) {
                                        Message message = Message.obtain();
                                        message.what = FINISH;
                                        mhandler.sendMessage(message);
                                        Log.e("aaaaaaaaaa", "商家不在线");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

                } else if (RadioButton_paypal.isChecked()) {
                    //如果选中paypal付款，则跳转到付款页面
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(allMessageOrderListBean.amount), "HKD", "总共需要：", PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent0 = new Intent(ProfileActivity.this, PaymentActivity.class);
                    intent0.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    startActivityForResult(intent0, 0);
                }

                break;
            case R.id.btn_openmylove:
                Intent intent0 = new Intent(ProfileActivity.this, MyLoveActivity.class);
                startActivity(intent0);
                break;
            case R.id.btn_opendrawer:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.getString("ifFirst", "").equals("true")) {
            rl_blank_myaddress.setVisibility(View.VISIBLE);
        } else {
            rl_blank_myaddress.setVisibility(View.GONE);
            tv_phone.setText(sp.getString("phone", ""));
            tv_name.setText(sp.getString("surname", "") + sp.getString("name", ""));
            tv_myaddress.setText(sp.getString("city", "") + sp.getString("district", "") + sp.getString("detailAddress", ""));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //获得返回结果
                PaymentConfirmation configuration = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (configuration != null) {
                    try {
                        //此处可以传递paymentID供服务器验证是否收到钱
                        //加密数据
                        String string = "newOrder@" + sp.getString("name", "") + "@" + sp.getString("phone", "") + "@" + getIntent().getStringExtra("id") + "@" + loginName;
                        String md5String = MySecurityUtil.string2MD5(string);
                        Log.e("paymentExample", configuration.toJSONObject().toString(4));
                        allMessageOrderListBean.setPayment(0);
                        allMessageOrderListBean.setUserId(Integer.parseInt(loginName));
                        allMessageOrderListBean.setEmail(sp.getString("email", ""));
                        allMessageOrderListBean.setName(sp.getString("name", ""));
                        allMessageOrderListBean.setSurname(sp.getString("surname", ""));
                        allMessageOrderListBean.setRoomAddress(sp.getString("detailAddress", ""));
                        allMessageOrderListBean.setTel(sp.getString("phone", ""));
                        allMessageOrderListBean.setDistrictAddress(sp.getString("city", "") + sp.getString("district", ""));
                        allMessageOrderListBean.setSign(md5String);
                        //使用gson把数据分装成json数据
                        Gson gson = new Gson();
                        String json = gson.toJson(allMessageOrderListBean);
                        Log.e("aaaaaaaaaaa", json);
                        //encrypt加密
                        String encryptJson = MySecurityUtil.encrypt(json);
                        //加密后在String2Base64
                        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                        url = "http://202.171.212.154:8080/hh/newOrder.action?json=" + encryptJsonToBase64;
                        Log.e("aaaaaaaaaaa", url);
                        //开始访问得到返回数据
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                Log.e("aaaaaaa", "onFailure");
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                //NOT UI Thread
                                if (response.isSuccessful()) {
                                    //解密数据
                                    String base64ToString = MySecurityUtil.Base64ToString(response.body().string());
                                    String json = MySecurityUtil.decrypt(base64ToString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(json);
                                        Log.e("aaaaaaaaaa", Integer.parseInt(jsonObject.get("ret_code").toString()) + "");
                                        if (Integer.parseInt(jsonObject.get("ret_code").toString()) == 0) {
                                            if (jsonObject.get("success").toString().equals("true")) {
                                                Intent intent = new Intent(ProfileActivity.this, OrderDetailActivity.class);
                                                intent.putExtra("orderId", jsonObject.get("orderId").toString());
                                                startActivity(intent);
                                            }
                                        } else if (Integer.parseInt(jsonObject.get("ret_code").toString()) == 48) {
                                            Message message = Message.obtain();
                                            message.what = FINISH;
                                            mhandler.sendMessage(message);
                                            Log.e("aaaaaaaaaa", "商家不在线");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
