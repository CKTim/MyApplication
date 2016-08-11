package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import bean.FooddetailBean;
import bean.MySecurityAddtoLoveBean;
import bean.MySecurityFoodDetailBean;
import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;
import utils.MySecurityUtil;
import widget.Custom_fooddetail_scrollview;

/**
 * Created by cxk on 2016/4/8.
 */
public class FooddetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rl_actionbar;
    private ImageView iv_food;
    private Custom_fooddetail_scrollview customScrollview;
    private RelativeLayout rl_toShop;
    private RelativeLayout rl_finishOrding;
    private TextView tv_total, tv_prices, tv_food_name, tv_sales, tv_food_descripse, tv_collected;
    private Button btn_back, btn_addtolove;
    private DisplayImageOptions options;
    private String url;
    private final static int FINISH = 0;
    private FooddetailBean fooddetailBean;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String loginName;
    private MySecurityFoodDetailBean mySecurityFoodDetailBean=new MySecurityFoodDetailBean();
    private MySecurityAddtoLoveBean mySecurityAddtoLoveBean=new MySecurityAddtoLoveBean();
    private android.os.Handler mhandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    Log.e("AAAAAAAAAA",json);
                    // 使用gson映射得到bean类
                    fooddetailBean = gson.fromJson(json, FooddetailBean.class);
                    //将数据settext进去
                    tv_prices.setText("HK$" + fooddetailBean.price);
                    tv_food_name.setText(fooddetailBean.name);
                    tv_sales.setText("Monthly sales " + fooddetailBean.sales);
                    tv_food_descripse.setText(fooddetailBean.intro);
                    tv_collected.setText(fooddetailBean.beCollected + " Collected");
                    if (fooddetailBean.isCollect.equals("true")) {
                        btn_addtolove.setSelected(true);
                    } else {
                        btn_addtolove.setSelected(false);
                    }
                    ImageLoader.getInstance().displayImage(fooddetailBean.url, iv_food,
                            options);
                    rl_toShop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FooddetailActivity.this, ShopActivity.class);
                            intent.putExtra("id", fooddetailBean.businessid);
                            startActivity(intent);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fooddetail_activity);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化各个控件ID
        initIDs();
        //判断应该显示底部哪个布局
        BooleanShowWhat();
        //获取数据并且显示
        GetData();
        //设置加载imageloader的option
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loading_url2)
                .showImageOnFail(R.mipmap.loadingfailed).cacheInMemory(true).cacheOnDisk(true).build();
//
//        //设置是否收藏
//        if (getIntent().getStringExtra("isCollect").equals("true")) {
//            btn_addtolove.setSelected(true);
//        } else {
//            btn_addtolove.setSelected(false);
//        }

        //收藏按钮监听事件
        btn_addtolove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_addtolove.isSelected()) {
                    btn_addtolove.setSelected(false);
                    //取消收藏
                    //加密数据
                    String string = "deleteFavorite@"+loginName+"@2"+"@"+getIntent().getStringExtra("id").toString();
                    String md5String = MySecurityUtil.string2MD5(string);;
                    mySecurityAddtoLoveBean.setUserId(loginName);
                    mySecurityAddtoLoveBean.setSign(md5String);
                    mySecurityAddtoLoveBean.setType("2");
                    mySecurityAddtoLoveBean.setTypeId(getIntent().getStringExtra("id").toString());
                    String json = gson.toJson(mySecurityAddtoLoveBean);
                    //encrypt加密
                    String encryptJson = MySecurityUtil.encrypt(json);
                    //加密后在String2Base64
                    String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                    String path = "http://202.171.212.154:8080/hh/deleteFavorite.action?json=" + encryptJsonToBase64;
                    //开始访问得到返回数据
                    Request request = new Request.Builder()
                            .url(path)
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
//                                    //将所得到的list传回回去
//                                    Message message = Message.obtain();
//                                    message.obj = response.body().string();
//                                    message.what = FINISH;
//                                    mhandler.sendMessage(message);
                                Log.e("AAAAAAAA", "取消收藏成功");
                            }
                        }
                    });

                } else {
                    btn_addtolove.setSelected(true);
                    //添加收藏
                    //加密数据
                    String string = "saveFavorite@"+loginName+"@2"+"@"+getIntent().getStringExtra("id").toString();
                    String md5String = MySecurityUtil.string2MD5(string);
                    mySecurityAddtoLoveBean.setUserId(loginName);
                    mySecurityAddtoLoveBean.setSign(md5String);
                    mySecurityAddtoLoveBean.setType("2");
                    mySecurityAddtoLoveBean.setTypeId(getIntent().getStringExtra("id").toString());
                    String json = gson.toJson(mySecurityAddtoLoveBean);
                    //encrypt加密
                    String encryptJson = MySecurityUtil.encrypt(json);
                    //加密后在String2Base64
                    String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                    String path = "http://202.171.212.154:8080/hh/saveFavorite.action?json=" + encryptJsonToBase64;
                    //开始访问得到返回数据
                    Request request = new Request.Builder()
                            .url(path)
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
//                                    //将所得到的list传回回去
//                                    Message message = Message.obtain();
//                                    message.obj = response.body().string();
//                                    message.what = FINISH;
//                                    mhandler.sendMessage(message);
                                Log.e("AAAAAAAA", "收藏成功");
                            }
                        }
                    });
                }
            }
        });
        //为scrollview设置滚动监听事件
        customScrollview.setOnScrollListener(new Custom_fooddetail_scrollview.OnScrollListener() {
            @Override
            public void OnScroll(int y) {
                //使actionbar随着下拉颜色渐变
                if (0 <= y && y <= 255) {

                }
                if (y > 255) {
                    rl_actionbar.getBackground().setAlpha(255);
                }
            }
        });

    }

    private void GetData() {
        //加密数据
        String string = "getFood@"+loginName+"@"+getIntent().getStringExtra("id").toString();
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityFoodDetailBean.setUserId(loginName);
        mySecurityFoodDetailBean.setSign(md5String);
        mySecurityFoodDetailBean.setFoodId(getIntent().getStringExtra("id").toString());
        String json = gson.toJson(mySecurityFoodDetailBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        url = "http://202.171.212.154:8080/hh/getFood.action?json=" + encryptJsonToBase64;
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
                    message.what = FINISH;
                    mhandler.sendMessage(message);
                }
            }
        });
    }

    private void BooleanShowWhat() {
        String ifShow = getIntent().getStringExtra("ifShow");
        if (ifShow.equals("show_gotoshop")) {
            rl_toShop.setVisibility(View.VISIBLE);
            rl_finishOrding.setVisibility(View.GONE);
        } else if (ifShow.equals("show_finishording")) {
            rl_toShop.setVisibility(View.GONE);
            rl_finishOrding.setVisibility(View.VISIBLE);
            tv_total = (TextView) rl_finishOrding.findViewById(R.id.tv_totalprices);
            String total_prices = getIntent().getStringExtra("totalPrices");
            tv_total.setText(total_prices);

        }
    }

    private void initIDs() {
        tv_prices = (TextView) this.findViewById(R.id.tv_prices);
        tv_food_name = (TextView) this.findViewById(R.id.food_name);
        tv_sales = (TextView) this.findViewById(R.id.food_xiaoliang);
        tv_food_descripse = (TextView) this.findViewById(R.id.tv_food_descripse);
        tv_collected = (TextView) this.findViewById(R.id.food_shoucang);
        iv_food = (ImageView) this.findViewById(R.id.iv_food);
        rl_finishOrding = (RelativeLayout) this.findViewById(R.id.rl_bottom_finishorder);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_addtolove = (Button) this.findViewById(R.id.btn_weishoucan);
        customScrollview = (Custom_fooddetail_scrollview) this.findViewById(R.id.scrollview);
        rl_toShop = (RelativeLayout) this.findViewById(R.id.rl_bottom_gotoshop);
        btn_back.setOnClickListener(this);
        btn_addtolove.setOnClickListener(this);
        //获取登录帐号
        SharedPreferences sp=FooddetailActivity.this.getSharedPreferences("userMessage",MODE_PRIVATE);
        loginName=sp.getString("loginName","");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                FooddetailActivity.this.finish();
                break;
            case R.id.btn_weishoucan:

                break;
        }
    }
}
