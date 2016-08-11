package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.FooddetailActivity;
import activity.OrderListActivity;
import adapter.OnRecyclerViewItemClickListener;
import bean.MySecurityAddtoLoveBean;
import bean.MySecurityShopMenuBean;
import bean.ShopmenuBean;
import cn.gdin.hk.hungry.R;
import utils.MySecurityUtil;


/**
 * Created by cxk on 2016/4/9.
 */
public class Shopmenu_fragment extends Fragment implements View.OnClickListener {
    private View v;
    private RecyclerView recyclerView_menu;
    private ListView listview;
    private List<Map<String, Object>> listCategory=new ArrayList<>();
    private List<Map<String, Object>> listAll;
    private List<Map<String, Object>> list_sure_orderlist;
    private MyRightAdapter adapter;
    private int singleFood_mount;
    private double TotalCost;
    private double singleFood_prices;
    private TextView tv_totalPrices;
    private LinearLayoutManager mLayoutManager;
    private Button btn_finishOrder;
    private List<Map<String, Object>> special_order_list;
    private String path;
    private final static int FINISH = 0;
    private ShopmenuBean shopmenuBean;
    private DisplayImageOptions options;
    private SimpleAdapter simpleAdapter;
    private String id;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityShopMenuBean mySecurityShopMenuBean=new MySecurityShopMenuBean();
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
                    shopmenuBean = gson.fromJson(json, ShopmenuBean.class);
                    //加载左边的分类list,将获取到的分类信息都放置到listcategory里面
                    listCategory = new ArrayList<>();
                    for (int i = 0; i < shopmenuBean.datas.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("category", shopmenuBean.datas.get(i).type);
                        listCategory.add(map);
                    }
                    simpleAdapter = new SimpleAdapter(getActivity(), listCategory, R.layout.custom_cycleview_left,
                            new String[]{"category"}, new int[]{R.id.tv_category});
                    listview.setAdapter(simpleAdapter);
                    //加载右边的recycleview，将所有的菜统一装载到listall里面
                    listAll = new ArrayList<>();
                    list_sure_orderlist = new ArrayList<>();
                    for (int i = 0; i < shopmenuBean.datas.size(); i++) {
                        for (int j = 0; j < shopmenuBean.datas.get(i).list.size(); j++) {
                            Map map = new HashMap();
                            map.put("ID", shopmenuBean.datas.get(i).list.get(j).id);
                            map.put("isCollect", shopmenuBean.datas.get(i).list.get(j).isCollect);
                            map.put("food_name", shopmenuBean.datas.get(i).list.get(j).name);
                            map.put("single_money", shopmenuBean.datas.get(i).list.get(j).price);
                            map.put("count", 0);
                            map.put("sure_orderlist", list_sure_orderlist);
                            map.put("url", shopmenuBean.datas.get(i).list.get(j).url);
                            special_order_list = new ArrayList<>();
                            if (shopmenuBean.datas.get(i).list.get(j).special.size() != 0) {

                                for (int z = 0; z < shopmenuBean.datas.get(i).list.get(j).special.size(); z++) {
                                    Map map0 = new HashMap();
                                    map0.put("special", shopmenuBean.datas.get(i).list.get(j).special.get(z));
                                    special_order_list.add(map0);
                                }

                            }
                            map.put("special_order", special_order_list);
                            listAll.add(map);
                        }
                    }

                    recyclerView_menu.setAdapter(adapter);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView_menu.setLayoutManager(mLayoutManager);
                    StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter); //绑定之前的adapter
                    recyclerView_menu.addItemDecoration(headersDecor);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.shopmenu_fragment, null);
        //初始化各个控件ID
        initIds();
        //网路获取商家菜式列表
        getData();
        //为左边的listview设置监听事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int scrollPosition = 0;
                for (int i = 0; i < position; i++) {
                    scrollPosition += shopmenuBean.datas.get(i).list.size();

                }
                scrollPosition = scrollPosition + 2;
                recyclerView_menu.smoothScrollToPosition(scrollPosition);
            }
        });

        //为右边的recycleview设置监听事件
        adapter = new MyRightAdapter();
        adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), FooddetailActivity.class);
                intent.putExtra("ifShow", "show_finishording");
                intent.putExtra("totalPrices", tv_totalPrices.getText().toString());
                intent.putExtra("id", listAll.get(position).get("ID").toString());
//                Log.e("AAAAAA", position + "nihao ");
                intent.putExtra("isCollect",listAll.get(position).get("isCollect").toString());
                startActivity(intent);
            }
        });

        //        recyclerView_menu.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                int firstCompletelyVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
//                listview.setItemChecked((int) adapter.getHeaderId(firstCompletelyVisibleItemPosition), true);
//
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        //定义一个加载图片的option，用于加载张图片
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loadingpic)
                .showImageOnFail(R.mipmap.loadingpic ).cacheInMemory(true).cacheOnDisk(true).build();

        return v;
    }


    private void getData() {
        //获取登录帐号
        SharedPreferences sp=getActivity().getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");
        id=getArguments().getString("id").toString();
        //加密数据
        String string = "getFoodList@"+id+"@"+loginName;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityShopMenuBean.setBusinessId(id);
        mySecurityShopMenuBean.setUserId(loginName);
        mySecurityShopMenuBean.setSign(md5String);
        String json = gson.toJson(mySecurityShopMenuBean);
        //encrypt加密
        String encryptJson = MySecurityUtil.encrypt(json);
        //加密后在String2Base64
        String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
        path = "http://202.171.212.154:8080/hh/getFoodList.action?json=" + encryptJsonToBase64;
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


    private void initIds() {
        recyclerView_menu = (RecyclerView) v.findViewById(R.id.recyclerView_menu);
        listview = (ListView) v.findViewById(R.id.listview);
        tv_totalPrices = (TextView) v.findViewById(R.id.tv_totalprices);
        btn_finishOrder = (Button) v.findViewById(R.id.btn_finishOrde);
        btn_finishOrder.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finishOrde:
                Intent intent = new Intent(getActivity(), OrderListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (Serializable) listAll);
                bundle.putString("totalPrices", tv_totalPrices.getText().toString());
                bundle.putString("id",id);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }


    //新建一个右边recycleview适配器
    public class MyRightAdapter extends RecyclerView.Adapter<MyRightAdapter.MyViewHolder> implements View.OnClickListener, StickyRecyclerHeadersAdapter<MyRightAdapter.MyHeadViewHolder> {
        private OnRecyclerViewItemClickListener mOnItemClickListener = null;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cycleview_right, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            view.setOnClickListener(this);
            return holder;
        }


        public void setOnItemClickListener(OnRecyclerViewItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }


        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            //设置各项参数
            holder.tv_prices.setText(listAll.get(position).get("single_money") + "");
            holder.tv_mount.setText(listAll.get(position).get("count") + "");
            holder.tv_foodname.setText(listAll.get(position).get("food_name") + "");
            //判断是否加入了收藏
            if(listAll.get(position).get("isCollect").toString().equals("true")){
                holder.btn_love.setSelected(true);
            }else{
                holder.btn_love.setSelected(false);
            }
            //为收藏按钮设置点击事件
            holder.btn_love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.btn_love.isSelected()){
                        holder.btn_love.setSelected(false);
                        //取消收藏
                        //加密数据
                        String string = "deleteFavorite@"+loginName+"@2"+"@"+listAll.get(position).get("ID").toString().trim();
                        String md5String = MySecurityUtil.string2MD5(string);;
                        mySecurityAddtoLoveBean.setUserId(loginName);
                        mySecurityAddtoLoveBean.setSign(md5String);
                        mySecurityAddtoLoveBean.setType("2");
                        mySecurityAddtoLoveBean.setTypeId(listAll.get(position).get("ID").toString());
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
                                    Log.e("AAAAAAAA", "取消收藏成功");
                                    for (int i = 0; i < listAll.size(); i++) {
                                        if (listAll.get(i).get("ID").toString().trim().equals(listAll.get(position).get("ID").toString().trim())) {
                                            listAll.get(i).put("isCollect", "false");

                                        }
                                    }
                                }
                            }
                        });

                    } else {
                        if(listAll.get(position).get("isCollect").equals("true")){
                            Log.e("AAAAAAAA", "已经收藏过，切勿重复收藏");
                        }else{
                            holder.btn_love.setSelected(true);
                            //添加收藏
                            //加密数据
                            String string = "saveFavorite@"+loginName+"@2"+"@"+listAll.get(position).get("ID").toString().trim();
                            String md5String = MySecurityUtil.string2MD5(string);;
                            mySecurityAddtoLoveBean.setUserId(loginName);
                            mySecurityAddtoLoveBean.setSign(md5String);
                            mySecurityAddtoLoveBean.setType("2");
                            mySecurityAddtoLoveBean.setTypeId(listAll.get(position).get("ID").toString());
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
                                        for (int i = 0; i < listAll.size(); i++) {
                                            if (listAll.get(i).get("ID").toString().trim().equals(listAll.get(position).get("ID").toString().trim())) {
                                                listAll.get(i).put("isCollect", "true");

                                            }
                                        }
                                        Log.e("AAAAAAAA", "收藏成功");
                                    }
                                }
                            });
                        }
                    }
                }
            });
            //加载食物照片
            ImageLoader.getInstance().displayImage((String) listAll.get(position).get("url"), holder.iv,
                    options);
            holder.itemView.setTag(position);
            //增加按钮
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleFood_mount = Integer.parseInt(holder.tv_mount.getText().toString());
                    TotalCost = Double.valueOf(tv_totalPrices.getText().toString());
                    singleFood_prices = Double.valueOf(holder.tv_prices.getText().toString());
                    holder.tv_mount.setText(singleFood_mount + 1 + "");
                    tv_totalPrices.setText(TotalCost + singleFood_prices + "");
                    //每点击一次增加按钮，动态改变当前list集合中该菜式的选择数量
                    for (int i = 0; i < listAll.size(); i++) {
                        if (listAll.get(i).get("ID").toString().trim().equals(listAll.get(position).get("ID").toString().trim())) {
                            listAll.get(i).put("count", holder.tv_mount.getText().toString());

                        }
                    }

                }
            });

            //减少按钮
            holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleFood_mount = Integer.parseInt(holder.tv_mount.getText().toString());
                    TotalCost = Double.valueOf(tv_totalPrices.getText().toString());
                    singleFood_prices = Double.valueOf(holder.tv_prices.getText().toString());
                    if (singleFood_mount >= 1) {
                        holder.tv_mount.setText(singleFood_mount - 1 + "");
                        tv_totalPrices.setText(TotalCost - singleFood_prices + "");
                        //没点击一次减少按钮，动态改变list集合中该菜式的选择数量
                        for (int i = 0; i < listAll.size(); i++) {
                            if (listAll.get(i).get("ID").toString().trim().equals(listAll.get(position).get("ID").toString().trim())) {
                                listAll.get(i).put("count", holder.tv_mount.getText().toString());

                            }
                        }
                    }
                }
            });

        }

        //获取headid
        @Override
        public long getHeaderId(int position) {

            return getSortType(position);
        }

        @Override
        public MyHeadViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycleview_headview, parent, false);
            MyHeadViewHolder holder = new MyHeadViewHolder(view);
            return holder;
        }

        @Override
        public void onBindHeaderViewHolder(MyHeadViewHolder holder, int position) {
            holder.tv_head.setText((String) listCategory.get(getSortType(position)).get("category"));

        }

        //2为该分类的list.size，此函数目的在于转换position
        public int getSortType(int position) {
            int sort = -1;
            int sum = 0;
            for (int i = 0; i < listCategory.size(); i++) {
                if (position >= sum) {
                    sort++;
                } else {
                    return sort;
                }
//                sum += listFood.get(i).size();
                sum += shopmenuBean.datas.get(i).list.size();
            }
            return sort;
        }


        //................................
        @Override
        public int getItemCount() {

            return listAll.size();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {             //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        }

        class MyHeadViewHolder extends RecyclerView.ViewHolder {
            TextView tv_head;

            public MyHeadViewHolder(View itemView) {
                super(itemView);
                tv_head = (TextView) itemView.findViewById(R.id.headview_tv);
            }
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            Button btn_love;
            Button btn_add;
            Button btn_decrease;
            TextView tv_prices;
            TextView tv_mount;
            TextView tv_foodname;


            public MyViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.iv_restaurant);
                btn_love = (Button) view.findViewById(R.id.btn_restaurant_love);
                btn_add = (Button) view.findViewById(R.id.btn_add);
                btn_decrease = (Button) view.findViewById(R.id.decrease);
                tv_prices = (TextView) view.findViewById(R.id.tv_food_prices);
                tv_mount = (TextView) view.findViewById(R.id.mount);
                tv_foodname = (TextView) view.findViewById(R.id.tv_food_name);
            }
        }
    }
}
