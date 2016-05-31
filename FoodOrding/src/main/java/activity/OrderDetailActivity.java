package activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cxk.myapplication.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.MySecurityOrderDetailBean;
import bean.MyloveBean;
import bean.OrderDetailBean;
import bean.OrderListBean;
import bean.ShopMessageBean;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/5/6.
 */
public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private String loginName;
    private ListView lv;
    private TextView tv_total,tv_OrderStatus;
    private MyFoodListViewAdapter adapter;
    private List<Map<String,Object>> list=new ArrayList<>();
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityOrderDetailBean mySecurityOrderDetailBean=new MySecurityOrderDetailBean();
    private static final int FINISH=0;
    private String path;
    private DrawerLayout drawerLayout;
    private ImageView iv_showMore,iv_shop;
    private RelativeLayout rl_moredetails,rl_shoppic;
    private Button btn_opendrawer,btn_openmylove;
    private OrderDetailBean orderDetailBean;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    Log.e("OrderDetail",json);
                    // 使用gson映射得到bean类
                    orderDetailBean = gson.fromJson(json, OrderDetailBean.class);
                    //先定义一个加载图片的option,加载商家图片
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic)
                            .showImageOnFail(R.mipmap.image2).cacheInMemory(true).build();
                    //加载餐厅照片
                    ImageLoader.getInstance().displayImage(orderDetailBean.url, iv_shop,
                            options);
                    //设置总价格
                    tv_total.setText("Total:   HK$"+orderDetailBean.amount);
                    //判断订单状态，处于接单还是配送
                    int Order_status=orderDetailBean.isfinish;
                    if(Order_status==0){
                        tv_OrderStatus.setText("Your order have submited");
                    }else if(Order_status==1){
                        tv_OrderStatus.setText("Your order have confirmed");
                    }else if(Order_status==2){
                        tv_OrderStatus.setText("Your order have distributed");
                    }else if(Order_status==3){
                        tv_OrderStatus.setText("Your order have finished");
                    }
                     //将bean类里面的东西装载到list中便于使用
                    for (int i = 0; i < orderDetailBean.foodlist.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("foodName", orderDetailBean.foodlist.get(i).name);
                        map.put("foodPrice", orderDetailBean.foodlist.get(i).price);
                        map.put("foodMount",orderDetailBean.foodlist.get(i).num);
                        map.put("special",orderDetailBean.foodlist.get(i).special);
                        list.add(map);
                    }

                    adapter=new MyFoodListViewAdapter();
                    lv.setAdapter(adapter);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetail_activity);
        //如果安卓5.0设置状态栏为orange
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化各个控件ID
        initIDS();
        //注册信鸽
        XGPushManager.registerPush(this, "user"+loginName, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.e("AAAAAA", "用户端登录成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.e("AAAAAA", "用户端登录失败" + i + s);
            }
        });
        //获取订单详情
        GetData();
//        //实现点击通知消失
//        if("CLEAR_NOTI_ACTION".equals(getIntent().getAction().toString())){
//            NotificationManager mNotifiManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotifiManager.cancel(1);
//        }
    }

    private void GetData() {
        //加密数据
        String string ="orderDetails@"+getIntent().getStringExtra("orderId");
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityOrderDetailBean.setOrdersId(getIntent().getStringExtra("orderId").toString());
        mySecurityOrderDetailBean.setSign(md5String);
        String json = gson.toJson(mySecurityOrderDetailBean);
        Log.e("AAAAAAA",json);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        path = "http://202.171.212.154:8080/hh/orderDetails.action?json=" + encryptJsonToBase64;
        Log.e("AAAAAAA",path);
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
                    //将所得到的传回回去
                    Message message = Message.obtain();
                    message.obj = response.body().string();
                    message.what = FINISH;
                    mhandler.sendMessage(message);
                }
            }
        });


    }

    private void initIDS() {
        lv=(ListView)this.findViewById(R.id.MyOrder_lv);
        tv_total=(TextView)this.findViewById(R.id.tv_total);
        tv_OrderStatus=(TextView)this.findViewById(R.id.tv_order_status);
        drawerLayout=(DrawerLayout)this.findViewById(R.id.drawerlayout);
        iv_showMore=(ImageView)this.findViewById(R.id.iv_showMore);
        btn_opendrawer=(Button)this.findViewById(R.id.btn_opendrawer);
        btn_openmylove=(Button)this.findViewById(R.id.btn_openmylove);
        rl_moredetails=(RelativeLayout)this.findViewById(R.id.rl_moredetails);
        rl_shoppic=(RelativeLayout)this.findViewById(R.id.rl_shoppic);
        iv_shop=(ImageView)this.findViewById(R.id.iv_shop);
        btn_opendrawer.setOnClickListener(this);
        btn_openmylove.setOnClickListener(this);
        iv_showMore.setOnClickListener(this);
        rl_shoppic.setOnClickListener(this);
        //获取登录帐号
        SharedPreferences sp=OrderDetailActivity.this.getSharedPreferences("userMessage",MODE_PRIVATE);
        loginName=sp.getString("loginName","");
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.btn_openmylove:
              Intent intent=new Intent(this,MyLoveActivity.class);
              startActivity(intent);
              break;
          case R.id.btn_opendrawer:
              drawerLayout.openDrawer(Gravity.LEFT);
              break;
          case R.id.iv_showMore:
              if (rl_moredetails.getVisibility()==View.VISIBLE) {
                  rl_moredetails.setVisibility(View.GONE);
              }else if(rl_moredetails.getVisibility()==View.GONE){
                  rl_moredetails.setVisibility(View.VISIBLE);
              }

              break;
          case R.id.rl_shoppic:
              Intent intent0=new Intent(OrderDetailActivity.this,ShopActivity.class);
              intent0.putExtra("id",orderDetailBean.businessid+"");
              Log.e("AAAAAAA",orderDetailBean.businessid+"");
              startActivity(intent0);
              break;
      }
    }

    //定义一个新的食物列表适配器
    public class MyFoodListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.history_listview_lv, null);
                holder.tv_foodname = (TextView) convertView.findViewById(R.id.tv_foodname);
                holder.tv_mount = (TextView) convertView.findViewById(R.id.tv_mount);
                holder.tv_prices = (TextView) convertView.findViewById(R.id.tv_prices);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_foodname.setText("-" + list.get(position).get("foodName").toString());
            holder.tv_mount.setText("×"+list.get(position).get("foodMount").toString());
            holder.tv_prices.setText("HK$"+list.get(position).get("foodPrice").toString());
            return convertView;
        }

        class ViewHolder {
            //食物名字
            TextView tv_foodname;
            //食物数量
            TextView tv_mount;
            //食物价格
            TextView tv_prices;
        }

    }


}
