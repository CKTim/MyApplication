package cn.gdin.hk.hungry;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.CategoryActivity;
import activity.FooddetailActivity;
import activity.MyLoveActivity;
import activity.ShopActivity;
import bean.HeatBean;
import bean.MySecurityAddtoLoveBean;
import bean.MySecurityHeatBean;
import utils.ManageActivityUtils;
import utils.MySecurityUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_openMyLove, btn_opendrawer, btn_restaurant, btn_cuisine, btn_district;
    private Button btn_restaurant1, btn_cuisine1, btn_district1;
    private DrawerLayout drawerlayout;
    private DisplayImageOptions options;
    private ListView lv;
    private ViewPager viewPager;
    private List<Map<String, Object>> list;
    private List<Map<String, Object>> list2;
    private List<Map<String, Object>> list3;
    private MyListViewAdapter adapter;
    private MyViewPagerAdapter viewPagerAdapter;
    private View headview1;
    private View headview2;
    private View fixed_view;
    private List<View> views;
    private String[] Urls = new String[]{"http://202.171.212.154/ad/ad1.jpg",
            "http://202.171.212.154/ad/ad2.jpg",
            "http://202.171.212.154/ad/ad3.jpg",};
    private String heatUrl;
    private ImageView[] points;
    private final static int FIRST = 0;
    private final static int AUTO = 1;
    private final static int FINISH = 3;
    private final static int UPDATA = 4;
    private int FINISHOk = 0;
    private int page = 1;
    private MySecurityHeatBean mySecurityHeatBean = new MySecurityHeatBean();
    private MySecurityAddtoLoveBean mySecurityAddtoLoveBean = new MySecurityAddtoLoveBean();
    private Gson gson;
    private String encryptJsonToBase64;
    private OkHttpClient client = new OkHttpClient();
    private String loginName;
    private long firstTime=0;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FIRST:
                    viewPager.setCurrentItem(0);
                    mhandler.sendEmptyMessageDelayed(AUTO, 5 * 1000);
                    break;

                case AUTO:
                    int index = viewPager.getCurrentItem();
                    if (index == Urls.length - 1) {
                        viewPager.setCurrentItem(0);
                        mhandler.sendEmptyMessageDelayed(AUTO, 5 * 1000);
                    } else {
                        viewPager.setCurrentItem(index + 1);
                        mhandler.sendEmptyMessageDelayed(AUTO, 5 * 1000);
                    }
                    break;

                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    // 使用gson映射得到bean类
                    HeatBean heatBean = gson.fromJson(json, HeatBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    // 初始化list2
                    list2 = new ArrayList<>();
                    for (int i = 0; i < heatBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", heatBean.datas.get(i).id);
                        map.put("isCollect", heatBean.datas.get(i).isCollect);
                        map.put("name", heatBean.datas.get(i).name);
                        //判断有没有价格
                        if (heatBean.datas.get(i).price != null) {
                            map.put("price", heatBean.datas.get(i).price);
                        }
                        map.put("type", heatBean.datas.get(i).type);
                        map.put("url", heatBean.datas.get(i).url);
                        list2.add(map);
                    }
                    adapter = new MyListViewAdapter(list2);
                    lv.setAdapter(adapter);
                    break;
                case UPDATA:
                    //解密数据
                    String base64ToString1 = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json1 = MySecurityUtil.decrypt(base64ToString1);
                    // 使用gson映射得到解密好的数据
                    HeatBean heatBean1 = gson.fromJson(json1, HeatBean.class);
                    // 将bean类里面的东西装载到list中便于使用
                    // 初始化list3
                    if (heatBean1.datas.size() == 0) {
                        Toast.makeText(MainActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        list3 = new ArrayList<>();
                        for (int i = 0; i < heatBean1.datas.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", heatBean1.datas.get(i).id);
                            map.put("isCollect", heatBean1.datas.get(i).isCollect);
                            map.put("name", heatBean1.datas.get(i).name);
                            //判断有没有价格
                            if (heatBean1.datas.get(i).price != null) {
                                map.put("price", heatBean1.datas.get(i).price);
                            }
                            map.put("type", heatBean1.datas.get(i).type);
                            map.put("url", heatBean1.datas.get(i).url);
                            list3.add(map);
                        }
                        list2.addAll(list3);
                        adapter.notifyDataSetChanged();
                    }
                    FINISHOk = 0;

                    break;

            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        // 初始化Listview的头部
        headview1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.listview_headview1, lv, false);
        headview2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.listview_headview2, lv, false);
        // 初始化控件ID
        initIDs();
        // 初始化ViewPager
        initViewPager();
        //初始化Listview
        initListView();
    }


    private void initListView() {
        // 为listview添加头部
        lv.addHeaderView(headview1, null, false);
        lv.addHeaderView(headview2, null, false);
        //先通过URL获取热门bean;
        list = new ArrayList<>();
        gson = new Gson();
        //加密数据
        String string = "getAllHeat@" + loginName + "@" + page + "@5";
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityHeatBean.setPageNow("1");
        mySecurityHeatBean.setPageSize("5");
        mySecurityHeatBean.setUserId(loginName);
        mySecurityHeatBean.setSign(md5String);
        String json = gson.toJson(mySecurityHeatBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        heatUrl = "http://202.171.212.154:8080/hh/getAllHeat.action?json=" + encryptJsonToBase64;
        //开始访问得到返回数据
        Request request = new Request.Builder()
                .url(heatUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("aaaaaaa", "onFailure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //NOT UI Thread
                if (response.isSuccessful()) {
                    //将所得到的list传回回去
                    Message message = Message.obtain();
                    message.obj = response.body().string();
                    message.what = FINISH;
                    mhandler.sendMessage(message);
                }
            }
        });

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
                            String string = "getAllHeat@" + loginName + "@" + page + "@5";
                            String md5String = MySecurityUtil.string2MD5(string);
                            mySecurityHeatBean = new MySecurityHeatBean();
                            mySecurityHeatBean.setPageNow(page + "");
                            mySecurityHeatBean.setPageSize("5");
                            mySecurityHeatBean.setUserId(loginName);
                            mySecurityHeatBean.setSign(md5String);
                            String json = gson.toJson(mySecurityHeatBean);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            heatUrl = "http://202.171.212.154:8080/hh/getAllHeat.action?json=" + encryptJsonToBase64;
                            //开始访问得到返回数据
                            Request request = new Request.Builder()
                                    .url(heatUrl)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    Log.e("aaaaaaa", "onFailure");
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    //NOT UI Thread
                                    if (response.isSuccessful()) {
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
                // 实现美团悬浮效果
                if (firstVisibleItem >= 1) {
                    headview2.setVisibility(View.GONE);
                    fixed_view.setVisibility(View.VISIBLE);
                } else {
                    headview2.setVisibility(View.VISIBLE);
                    fixed_view.setVisibility(View.GONE);
                }

            }
        });

        //listview的点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //先判断点击进去的是菜还是餐厅
                if (list2.get(position - 2).get("type").equals("1")) {
                    Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                    intent.putExtra("id", list2.get(position - 2).get("id").toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, FooddetailActivity.class);
                    intent.putExtra("ifShow", "show_gotoshop");
                    intent.putExtra("id", list2.get(position - 2).get("id").toString());
                    intent.putExtra("isCollect", list2.get(position - 2).get("isCollect").toString());
                    startActivity(intent);
                }
            }
        });
    }


    private void initViewPager() {
        // 将图片一张一张的加载进去
        views = new ArrayList<View>();
        for (int i = 0; i < Urls.length; i++) {
            ImageView imageview = new ImageView(MainActivity.this);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
//            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loading_url2)
//                    .showImageOnFail(R.mipmap.loading_url2_failed).cacheInMemory(true).build();
            ImageLoader.getInstance().displayImage(Urls[i], imageview, options);
            views.add(imageview);
        }

        // 动态创建小圆点
        points = new ImageView[Urls.length];
        LinearLayout liLayout = (LinearLayout) headview1.findViewById(R.id.firststep_linearlayout_point);
        //用于将dp转化为px
        float scale = this.getResources().getDisplayMetrics().density;

        for (int i = 0; i < Urls.length; i++) {
            // 创建小圆点imageview并加入到布局中
            ImageView imageview = new ImageView(MainActivity.this);
            imageview.setBackgroundResource(R.drawable.firststep_viewpagrpoints_selector);
            liLayout.addView(imageview);
            // 动态设置小圆点imageview的宽高
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageview.getLayoutParams();
            params.height = (int) (5 * scale + 0.5f);
            params.width = (int) (5 * scale + 0.5f);
            params.leftMargin = (int) (7 * scale + 0.5f);
            imageview.setLayoutParams(params);
            points[i] = imageview;
            if (i == 0) {
                points[i].setEnabled(true);
            } else {
                points[i].setEnabled(false);
            }
        }
        viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        // 发送一个空消息让它是开始自动播放
        mhandler.sendEmptyMessageAtTime(FIRST, 5 * 1000);
        // 为viewpager设置监听事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                // 将所选页面的小圆点设置为true，其他为false
                for (int i = 0; i < Urls.length; i++) {
                    if (arg0 == i) {
                        points[i].setEnabled(true);
                    } else {
                        points[i].setEnabled(false);
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    private void initIDs() {
        lv = (ListView) this.findViewById(R.id.firststep_lv);
        fixed_view = (LinearLayout) this.findViewById(R.id.fixed_to_top);
        viewPager = (ViewPager) headview1.findViewById(R.id.firststep_viewpage);
        btn_opendrawer = (Button) this.findViewById(R.id.btn_opendrawer);
        btn_openMyLove = (Button) this.findViewById(R.id.btn_openmylove);
        drawerlayout = (DrawerLayout) this.findViewById(R.id.drawerlayout);
        btn_restaurant = (Button) headview2.findViewById(R.id.btn_restaurant);
        btn_cuisine = (Button) headview2.findViewById(R.id.btn_cuisine);
        btn_district = (Button) headview2.findViewById(R.id.btn_district);
        btn_restaurant1 = (Button) this.findViewById(R.id.btn_restaurant1);
        btn_cuisine1 = (Button) this.findViewById(R.id.btn_cuisine1);
        btn_district1 = (Button) this.findViewById(R.id.btn_district1);
        btn_opendrawer.setOnClickListener(this);
        btn_openMyLove.setOnClickListener(this);
        btn_restaurant.setOnClickListener(this);
        btn_cuisine.setOnClickListener(this);
        btn_district.setOnClickListener(this);
        btn_restaurant1.setOnClickListener(this);
        btn_cuisine1.setOnClickListener(this);
        btn_district1.setOnClickListener(this);
        //获取登录帐号
        SharedPreferences sp = MainActivity.this.getSharedPreferences("userMessage", MODE_PRIVATE);
        loginName = sp.getString("loginName", "");
        //先定义一个加载图片的option
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic)
                .showImageOnFail(R.mipmap.loadingfailed).cacheInMemory(true).cacheOnDisk(true).displayer(new SimpleBitmapDisplayer()).build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_opendrawer:
                drawerlayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.btn_openmylove:
                Intent intent = new Intent(this, MyLoveActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cuisine:
                Intent intent0 = new Intent(this, CategoryActivity.class);
                intent0.putExtra("buttonID", "a");
                startActivity(intent0);
                break;
            case R.id.btn_restaurant:
                Intent intent1 = new Intent(this, CategoryActivity.class);
                intent1.putExtra("buttonID", "b");
                startActivity(intent1);
                break;
            case R.id.btn_district:
                Intent intent2 = new Intent(this, CategoryActivity.class);
                intent2.putExtra("buttonID", "c");
                startActivity(intent2);
                break;
            case R.id.btn_restaurant1:
                Intent intent3 = new Intent(this, CategoryActivity.class);
                intent3.putExtra("buttonID", "e");
                startActivity(intent3);
                break;
            case R.id.btn_cuisine1:
                Intent intent4 = new Intent(this, CategoryActivity.class);
                intent4.putExtra("buttonID", "d");
                startActivity(intent4);
                break;
            case R.id.btn_district1:
                Intent intent5 = new Intent(this, CategoryActivity.class);
                intent5.putExtra("buttonID", "f");
                startActivity(intent5);
                break;
        }
    }


    // 定义一个新的viewpager适配器
    class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub/
            ((ViewPager) container).addView(views.get(position));
            views.get(position).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                }
            });
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView(views.get(position));
        }
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
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_home_listview, null);
                holder.iv_restaurant = (ImageView) convertView.findViewById(R.id.iv_restaurant);
                holder.tv_restaurant = (TextView) convertView.findViewById(R.id.tv_restaurant_name);
                holder.tv_Foodprices = (TextView) convertView.findViewById(R.id.tv_food_prices);
                holder.btn_addto_love = (Button) convertView.findViewById(R.id.btn_restaurant_love);
                holder.tv_Foodprices1 = (TextView) convertView.findViewById(R.id.tv_food_prices1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
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
                        String string = "deleteFavorite@" + loginName + "@" + list.get(position).get("type") + "@" + list.get(position).get("id").toString().trim();
                        String md5String = MySecurityUtil.string2MD5(string);
                        mySecurityAddtoLoveBean.setUserId(loginName);
                        mySecurityAddtoLoveBean.setSign(md5String);
                        mySecurityAddtoLoveBean.setType(list.get(position).get("type").toString());
                        mySecurityAddtoLoveBean.setTypeId(list.get(position).get("id").toString());
                        String json = gson.toJson(mySecurityAddtoLoveBean);
                        //encrypt加密
                        String encryptJson = MySecurityUtil.encrypt(json);
                        //加密后在String2Base64
                        encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                        heatUrl = "http://202.171.212.154:8080/hh/deleteFavorite.action?json=" + encryptJsonToBase64;
                        //开始访问得到返回数据
                        Request request = new Request.Builder()
                                .url(heatUrl)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
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
                        if (list.get(position).get("isCollect").equals("true")) {
                            Log.e("AAAAAAAA", "已经收藏过，切勿重复收藏");
                        } else {
                            holder.btn_addto_love.setSelected(true);
                            //添加收藏
                            //加密数据
                            String string = "saveFavorite@" + loginName + "@" + list.get(position).get("type") + "@" + list.get(position).get("id").toString().trim();
                            String md5String = MySecurityUtil.string2MD5(string);
                            ;
                            mySecurityAddtoLoveBean.setUserId(loginName);
                            mySecurityAddtoLoveBean.setSign(md5String);
                            mySecurityAddtoLoveBean.setType(list.get(position).get("type").toString());
                            mySecurityAddtoLoveBean.setTypeId(list.get(position).get("id").toString());
                            String json = gson.toJson(mySecurityAddtoLoveBean);
                            //encrypt加密
                            String encryptJson = MySecurityUtil.encrypt(json);
                            //加密后在String2Base64
                            encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                            heatUrl = "http://202.171.212.154:8080/hh/saveFavorite.action?json=" + encryptJsonToBase64;
                            //开始访问得到返回数据
                            Request request = new Request.Builder()
                                    .url(heatUrl)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
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

    //实现点击两次返回键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            long secondTime=System.currentTimeMillis();
            if(secondTime-firstTime>2000){
                Toast.makeText(this,"請按一次退出程序",Toast.LENGTH_SHORT).show();
                firstTime=secondTime;
                return true;
            }else{
                ManageActivityUtils.finishAll();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
