package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cxk.myapplication.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.HeatBean;
import bean.MySecurityShopMessageBean;
import bean.NearbyAllListBean;
import bean.ShopMessageBean;
import bean.ShopmenuBean;
import fragment.Shopcomment_fragment;
import fragment.Shopdetail_fragment;
import fragment.Shopmenu_fragment;
import utils.HttpUtilsAddToLove;
import utils.HttpUtilsGetJson;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/9.
 */
public class ShopActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tablayout;
    private ViewPager viewpager;
    private List<Fragment> list;
    private FragmentManager manager;
    private FragmentTransaction transtion;
    private MyFragmentPageAdapter adapter;
    private List<String> titles;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView iv_shop;
    private Button btn_love;
    private String id;
    private String url;
    private String loginName;
    private static final int Finish = 0;
    private Shopmenu_fragment f1;
    private Shopcomment_fragment f2;
    private Shopdetail_fragment f3;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityShopMessageBean mySecurityShopMessageBean=new MySecurityShopMessageBean();
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Finish:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);

                    // 使用gson映射得到bean类
                    ShopMessageBean ShopmessageBean = gson.fromJson(json, ShopMessageBean.class);
                    //将id和经纬度传递到第三个fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("latidue", ShopmessageBean.latidue);
                    bundle.putString("longitude", ShopmessageBean.longitude);
                    bundle.putString("id", id);
                    f3.setArguments(bundle);
                    list.add(f3);
                    //使用manager和transtion提交事务后为viewpager设置一个适配器
                    manager = getSupportFragmentManager();
                    adapter = new MyFragmentPageAdapter(manager);
                    transtion = manager.beginTransaction();
                    transtion.commitAllowingStateLoss();
                    viewpager.setAdapter(adapter);

                    // tablayout.addTab可以将标题添加进Tab里面，true表示默认选中
                    tablayout.addTab(tablayout.newTab().setText(titles.get(0)), true);
                    tablayout.addTab(tablayout.newTab().setText(titles.get(1)), false);
                    tablayout.addTab(tablayout.newTab().setText(titles.get(2)), false);
                    //这两个方法是将Tablayout和Viewpager联合起来
                    tablayout.setupWithViewPager(viewpager);
                    tablayout.setTabsFromPagerAdapter(adapter);
                    //通过CollapsingToolbarLayout修改字体颜色
                    //设置还没收缩时状态下字体颜色,大小
                    mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CustomExpandedTitleTextAppearance);
                    //设置收缩后Toolbar上字体的颜色，大小
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CustomCollapsedTitleTextAppearance);
                    mCollapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.orange));
                    //加载商家的图片，是否收藏已经店名等
                    //先定义一个加载图片的option
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.loading_url2)
                            .showImageOnFail(R.mipmap.loading_url2_failed).cacheInMemory(true).build();
                    //加载餐厅照片
                    ImageLoader.getInstance().displayImage(ShopmessageBean.url, iv_shop,
                            options);
                    //设置标题，即店名
                    mCollapsingToolbarLayout.setTitle(ShopmessageBean.name);
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.attr.actionViewClass);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
        //初始化各个控件ID
        initIds();
        //将各个标题装在titles里面
        titles = new ArrayList<String>();
        titles.add("Menu");
        titles.add("Comment");
        titles.add("Message");
        //将三个Fragment装进集合中
        list = new ArrayList<Fragment>();
        id = getIntent().getStringExtra("id").toString();
        f1 = new Shopmenu_fragment();
        f2 = new Shopcomment_fragment();
        f3 = new Shopdetail_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        f1.setArguments(bundle);
        f2.setArguments(bundle);
        list.add(f1);
        list.add(f2);
        //加密数据
        String string = "getBusiness@"+getIntent().getStringExtra("id")+"@"+loginName;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityShopMessageBean.setUserId(loginName);
        mySecurityShopMessageBean.setSign(md5String);
        mySecurityShopMessageBean.setBusinessId(getIntent().getStringExtra("id"));
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


    private void initIds() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.back_restaurant);
        setSupportActionBar(toolbar);
        tablayout = (TabLayout) this.findViewById(R.id.tablayout);
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        iv_shop = (ImageView) this.findViewById(R.id.iv_shop);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setStatusBarScrimColor(Color.BLACK);
        //获取登录帐号
        SharedPreferences sp=ShopActivity.this.getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");

    }


    // 定义一个适配器给ViewPager
    class MyFragmentPageAdapter extends FragmentPagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

//            super.destroyItem(container, position, object);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_love:

                break;
            case R.id.btn_share:
                Intent ShareIntent = new Intent();
                ShareIntent.setAction(Intent.ACTION_SEND);
                ShareIntent.putExtra(Intent.EXTRA_TEXT, "我们的网站www.baidu.com");
                ShareIntent.setType("text/plain");
                ShopActivity.this.startActivity(Intent.createChooser(ShareIntent, "分享到"));
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
