package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.HistoryOrderBean;
import bean.MySecurityHistoryOrderBean;
import bean.MyloveBean;
import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/14.
 */
public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_back;
    private MyListViewAdapter adapter;
    //    private MyFoodListViewAdapter food_adapter;
    private ListView lv;
    private List<Map<String, Object>> list = new ArrayList<>();
    private List<Map<String, Object>> list3;
    private TextView tv_title;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String loginName;
    private MySecurityHistoryOrderBean mySecurityHistoryOrderBean = new MySecurityHistoryOrderBean();
    private String url;
    private final static int FINISH = 1;
    private final static int UPDATA = 2;
    private int FINISHOk = 0;
    private int page = 1;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    Log.e("AAAAAAAA", json);
                    // 使用gson映射得到bean类
                    HistoryOrderBean historyOrderBean = gson.fromJson(json, HistoryOrderBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    for (int i = 0; i < historyOrderBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("OrderId", historyOrderBean.datas.get(i).orderid);
                        map.put("BussinessName", historyOrderBean.datas.get(i).businessname);
                        map.put("url", historyOrderBean.datas.get(i).url);
                        map.put("isFinish", historyOrderBean.datas.get(i).isfinish);
                        map.put("isComment", historyOrderBean.datas.get(i).iscomment);
                        map.put("amount",historyOrderBean.datas.get(i).amount);
                        list.add(map);
                    }
                    adapter = new MyListViewAdapter();
                    lv.setAdapter(adapter);
                    break;

                case UPDATA:
                    //解密数据
                    String base64ToString1 = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json1 = MySecurityUtil.decrypt(base64ToString1);
                    // 使用gson映射得到解密好的数据
                    MyloveBean myloveBean1 = gson.fromJson(json1, MyloveBean.class);
                    if (myloveBean1.datas.size() == 0) {
                        Toast.makeText(HistoryActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        list3 = new ArrayList<>();
                        // 使用gson映射得到bean类
                        HistoryOrderBean historyOrderBean1 = gson.fromJson(json1, HistoryOrderBean.class);
                        // 将bean类里面的东西装载到list中便于使用
                        for (int i = 0; i < historyOrderBean1.datas.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("OrderId", historyOrderBean1.datas.get(i).orderid);
                            map.put("BussinessName", historyOrderBean1.datas.get(i).businessname);
                            map.put("url", historyOrderBean1.datas.get(i).url);
                            map.put("isFinish", historyOrderBean1.datas.get(i).isfinish);
                            map.put("isComment", historyOrderBean1.datas.get(i).iscomment);
                            list3.add(map);
                        }
                        list.addAll(list3);
                        adapter.notifyDataSetChanged();
                    }
                    FINISHOk = 0;
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化ID
        initIDs();
        //首次获取数据
        GetData();
        //下拉加载更多
        UpdataData();
        //listview监听事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("AAAAAA", "dianji");
                Intent intent = new Intent(HistoryActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderId", list.get(position).get("OrderId").toString());
                startActivity(intent);
            }
        });
    }

    private void UpdataData() {
        //listview滑动监听事件，实现分类加载
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 监听滑动到底部
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //这个if的作用防止页数乱加
                        if (FINISHOk == 0) {
                            page = page + 1;
                            FINISHOk = 1;
                            //加密数据
                            String string = "userHistoryOrder@" + loginName + "@" + page + "@5";
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityHistoryOrderBean.setUserId(loginName);
                            mySecurityHistoryOrderBean.setSign(md5String);
                            mySecurityHistoryOrderBean.setPageNow(page + "");
                            mySecurityHistoryOrderBean.setPageSize(5 + "");
                            String json = gson.toJson(mySecurityHistoryOrderBean);
                            Log.e("AAAAAAAAA", json);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            url = "http://202.171.212.154:8080/hh/userHistoryOrder.action?json=" + encryptJsonToBase64;
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
                                        message.what = UPDATA;
                                        mhandler.sendMessage(message);
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });



    }

    private void GetData() {
        //加密数据
        String string = "userHistoryOrder@" + loginName + "@" + page + "@5";
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityHistoryOrderBean.setUserId(loginName);
        mySecurityHistoryOrderBean.setSign(md5String);
        mySecurityHistoryOrderBean.setPageNow(page + "");
        mySecurityHistoryOrderBean.setPageSize(5 + "");
        String json = gson.toJson(mySecurityHistoryOrderBean);
        Log.e("AAAAAAAAA", json);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        url = "http://202.171.212.154:8080/hh/userHistoryOrder.action?json=" + encryptJsonToBase64;
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

    private void initIDs() {
        btn_back = (Button) this.findViewById(R.id.btn_back);
        lv = (ListView) this.findViewById(R.id.myhistory_lv);
        tv_title = (TextView) this.findViewById(R.id.actionbar_title);
        btn_back.setOnClickListener(this);
        list = new ArrayList<Map<String, Object>>();
        tv_title.setText("History");
        //获取登录帐号
        SharedPreferences sp = HistoryActivity.this.getSharedPreferences("userMessage", MODE_PRIVATE);
        loginName = sp.getString("loginName", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                HistoryActivity.this.finish();
                break;
        }
    }

    //定义一个新的历史记录listview适配器
    public class MyListViewAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.custom_history_listview, null);
                holder.iv_restaurant = (ImageView) convertView.findViewById(R.id.iv_restaurant);
                holder.tv_restaurant = (TextView) convertView.findViewById(R.id.tv_restaurant_name);
                holder.tv_total = (TextView) convertView.findViewById(R.id.tv_total);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_order_status);
                holder.btn_addComment = (Button) convertView.findViewById(R.id.btn_addComment);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.food_lv.setAdapter(food_adapter);
            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic)
                    .showImageOnFail(R.mipmap.loadingfailed).cacheInMemory(true).cacheOnDisk(true).build();
            ImageLoader.getInstance().displayImage((String) list.get(position).get("url"), holder.iv_restaurant,
                    options);
            holder.tv_restaurant.setText((String) list.get(position).get("BussinessName"));
            holder.tv_total.setText("Total:  HK$"+list.get(position).get("amount"));
            holder.btn_addComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("AAAAAAAAAA", "点击评论");
                }
            });
            String isFinish = list.get(position).get("isFinish").toString();
            if (isFinish.equals("0")) {
                holder.tv_status.setText("have submited");
            } else if (isFinish.equals("1")) {
                holder.tv_status.setText("have confirmed");
            } else if (isFinish.equals("2")) {
                holder.tv_status.setText("have finished");
            } else if (isFinish.equals("3")) {
                holder.tv_status.setText("have finished");
            }
            return convertView;
        }

        class ViewHolder {
            //餐厅或者食物照片
            ImageView iv_restaurant;
            // 餐厅或者食物名字
            TextView tv_restaurant;
            //食物总价
            TextView tv_total;
            TextView tv_status;
            Button btn_addComment;

        }
    }
//
//    //定义一个新的食物列表适配器
//    public class MyFoodListViewAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return 3;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return list_food.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.history_listview_lv, null);
//                holder.tv_foodname = (TextView) convertView.findViewById(R.id.tv_foodname);
//                holder.tv_mount = (TextView) convertView.findViewById(R.id.tv_mount);
//                holder.tv_prices = (TextView) convertView.findViewById(R.id.tv_prices);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
////		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.image1)
////				.showImageOnFail(R.drawable.image2).cacheInMemory(true).build();
////		ImageLoader.getInstance().displayImage((String) list.get(position).get("ShopPic"), holder.iv_restaurant,
////				options);
////		holder.tv_restaurant.setText((String) list.get(position).get("ShopName"));
//            return convertView;
//        }
//
//        class ViewHolder {
//            //食物名字
//            TextView tv_foodname;
//            //食物数量
//            TextView tv_mount;
//            //食物价格
//            TextView tv_prices;
//        }
//


    @Override
    protected void onResume() {
        super.onResume();
    }
}
