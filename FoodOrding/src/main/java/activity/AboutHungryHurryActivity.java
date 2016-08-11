package activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;

/**
 * Created by cxk on 2016/4/15.
 */
public class AboutHungryHurryActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abouthungryhurry);
        ManageActivityUtils.addActivity(this);
        //如果安卓5.0设置状态栏为orange
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        btn_back = (Button) this.findViewById(R.id.btn_back);
        tv_title = (TextView) this.findViewById(R.id.actionbar_title);
        tv_title.setText("About Hunrry Hurry");
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                AboutHungryHurryActivity.this.finish();
                break;
        }
    }
}
