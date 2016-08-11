package fragment;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.MySecurityShopMessageBean;
import bean.ShopMessageBean;
import cn.gdin.hk.hungry.R;
import utils.HttpUtilsGetJson;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/4/9.
 */
public class Shopcomment_fragment extends Fragment {
    private View v;
    private ListView listview;
    List<Map<String, Object>> list;
    private MyListViewAdapter adapter;
    private RatingBar ratingBar;
    private HttpUtilsGetJson httpUtilsGetJson = new HttpUtilsGetJson();
    private ShopMessageBean shopMessageBean;
    private ShopMessageBean ShopmessageBean;
    private String url;
    private String id;
    private static final int Finish = 0;
    private TextView tv_Message;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityShopMessageBean mySecurityShopMessageBean=new MySecurityShopMessageBean();
    private String loginName;;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Finish:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    // 使用gson映射得到bean类
                    ShopmessageBean= gson.fromJson(json, ShopMessageBean.class);
                    ratingBar.setRating(Float.valueOf(ShopmessageBean.grade));
                    tv_Message.setText(ShopmessageBean.msg);
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=LayoutInflater.from(getActivity()).inflate(R.layout.shopcomment_fragment,null);
        //初始化ID
        initIDS();
        list = new ArrayList<Map<String, Object>>();
        adapter=new MyListViewAdapter();
        listview.setAdapter(adapter);
        listview.setFocusable(false);
        //获取数据
        GetData();
        return v;
    }

    private void initIDS() {
        listview = (ListView) v.findViewById(R.id.listview);
        ratingBar=(RatingBar)v.findViewById(R.id.level_ratingbar);
        tv_Message=(TextView)v.findViewById(R.id.tv_Message);
        //获取登录帐号
        SharedPreferences sp=getActivity().getSharedPreferences("userMessage", Context.MODE_PRIVATE);
        loginName=sp.getString("loginName","");
    }


    private void GetData() {
        //获取商家的ID
        id = getArguments().getString("id").toString();
        //加密数据
        String string = "getBusiness@"+id+"@"+loginName;
        String md5String = MySecurityUtil.string2MD5(string);
        mySecurityShopMessageBean.setUserId(loginName);
        mySecurityShopMessageBean.setSign(md5String);
        mySecurityShopMessageBean.setBusinessId(id);
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



    //自定义一个适配器
    public class MyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 20;
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
            if(convertView==null){
                holder=new ViewHolder();
                convertView=LayoutInflater.from(getActivity()).inflate(R.layout.custom_listview_comment, null);
                holder.imageview=(ImageView) convertView.findViewById(R.id.iv_nick);
                holder.tv_nick=(TextView) convertView.findViewById(R.id.tv_username);
                holder.tv_data=(TextView) convertView.findViewById(R.id.tv_data);
                holder.tv_comment=(TextView) convertView.findViewById(R.id.tv_comment);
                convertView.setTag(holder);
            } else {
                holder=(ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        class ViewHolder{
            ImageView imageview;
            TextView tv_nick;
            TextView tv_data;
            TextView tv_comment;
        }

    }

}
