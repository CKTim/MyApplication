package widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

import com.example.cxk.myapplication.MainActivity;
import com.example.cxk.myapplication.R;

import activity.AboutHungryHurryActivity;
import activity.CustomersupportActivity;
import activity.HistoryActivity;

public class Custom_leftdrawer extends RelativeLayout implements OnClickListener {
    private View view;
    private RelativeLayout rl_neworder, rl_history, rl_customer_support, rl_setting, rl_about, rl_share;
    private DrawerLayout drawerLayout;
    public Custom_leftdrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_leftdrawer, this);
        //初始化ID
        initIDs();
    }

    private void initIDs() {
        rl_neworder = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl1_neworder);
        rl_history = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl2_History);
        rl_customer_support = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl3_CustomerSupport);
        rl_setting = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl4_settings);
        rl_about = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl5_about_Hunry);
        rl_share = (RelativeLayout) view.findViewById(R.id.leftdrawer_rl6_share);
        rl_neworder.setOnClickListener(this);
        rl_history.setOnClickListener(this);
        rl_customer_support.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftdrawer_rl1_neworder:
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.leftdrawer_rl2_History:
                Intent intent1 = new Intent(getContext(), HistoryActivity.class);
                getContext().startActivity(intent1);
                break;
            case R.id.leftdrawer_rl3_CustomerSupport:
                Intent intent2=new Intent(getContext(), CustomersupportActivity.class);
                getContext().startActivity(intent2);
                break;
            case R.id.leftdrawer_rl4_settings:
                break;
            case R.id.leftdrawer_rl5_about_Hunry:
                Intent intent4=new Intent(getContext(), AboutHungryHurryActivity.class);
                getContext().startActivity(intent4);
                break;
            case R.id.leftdrawer_rl6_share:
                Intent ShareIntent=new Intent();
                ShareIntent.setAction(Intent.ACTION_SEND);
                ShareIntent.putExtra(Intent.EXTRA_TEXT,"我们的网站www.baidu.com");
                ShareIntent.setType("text/plain");
                getContext().startActivity(Intent.createChooser(ShareIntent,"分享到"));
                break;
        }
    }
}
