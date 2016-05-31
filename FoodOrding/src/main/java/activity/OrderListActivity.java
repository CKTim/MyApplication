package activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cxk.myapplication.R;
import com.google.gson.Gson;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.AllMessageOrderListBean;
import bean.OrderListBean;
import utils.HttpUtilsAddToLove;
import utils.HttpUtilsGetJson;
import utils.HttpUtilsSendOrder;

/**
 * Created by cxk on 2016/4/12.
 */
public class OrderListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listview;
    private List<Map<String, Object>> list_special_order;
    private String listString;
    private View HeadView;
    private List<Map<String, Object>> gridview_list;
    private List<Map<String, Object>> list;
    private HashMap<Object, Object> map_status;
    private TextView tv_allcount;
    private int count;
    private double total_count;
    private Button btn_opendrawer, btn_paynow,btn_openMylove;
    private DrawerLayout drawerLayout;
    private String url;
    private String urlUTF8;
    private TextView tv_single_special;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlist_activity);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        HeadView = LayoutInflater.from(OrderListActivity.this).inflate(R.layout.orderlistview_headview, listview, false);
        //初始化各个控件ID
        initIDs();
        //为listview添加头部并且设置适配器
        list = new ArrayList<>();
        listview.addHeaderView(HeadView, null, true);
        listview.setAdapter(new MyListViewAdapter());
        list_special_order = new ArrayList<Map<String, Object>>();
        //获取传过来的list并且提取出其中数量不为0的菜式
        list = (List<Map<String, Object>>) getIntent().getSerializableExtra("list");
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(list.get(i).get("count").toString()) == 0) {
                list.remove(i);
                i--;
            }
        }

    }

    private void initIDs() {
        listview = (ListView) this.findViewById(R.id.secondstep_orderlist_lv);
        tv_allcount = (TextView) this.findViewById(R.id.tv_total_count);
        btn_opendrawer = (Button) this.findViewById(R.id.btn_opendrawer);
        btn_paynow = (Button) this.findViewById(R.id.btn_paynow);
        drawerLayout = (DrawerLayout) this.findViewById(R.id.drawerlayout);
        btn_openMylove= (Button) this.findViewById(R.id. btn_openmylove);
        btn_opendrawer.setOnClickListener(this);
        btn_paynow.setOnClickListener(this);
        btn_openMylove.setOnClickListener(this);
        tv_allcount.setText(getIntent().getStringExtra("totalPrices"));
        map_status = new HashMap<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_opendrawer:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.btn_paynow:
                Gson gson=new Gson();
                //定义一个显示菜的订单的bean
                List<OrderListBean> listbean=new ArrayList<>();
                //定义一个显示所有信息的bean，包括用户ID，地址等
                AllMessageOrderListBean allMessageOrderListBean=new AllMessageOrderListBean();
                //将用户ID等信息加进去
                allMessageOrderListBean.setBusinessId(Integer.parseInt(getIntent().getStringExtra("id")));
                allMessageOrderListBean.setUserId(9);
                allMessageOrderListBean.setAmount(Double.valueOf(tv_allcount.getText().toString()));
                //用一个for循环将各个菜的属性以及special order加进来
                for(int i=0;i<list.size();i++){
                    OrderListBean sendOrderListBean=new OrderListBean();
                    //获取用户选中的special order
                    List<Map<String,Object>> list_order= (List<Map<String, Object>>) list.get(i).get("sure_orderlist");
                    listString=new String();
                    //将每个菜选中的special order，用一个for循环加到一个list《String》中
                    for(int j=0;j<list_order.size();j++){
                        listString=listString+"#"+list_order.get(j).get("singe_special").toString().replace(" ","");

                    }
                    sendOrderListBean.setFoodId(Integer.parseInt(list.get(i).get("ID").toString()));
                    sendOrderListBean.setSpecial(listString);
                    sendOrderListBean.setNum(Integer.parseInt(list.get(i).get("count").toString()));
                    listbean.add(sendOrderListBean);
                }
                //将菜单的bean装到所有信息的bean里面
                allMessageOrderListBean.setOrderitem(listbean);
                Intent intent = new Intent(OrderListActivity.this, ProfileActivity.class);
                intent.putExtra("allMessageBean",allMessageOrderListBean);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                startActivity(intent);
                break;
            case R.id.btn_openmylove:
                Intent intent0=new Intent(OrderListActivity.this,MyLoveActivity.class);
                startActivity(intent0);
                break;
        }
    }


    //自定义一个listview适配器
    class MyListViewAdapter extends BaseAdapter {
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
                convertView = LayoutInflater.from(OrderListActivity.this).inflate(R.layout.custom_listview_orderlist, null);
                holder.tv_foodname = (TextView) convertView.findViewById(R.id.tv_foodname);
                holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
                holder.btn_decrese = (Button) convertView.findViewById(R.id.btn_decrease);
                holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                holder.tv_prices = (TextView) convertView.findViewById(R.id.tv_prices);
                holder.btn_specialOrder = (Button) convertView.findViewById(R.id.btn_special_order);
                holder.gridview = (GridView) convertView.findViewById(R.id.gridview);
                holder.btn_sure = (Button) convertView.findViewById(R.id.btn_sure_gridview);
                holder.btn_dissmiss = (Button) convertView.findViewById(R.id.btn_diss_gridview);
                holder.rl_special_order_list = (RelativeLayout) convertView.findViewById(R.id.special_order_list);
                holder.ln_ln_list_special = (LinearLayout) convertView.findViewById(R.id.ln_list_special);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //点击special order出现gridview列表
            holder.btn_specialOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list_special_order = new ArrayList<Map<String, Object>>();
                    holder.ln_ln_list_special.setVisibility(View.GONE);
                    holder.ln_ln_list_special.removeAllViews();
                    //判断将special order下面的布局是否可见
                    if (holder.ln_ln_list_special.getVisibility() == View.VISIBLE) {
                        //将special order下面的布局隐藏并且移除里面的东西
                        holder.ln_ln_list_special.setVisibility(View.GONE);
                        holder.ln_ln_list_special.removeAllViews();
                    }

                    holder.btn_specialOrder.setSelected(true);
                    holder.rl_special_order_list.setVisibility(View.VISIBLE);

                }
            });

            //为 holder.gridview设置点击监听事件
            holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_single_special = (TextView) view.findViewById(R.id.tv_special_order);
                    if (tv_single_special.isSelected()) {
                        tv_single_special.setSelected(false);
                        //将gridview item里面的textview内容从list移除
                        for (int i = 0; i < list_special_order.size(); i++) {
                            if (list_special_order.get(i).get("position").equals(position + "")) {
                                list_special_order.remove(i);
                            }
                        }

                    } else {
                        tv_single_special.setSelected(true);
                        //将gridview item里面的textview内容加到list当中
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("singe_special", tv_single_special.getText().toString());
                        map.put("position", position + "");
                        list_special_order.add(map);
                    }
                }
            });

            //为gridview下面那两个按钮设置监听事件
            holder.btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list_special_order.size() != 0) {
                        //隐藏gridview
                        holder.rl_special_order_list.setVisibility(View.GONE);
                        //显示special下面的线性布局
                        holder.ln_ln_list_special.setVisibility(View.VISIBLE);
                        for (int i = 0; i < list_special_order.size(); i++) {
                            TextView tv = new TextView(OrderListActivity.this);
                            tv.setTextColor(getResources().getColor(R.color.gray));
                            tv.setText("-" + (String) list_special_order.get(i).get("singe_special"));
                            holder.ln_ln_list_special.addView(tv);
                        }
                      //map_status用于记录选好的special order 方便恢复
                        map_status.put(position, list_special_order);
                        list.get(position).put("sure_orderlist",map_status.get(position));
                    }

                }
            });

            holder.btn_dissmiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //隐藏gridview
                    holder.rl_special_order_list.setVisibility(View.GONE);
                    //将holder.btn_specialOrder按钮变暗
                    holder.btn_specialOrder.setSelected(false);
                    //状态map移除相应数据
                    map_status.remove(position);
                }
            });

            //为增加和减少按钮添加点击事件
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = Integer.parseInt(holder.tv_count.getText().toString());
                    total_count = Double.valueOf(tv_allcount.getText().toString());
                    holder.tv_count.setText(count + 1 + "");
                    tv_allcount.setText(total_count + Double.valueOf(holder.tv_prices.getText().toString()) + "");
                    //将更改后的mount put进去，防止下拉后数量回复默认
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get("ID").toString().trim().equals(list.get(position).get("ID").toString().trim())) {
                            list.get(i).put("count", holder.tv_count.getText().toString());

                        }
                    }
                }
            });

            holder.btn_decrese.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = Integer.parseInt(holder.tv_count.getText().toString());
                    if (count >= 1) {
                        total_count = Double.valueOf(tv_allcount.getText().toString());
                        holder.tv_count.setText(count - 1 + "");
                        tv_allcount.setText(total_count - Double.valueOf(holder.tv_prices.getText().toString()) + "");
                        //将更改后的mount put进去，防止下拉后数量回复默认
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).get("ID").toString().trim().equals(list.get(position).get("ID").toString().trim())) {
                                list.get(i).put("count", holder.tv_count.getText().toString());

                            }
                        }
                    }


                }
            });

            //判断是否有special_order,有就显示。并且加载special order button下面的gridview
            gridview_list = (List<Map<String, Object>>) list.get(position).get("special_order");

            //判断这个special order集合里面是否有数据
            if (gridview_list.size() != 0) {
                //开始装载gridview
                SimpleAdapter simpleAdapter = new SimpleAdapter(OrderListActivity.this, gridview_list,
                        R.layout.custom_gridview_orderlist, new String[]{"special"},
                        new int[]{R.id.tv_special_order});
                holder.gridview.setAdapter(simpleAdapter);
                //有的话显示special order button
                holder.btn_specialOrder.setVisibility(View.VISIBLE);
                holder.rl_special_order_list.setVisibility(View.GONE);
                holder.ln_ln_list_special.setVisibility(View.GONE);
                //map_status.get(position)判断当前位置是否有已经选好的list order
                if (map_status.get(position) != null) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) map_status.get(position);
                    //恢复数据前先移除里面原先的东西
                    holder.ln_ln_list_special.removeAllViews();
                    for (int i = 0; i < list.size(); i++) {
                        TextView tv = new TextView(OrderListActivity.this);
                        tv.setTextColor(getResources().getColor(R.color.gray));
                        tv.setText("-" + (String) list.get(i).get("singe_special"));
                        holder.ln_ln_list_special.addView(tv);
                    }
                    holder.ln_ln_list_special.setVisibility(View.VISIBLE);
                    holder.btn_specialOrder.setSelected(true);
                }else{
                    holder.btn_specialOrder.setSelected(false);
                }


            } else {
                holder.btn_specialOrder.setVisibility(View.GONE);
                holder.rl_special_order_list.setVisibility(View.GONE);
                holder.ln_ln_list_special.setVisibility(View.GONE);
            }
            holder.tv_prices.setText(list.get(position).get("single_money") + "");
            holder.tv_count.setText(list.get(position).get("count") + "");
            holder.tv_foodname.setText(list.get(position).get("food_name") + "");


            return convertView;
        }

        class ViewHolder {
            //食物名字
            TextView tv_foodname;
            //增加按钮
            Button btn_add;
            //减少按钮
            Button btn_decrese;
            //数量
            TextView tv_count;
            //价格
            TextView tv_prices;
            //specialorder
            Button btn_specialOrder;
            //specialorder list
            GridView gridview;
            //确定按钮
            Button btn_sure;
            //消失按钮
            Button btn_dissmiss;
            //装载着gridview和那两个按钮的布局
            RelativeLayout rl_special_order_list;
            //装载着special order Textview的线性布局
            LinearLayout ln_ln_list_special;


        }
    }
}
