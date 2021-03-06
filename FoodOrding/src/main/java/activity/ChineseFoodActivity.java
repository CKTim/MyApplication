package activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;

import cn.gdin.hk.hungry.R;
import fragment.Chinese_fragment1;
import fragment.Chinese_fragment2;
import utils.ManageActivityUtils;

/**
 * Created by cxk on 2016/4/7.
 */
public class ChineseFoodActivity extends FragmentActivity implements View.OnClickListener {
    private TabLayout tablayout;
    private ViewPager viewpager;
    private List<Fragment> list;
    private FragmentManager manager;
    private FragmentTransaction transtion;
    private MyFragmentPageAdapter adapter;
    private List<String> titles;
    private Button btn_back,btn_love;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chinesefood_activity);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        //先findID
        initIDs();
        //将各个标题装在titles里面
        titles = new ArrayList<String>();
        titles.add("熱門菜式");
        titles.add("今日推薦");
        //将两个Fragment装进集合中
        list = new ArrayList<Fragment>();
        Chinese_fragment1 f1 = new Chinese_fragment1();
        Chinese_fragment2 f2 = new Chinese_fragment2();
        list.add(f1);
        list.add(f2);
        //使用manager和transtion提交事务后为viewpager设置一个适配器
        manager = getSupportFragmentManager();
        adapter = new MyFragmentPageAdapter(manager);
        transtion = manager.beginTransaction();
        transtion.commit();
        viewpager.setAdapter(adapter);
        // tablayout.addTab可以将标题添加进Tab里面，true表示默认选中
        tablayout.addTab(tablayout.newTab().setText(titles.get(0)), true);
        tablayout.addTab(tablayout.newTab().setText(titles.get(1)), false);
        //这两个方法是将Tablayout和Viewpager联合起来
        tablayout.setupWithViewPager(viewpager);
        tablayout.setTabsFromPagerAdapter(adapter);
    }

    private void initIDs() {
        tablayout = (TabLayout) this.findViewById(R.id.chinese_tablayout);
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        btn_back=(Button)this.findViewById(R.id.btn_back);
        btn_love=(Button)this.findViewById(R.id.btn_openmylove);
        btn_back.setOnClickListener(this);
        btn_love.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                ChineseFoodActivity.this.finish();
                break;
            case R.id.btn_openmylove:
                Intent intent=new Intent(ChineseFoodActivity.this,MyLoveActivity.class);
                startActivity(intent);
                break;
        }
    }


    //定义一个适配器给ViewPager
    class MyFragmentPageAdapter extends FragmentPagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {

            return list.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles.get(position);
        }

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            super.destroyItem(container, position, object);
        }

    }
}
