package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import activity.FooddetailActivity;
import bean.CategoryFoodBean;
import bean.MySecurityAddtoLoveBean;
import bean.MySecurityCategoryBean;
import cn.gdin.hk.hungry.R;
import utils.HttpUtilsAddToLove;
import utils.HttpUtilsGetJson;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/7.
 */
public class Korea_fragment1 extends Fragment {
    private View v;
    private ListView lv1;
    private List<Map<String, Object>> list;
    private HttpUtilsGetJson httpUtilsGetJson = new HttpUtilsGetJson();
    private final static int FINISH = 0;
    private final static int UPDATA = 1;
    private int FINISHOk = 0;
    private int page = 1;
    private String url;
    private List<Map<String, Object>> list1;
    private List<Map<String, Object>> list2;
    private MyListViewAdapter adapter;
    private HttpUtilsAddToLove httpUtilsAddToLove = new HttpUtilsAddToLove();
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityCategoryBean mySecurityCategoryBean = new MySecurityCategoryBean();
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
                    CategoryFoodBean categoryFoodBean = gson.fromJson(json, CategoryFoodBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    list = new ArrayList<>();
                    for (int i = 0; i < categoryFoodBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", categoryFoodBean.datas.get(i).id);
                        map.put("isCollect", categoryFoodBean.datas.get(i).isCollect);
                        map.put("name", categoryFoodBean.datas.get(i).name);
                        map.put("price", categoryFoodBean.datas.get(i).price);
                        map.put("url", categoryFoodBean.datas.get(i).url);
                        list.add(map);
                    }
                    adapter = new MyListViewAdapter(list);
                    lv1.setAdapter(adapter);
                    break;
                case UPDATA:
                    //解密数据
                    String base64ToString1 = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json1 = MySecurityUtil.decrypt(base64ToString1);
                    // 使用gson映射得到bean类
                    CategoryFoodBean categoryFoodBean1 = gson.fromJson(json1, CategoryFoodBean.class);
                    if (categoryFoodBean1.datas.size() == 0) {
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        // 将bean类里面的东西装载到list中便于使用
                        list2 = new ArrayList<>();
                        for (int i = 0; i < categoryFoodBean1.datas.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", categoryFoodBean1.datas.get(i).id);
                            map.put("isCollect", categoryFoodBean1.datas.get(i).isCollect);
                            map.put("name", categoryFoodBean1.datas.get(i).name);
                            map.put("price", categoryFoodBean1.datas.get(i).price);
                            map.put("url", categoryFoodBean1.datas.get(i).url);
                            list2.add(map);
                        }
                        list.addAll(list2);
                        adapter.notifyDataSetChanged();
                    }
                    FINISHOk = 0;
                    break;
            }
        }

        ;
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.koreafood_fragment1, null);
        lv1 = (ListView) v.findViewById(R.id.koreafragment1_lv);
        //获取登录帐号
        SharedPreferences sp=getActivity().getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");
        //获取数据
        GetData();
        //监听listview滑动到底部实现分页加载
        lv1.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                            String string = "getHeatFoodById@"+loginName + "@30" + "@" + page + "@" + 5;
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityCategoryBean.setUserId(loginName);
                            mySecurityCategoryBean.setSign(md5String);
                            mySecurityCategoryBean.setPageSize("5");
                            mySecurityCategoryBean.setPageNow(page + "");
                            mySecurityCategoryBean.setId(30);
                            String json = gson.toJson(mySecurityCategoryBean);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            url = "http://202.171.212.154:8080/hh/getHeatFoodById.action?json=" + encryptJsonToBase64;
                            Log.e("AAAAAA", url);
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

        //listviewd点击事件
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FooddetailActivity.class);
                intent.putExtra("ifShow", "show_gotoshop");
                intent.putExtra("id", list.get(position).get("id").toString());
                intent.putExtra("isCollect", list.get(position).get("isCollect").toString());
                startActivity(intent);
            }
        });

        return v;
    }

    private void GetData() {

        //加密数据
        String string = "getHeatFoodById@"+loginName+ "@30" + "@" + page + "@" + 5;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityCategoryBean.setUserId(loginName);
        mySecurityCategoryBean.setSign(md5String);
        mySecurityCategoryBean.setPageSize("5");
        mySecurityCategoryBean.setPageNow(page + "");
        mySecurityCategoryBean.setId(30);
        String json = gson.toJson(mySecurityCategoryBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        url = "http://202.171.212.154:8080/hh/getHeatFoodById.action?json=" + encryptJsonToBase64;
        Log.e("AAAAAA", url);
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

    //定义一个新的listview适配器
    class MyListViewAdapter extends BaseAdapter {
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_home_listview, null);
                holder.iv_restaurant = (ImageView) convertView.findViewById(R.id.iv_restaurant);
                holder.tv_restaurant = (TextView) convertView.findViewById(R.id.tv_restaurant_name);
                holder.tv_Foodprices = (TextView) convertView.findViewById(R.id.tv_food_prices);
                holder.btn_addto_love = (Button) convertView.findViewById(R.id.btn_restaurant_love);
                holder.tv_Foodprices1 = (TextView) convertView.findViewById(R.id.tv_food_prices1);
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
            //设置价格
            holder.tv_Foodprices1.setVisibility(View.VISIBLE);
            holder.tv_Foodprices.setVisibility(View.VISIBLE);
            holder.tv_Foodprices.setText((String) list.get(position).get("price"));
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
                        String string = "deleteFavorite@"+loginName+"@2"+"@"+list.get(position).get("id").toString().trim();
                        String md5String = MySecurityUtil.string2MD5(string);;
                        mySecurityAddtoLoveBean.setUserId(loginName);
                        mySecurityAddtoLoveBean.setSign(md5String);
                        mySecurityAddtoLoveBean.setType("2");
                        mySecurityAddtoLoveBean.setTypeId(list.get(position).get("id").toString());
                        String json = gson.toJson(mySecurityAddtoLoveBean);
                        //encrypt加密
                        String encryptJson = MySecurityUtil.encrypt(json);
                        //加密后在String2Base64
                        String  encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
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
                        }else{
                            holder.btn_addto_love.setSelected(true);
                            //添加收藏
                            //加密数据
                            String string = "saveFavorite@"+loginName+"@2"+"@"+list.get(position).get("id").toString().trim();
                            String md5String = MySecurityUtil.string2MD5(string);;
                            mySecurityAddtoLoveBean.setUserId(loginName);
                            mySecurityAddtoLoveBean.setSign(md5String);
                            mySecurityAddtoLoveBean.setType("2");
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
