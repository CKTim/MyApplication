package activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.MySecurityAddtoLoveBean;
import bean.MySecurityHeatBean;
import bean.MyloveBean;
import cn.gdin.hk.hungry.R;
import utils.HttpUtilsAddToLove;
import utils.HttpUtilsGetJson;
import utils.ManageActivityUtils;
import utils.MySecurityUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.gdin.hk.hungry.MainActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MyLoveActivity extends Activity implements OnClickListener {
    private Button btn_addmore, btn_back;
    private DrawerLayout drawerlayout;
    private MyListViewAdapter adapter;
    private ListView lv;
    private List<Map<String, Object>> list=new ArrayList<>();
    private List<Map<String, Object>> list1;
    private List<Map<String, Object>> list3;
    private HttpUtilsGetJson httpUtilsGetJson;
    private HttpUtilsAddToLove httpUtilsAddToLove;
    private String loveUrl;
    private final static int FINISH = 1;
    private final static int UPDATA = 2;
    private int FINISHOk = 0;
    private int page = 1;
    private String path;
    private MyloveBean myloveBean;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityHeatBean mySecurityHeatBean=new MySecurityHeatBean();
    private MySecurityAddtoLoveBean mySecurityAddtoLoveBean=new MySecurityAddtoLoveBean();
    private String loginName;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    // 使用gson映射得到bean类
                    MyloveBean myloveBean = gson.fromJson(json, MyloveBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    Log.e("AAAAAAAAAA",myloveBean.datas.size()+"");
                    for (int i = 0; i < myloveBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", myloveBean.datas.get(i).id);
                        map.put("isCollect", "true");
                        map.put("name", myloveBean.datas.get(i).name);
                        //判断有没有价格
                        if (myloveBean.datas.get(i).price != null) {
                            map.put("price", myloveBean.datas.get(i).price);
                        }
                        map.put("type", myloveBean.datas.get(i).type);
                        map.put("url", myloveBean.datas.get(i).url);
                        list.add(map);
                    }
                    adapter = new MyListViewAdapter(list);
                    lv.setAdapter(adapter);
                    break;

                case UPDATA:
                    //解密数据
                    String base64ToString1 = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json1 = MySecurityUtil.decrypt(base64ToString1);
                    // 使用gson映射得到解密好的数据
                    MyloveBean myloveBean1 = gson.fromJson(json1, MyloveBean.class);
                    Log.e("AAAAAAAAAA",myloveBean1.datas.size()+"");
                    if (myloveBean1.datas.size() == 0) {
                        Toast.makeText(MyLoveActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        list3 = new ArrayList<>();
                        // 将bean类里面的东西装载到list中便于使用
                        for (int i = 0; i < myloveBean1.datas.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", myloveBean1.datas.get(i).id);
                            map.put("isCollect", "true");
                            map.put("name", myloveBean1.datas.get(i).name);
                            //判断有没有价格
                            if (myloveBean1.datas.get(i).price != null) {
                                map.put("price", myloveBean1.datas.get(i).price);
                            }
                            map.put("type", myloveBean1.datas.get(i).type);
                            map.put("url", myloveBean1.datas.get(i).url);
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
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylove_activity);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //初始化ID
        initIDs();
        //开始加载数据
        getData();
        //实现分页加载
        UpdataData();

        //listview的点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //先判断点击进去的是菜还是餐厅
                if (list.get(position).get("type").equals("1")) {
                    Intent intent = new Intent(MyLoveActivity.this, ShopActivity.class);
                    intent.putExtra("id",list.get(position).get("id").toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MyLoveActivity.this, FooddetailActivity.class);
                    intent.putExtra("ifShow", "show_gotoshop");
                    intent.putExtra("id",list.get(position).get("id").toString());
                    intent.putExtra("isCollect",list.get(position).get("isCollect").toString());
                    startActivity(intent);
                }
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
                            String string = "getAllFavorite@"+loginName+"@"+page+"@5" ;
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityHeatBean.setUserId(loginName);
                            mySecurityHeatBean.setSign(md5String);
                            mySecurityHeatBean.setPageSize("5");
                            mySecurityHeatBean.setPageNow(page + "");
                            String json = gson.toJson(mySecurityHeatBean);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            loveUrl = "http://202.171.212.154:8080/hh/getAllFavorite.action?json=" + encryptJsonToBase64;
                            //开始访问得到返回数据
                            Request request = new Request.Builder()
                                    .url(loveUrl)
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

    private void getData() {
        //加密数据
        String string = "getAllFavorite@"+loginName+"@"+page+"@5" ;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityHeatBean.setUserId(loginName);
        mySecurityHeatBean.setSign(md5String);
        mySecurityHeatBean.setPageSize("5");
        mySecurityHeatBean.setPageNow(page+"");
        String json = gson.toJson(mySecurityHeatBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        loveUrl = "http://202.171.212.154:8080/hh/getAllFavorite.action?json=" + encryptJsonToBase64;
        Log.e("AAAAAA",loveUrl);
        //开始访问得到返回数据
        Request request = new Request.Builder()
                .url(loveUrl)
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
        btn_addmore = (Button) this.findViewById(R.id.btn_addmore);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        lv = (ListView) this.findViewById(R.id.mylove_lv);
        drawerlayout = (DrawerLayout) this.findViewById(R.id.drawerlayout);
        btn_addmore.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        list = new ArrayList<Map<String, Object>>();
        //获取登录帐号
        SharedPreferences sp=MyLoveActivity.this.getSharedPreferences("userMessage",MODE_PRIVATE);
        loginName=sp.getString("loginName","");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_opendrawer:
                drawerlayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.btn_addmore:
                Intent intent = new Intent(MyLoveActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_back:
                MyLoveActivity.this.finish();
                break;
        }
    }


    //自定义一个listview适配器
    public class MyListViewAdapter extends BaseAdapter {
        private List<Map<String, Object>> list;

        public MyListViewAdapter(List<Map<String, Object>> list) {
            this.list = list;
        }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MyLoveActivity.this).inflate(R.layout.custom_home_listview, null);
                holder.iv_restaurant = (ImageView) convertView.findViewById(R.id.iv_restaurant);
                holder.tv_restaurant = (TextView) convertView.findViewById(R.id.tv_restaurant_name);
                holder.tv_Foodprices = (TextView) convertView.findViewById(R.id.tv_food_prices);
                holder.tv_Foodprices1 = (TextView) convertView.findViewById(R.id.tv_food_prices1);
                holder.btn_addto_love = (Button) convertView.findViewById(R.id.btn_restaurant_love);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //先定义一个加载图片的option
            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic)
                    .showImageOnFail(R.mipmap.loadingfailed).cacheInMemory(true).cacheOnDisk(true).build();
            //加载餐厅或者食物照片
            ImageLoader.getInstance().displayImage((String) list.get(position).get("url"), holder.iv_restaurant,
                    options);
            //设置餐厅名字或者食物名字
            holder.tv_restaurant.setText((String) list.get(position).get("name"));
            //判断有没有价格，有就设置进去
            if (list.get(position).get("price") == null) {
                holder.tv_Foodprices1.setVisibility(View.GONE);
                holder.tv_Foodprices.setVisibility(View.GONE);
            } else {
                holder.tv_Foodprices1.setVisibility(View.VISIBLE);
                holder.tv_Foodprices.setVisibility(View.VISIBLE);
                holder.tv_Foodprices.setText((String) list.get(position).get("price"));
            }

            //判断有没有加入收藏
            if (list.get(position).get("isCollect").toString().equals("true")) {
                holder.btn_addto_love.setSelected(true);
            } else {
                holder.btn_addto_love.setSelected(false);
            }
            //收藏按钮的监听
            holder.btn_addto_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.btn_addto_love.isSelected()) {
                        holder.btn_addto_love.setSelected(false);
                        //取消收藏
                        //加密数据
                        String string = "deleteFavorite@"+loginName+"@" + list.get(position).get("type")+"@"+list.get(position).get("id").toString().trim();
                        String md5String = MySecurityUtil.string2MD5(string);;
                        mySecurityAddtoLoveBean.setUserId(loginName);
                        mySecurityAddtoLoveBean.setSign(md5String);
                        mySecurityAddtoLoveBean.setType(list.get(position).get("type").toString());
                        mySecurityAddtoLoveBean.setTypeId(list.get(position).get("id").toString());
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
                                    list.get(position).put("isCollect", "false");
                                }
                            }
                        });
                    } else {
                        if(list.get(position).get("isCollect").equals("true")){
                            Log.e("AAAAAAAA", "已经收藏过，切勿重复收藏");
                        }else {
                            holder.btn_addto_love.setSelected(true);
                            //添加收藏
                            //加密数据
                            String string = "saveFavorite@"+loginName+"@" + list.get(position).get("type") + "@" + list.get(position).get("id").toString().trim();
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityAddtoLoveBean.setUserId(loginName);
                            mySecurityAddtoLoveBean.setSign(md5String);
                            mySecurityAddtoLoveBean.setType(list.get(position).get("type").toString());
                            mySecurityAddtoLoveBean.setTypeId(list.get(position).get("id").toString());
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
                                        list.get(position).put("isCollect", "true");
                                        Log.e("AAAAAAAA", "收藏成功");
                                    }
                                }
                            });
                        }
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            //餐厅或者食物照片
            ImageView iv_restaurant;
            // 餐厅或者食物名字
            TextView tv_restaurant;
            // 食物价格
            TextView tv_Foodprices;
            TextView tv_Foodprices1;
            //收藏按钮
            Button btn_addto_love;

        }
    }
}
