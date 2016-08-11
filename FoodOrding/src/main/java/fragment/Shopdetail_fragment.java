package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import activity.ShopMapActivity;
import bean.MySecurityShopMessageBean;
import bean.ShopMessageBean;
import cn.gdin.hk.hungry.R;
import utils.HttpUtilsGetJson;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/9.
 */
public class Shopdetail_fragment extends Fragment implements View.OnClickListener {
    private View v;
    private String id;
    private TextView tv_shopName, tv_time, tv_address, tv_distance;
    private Button btn_call;
    private RelativeLayout rl_shopAddress;
    private HttpUtilsGetJson httpUtilsGetJson = new HttpUtilsGetJson();
    private ShopMessageBean shopMessageBean=new ShopMessageBean();
    private ShopMessageBean ShopmessageBean=new ShopMessageBean();
    private String url;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption;
    private Double latidue, longitude;
    private static final int Finish = 0;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityShopMessageBean mySecurityShopMessageBean=new MySecurityShopMessageBean();
    private String loginName;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Finish:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    // 使用gson映射得到bean类
                    ShopmessageBean= gson.fromJson(json, ShopMessageBean.class);
                    tv_shopName.setText(ShopmessageBean.name);
                    tv_time.setText("Working Hours:   " + ShopmessageBean.time);
                    tv_address.setText(ShopmessageBean.address);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.shopdetail_fragment, null);
        id = getArguments().getString("id").toString();
        //初始化各个控件ID
        initIDs();
        //获取商家电话，地址等信息
        GetData();
        //获取数据
        CaluteDistance();
        return v;
    }

    private void CaluteDistance() {
        //定位到当前位置并且获取相应经纬度
        mLocationClient = new AMapLocationClient(getActivity());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        latidue = amapLocation.getLatitude();//获取当前位置的纬度
                        longitude = amapLocation.getLongitude();//获取当前位置的经度
                        LatLng startLatlng = new LatLng(latidue, longitude);
                        Log.e("nihao",latidue+".........");
                        LatLng endLatlng = new LatLng(Float.valueOf(getArguments().getString("latidue").toString()), Float.valueOf(getArguments().getString("longitude").toString()));
                        Float distance = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
                        //设置当前店铺大概离你多少距离
                        tv_distance.setText("约" + Math.round(distance) + "米");


                    }
                }
            }
        });
    }

    private void GetData() {
        //加密数据
        String string = "getBusiness@"+id+"@"+loginName;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityShopMessageBean.setUserId(loginName);
        mySecurityShopMessageBean.setSign(md5String);
        mySecurityShopMessageBean.setBusinessId(id);
        String json = gson.toJson(mySecurityShopMessageBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        url = "http://202.171.212.154:8080/hh/getBusiness.action?json=" + encryptJsonToBase64;
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
                    //将所得到的传回回去
                    Message message = Message.obtain();
                    message.obj = response.body().string();
                    message.what = Finish;
                    mhandler.sendMessage(message);
                }
            }
        });


    }

    private void initIDs() {
        tv_shopName = (TextView) v.findViewById(R.id.tv_shopName);
        tv_time = (TextView) v.findViewById(R.id.tv_time);
        tv_address = (TextView) v.findViewById(R.id.tv_address);
        tv_distance = (TextView) v.findViewById(R.id.tv_distance);
        btn_call = (Button) v.findViewById(R.id.btn_call);
        rl_shopAddress=(RelativeLayout)v.findViewById(R.id.rl_shopname_shopaddress);
        btn_call.setOnClickListener(this);
        rl_shopAddress.setOnClickListener(this);
        //获取登录帐号
        SharedPreferences sp=getActivity().getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_call:
                // 调用拨号界面
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:" + ShopmessageBean.tel));
                startActivity(intent);
                break;
            case R.id.rl_shopname_shopaddress:
                Intent intent0=new Intent(getActivity(), ShopMapActivity.class);
                intent0.putExtra("endlatidue",getArguments().getString("latidue").toString());
                intent0.putExtra("endlongitude",getArguments().getString("longitude").toString());
                startActivity(intent0);
                break;
        }
    }
}
