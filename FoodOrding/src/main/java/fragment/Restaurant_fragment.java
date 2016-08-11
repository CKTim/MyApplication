package fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AbsListView.OnScrollListener;

import activity.ShopActivity;
import bean.MySecurityAddtoLoveBean;
import bean.MySecurityNeabyAllListBean;
import bean.NearbyAllListBean;
import cn.gdin.hk.hungry.R;
import utils.HttpUtilsAddToLove;
import utils.HttpUtilsGetJson;
import utils.MySecurityUtil;

public class Restaurant_fragment extends Fragment {
    private View view;
    private ListView lv;
    private ImageLoader loader;
    private DisplayImageOptions options;
    private MyListViewAdapter adapter;
    private List<Map<String, Object>> list;
    private List<Map<String, Object>> list2;
    private HttpUtilsGetJson httpUtilsGetJson;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption;
    private HttpUtilsAddToLove httpUtilsAddToLove = new HttpUtilsAddToLove();
    private double latidue;
    private double longitude;
    private String path;
    private int page = 1;
    private int FINISHOk = 0;
    private final static int FINISH = 3;
    private final static int UPDATA = 4;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityNeabyAllListBean mySecurityNeabyAllListBean = new MySecurityNeabyAllListBean();
    private MySecurityAddtoLoveBean mySecurityAddtoLoveBean=new MySecurityAddtoLoveBean();
    private String loginName;
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    // 使用gson映射得到bean类
                    NearbyAllListBean nearbyAllListBean = gson.fromJson(json, NearbyAllListBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    list2 = new ArrayList<>();
                    // 将bean类里的东西装载到list中方便使用
                    for (int i = 0; i < nearbyAllListBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("ShopName", nearbyAllListBean.datas.get(i).name);
                        map.put("id", nearbyAllListBean.datas.get(i).id);
                        map.put("ShopPic", nearbyAllListBean.datas.get(i).url);
                        map.put("isCollect", nearbyAllListBean.datas.get(i).isCollect);
                        map.put("latidue", nearbyAllListBean.datas.get(i).latidue);
                        map.put("longitude", nearbyAllListBean.datas.get(i).longitude);
                        list2.add(map);
                    }
                    adapter = new MyListViewAdapter();
                    lv.setAdapter(adapter);
                    break;
                case UPDATA:
                    //解密数据
                    String base64ToString1 = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json1 = MySecurityUtil.decrypt(base64ToString1);
                    // 使用gson映射得到bean类
                    NearbyAllListBean nearbyAllListBean1 = gson.fromJson(json1, NearbyAllListBean.class);
                    if (nearbyAllListBean1.datas.size() == 0) {
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        // 将bean类里面的东西装载到list中便于使用
                        list = new ArrayList<>();
                        // 将bean类里的东西装载到list中方便使用
                        for (int i = 0; i < nearbyAllListBean1.datas.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("ShopName", nearbyAllListBean1.datas.get(i).name);
                            map.put("id", nearbyAllListBean1.datas.get(i).id);
                            map.put("ShopPic", nearbyAllListBean1.datas.get(i).url);
                            map.put("isCollect", nearbyAllListBean1.datas.get(i).isCollect);
                            map.put("latidue", nearbyAllListBean1.datas.get(i).latidue);
                            map.put("longitude", nearbyAllListBean1.datas.get(i).longitude);
                            list.add(map);
                        }
                        list2.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                    FINISHOk = 0;
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = LayoutInflater.from(getActivity()).inflate(R.layout.firststep_restaurant_fragment, null);
        //初始化各个控件ID
        initIDs();
        //定位到当前位置并且搜索附近餐厅
        location_to_search();
        //Ϊlistview的监听，实现动态加载数据
        lv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //监听是否滑动到了最后
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //防止多次加载
                        if (FINISHOk == 0) {
                            page = page + 1;
                            FINISHOk = 1;
                            //加密数据
                            String string = "getAroundBusiness@"+loginName+"@5000@"+page+"@5";
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityNeabyAllListBean = new MySecurityNeabyAllListBean();
                            mySecurityNeabyAllListBean.setPageNow(page + "");
                            mySecurityNeabyAllListBean.setPageSize("5");
                            mySecurityNeabyAllListBean.setUserId(loginName);
                            mySecurityNeabyAllListBean.setRaidus(5000);
                            mySecurityNeabyAllListBean.setLat(latidue);
                            mySecurityNeabyAllListBean.setLon(longitude);
                            mySecurityNeabyAllListBean.setSign(md5String);
                            String json = gson.toJson(mySecurityNeabyAllListBean);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            path = "http://202.171.212.154:8080/hh/getAroundBusiness.action?json=" + encryptJsonToBase64;
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

        //设置ImageLoader的option
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic).showImageOnFail(R.mipmap.loadingfailed).cacheInMemory(true).cacheOnDisk(true).build();
        loader = ImageLoader.getInstance();
        //设置listview监听事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                intent.putExtra("ifShow", "show_gotoshop");
                intent.putExtra("id", list2.get(position).get("id").toString());
                startActivity(intent);
            }
        });


        return view;
    }


    private void location_to_search() {
        //开始定位
        mLocationClient = new AMapLocationClient(getActivity());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(new AMapLocationListener() {

            @SuppressWarnings("static-access")
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                Log.i("cxk", amapLocation + "'");
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        latidue = amapLocation.getLatitude();//获取当前位置的纬度
                        longitude = amapLocation.getLongitude();//获取当前位置的经度
                        //加密数据
                        String string = "getAroundBusiness@"+loginName+"@5000@1@5";
                        String md5String = MySecurityUtil.string2MD5(string);
                        mySecurityNeabyAllListBean.setPageNow("1");
                        mySecurityNeabyAllListBean.setPageSize("5");
                        mySecurityNeabyAllListBean.setUserId(loginName);
                        mySecurityNeabyAllListBean.setRaidus(5000);
                        mySecurityNeabyAllListBean.setLat(latidue);
                        mySecurityNeabyAllListBean.setLon(longitude);
                        mySecurityNeabyAllListBean.setSign(md5String);
                        String json = gson.toJson(mySecurityNeabyAllListBean);
                        //encrypt加密
                        String encryptJson = MySecurityUtil.encrypt(json);
                        //加密后在String2Base64
                        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                        path = "http://202.171.212.154:8080/hh/getAroundBusiness.action?json=" + encryptJsonToBase64;
                        Log.e("cxk", path);
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
                }
            }
        });
    }

    private void initIDs() {
        // TODO Auto-generated method stub
        lv = (ListView) view.findViewById(R.id.restaurant_lv);
        //获取登录帐号
        SharedPreferences sp=getActivity().getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");
    }


    //定义一个listview适配器
    public class MyListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list2.get(position);
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
                holder.tv_food_prices1 = (TextView) convertView.findViewById(R.id.tv_food_prices1);
                holder.btn_addtolove = (Button) convertView.findViewById(R.id.btn_restaurant_love);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //把价格隐藏掉
            holder.tv_Foodprices.setVisibility(View.GONE);
            holder.tv_food_prices1.setVisibility(View.GONE);

            //加载图片
            loader.displayImage((String) list2.get(position).get("ShopPic"), holder.iv_restaurant,
                    options);
            holder.tv_restaurant.setText((String) list2.get(position).get("ShopName"));

            //判断有没有加入收藏
            if (list2.get(position).get("isCollect").toString().equals("true")) {
                holder.btn_addtolove.setSelected(true);
            } else {
                holder.btn_addtolove.setSelected(false);
            }
            //收藏按钮的监听
            holder.btn_addtolove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.btn_addtolove.isSelected()) {
                        holder.btn_addtolove.setSelected(false);
                        //取消收藏
                        //加密数据
                        String string = "deleteFavorite@"+loginName+"@1"+"@"+list2.get(position).get("id").toString().trim();
                        String md5String = MySecurityUtil.string2MD5(string);;
                        mySecurityAddtoLoveBean.setUserId(loginName);
                        mySecurityAddtoLoveBean.setSign(md5String);
                        mySecurityAddtoLoveBean.setType("1");
                        mySecurityAddtoLoveBean.setTypeId(list2.get(position).get("id").toString());
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
                                    Log.e("AAAAAAAA", "取消收藏成功");
                                    list2.get(position).put("isCollect", "false");
                                }
                            }
                        });
                    } else {
                        if(list2.get(position).get("isCollect").equals("true")){
                            Log.e("AAAAAAAA", "已经收藏过，切勿重复收藏");
                        }else {
                            holder.btn_addtolove.setSelected(true);
                            //添加收藏
                            //加密数据
                            String string = "saveFavorite@"+loginName+"@1"+ "@" + list2.get(position).get("id").toString().trim();
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityAddtoLoveBean.setUserId(loginName);
                            mySecurityAddtoLoveBean.setSign(md5String);
                            mySecurityAddtoLoveBean.setType("1");
                            mySecurityAddtoLoveBean.setTypeId(list2.get(position).get("id").toString());
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
                                        list2.get(position).put("isCollect", "true");
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
            //食物或者餐厅名称
            TextView tv_restaurant;
            //食物价格
            TextView tv_Foodprices;
            TextView tv_food_prices1;
            //收藏按钮
            Button btn_addtolove;

        }
    }

}
