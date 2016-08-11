package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;

/**
 * Created by cxk on 2016/8/2.
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    private Button btn_loginOut,btn_back;
    private SharedPreferences sp;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ManageActivityUtils.addActivity(this);
        init();

    }

    private void init() {
        btn_loginOut = (Button) this.findViewById(R.id.btn_loginOut);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        tv_title = (TextView) this.findViewById(R.id.actionbar_title);
        tv_title.setText("Setting");
        btn_loginOut.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        sp = getSharedPreferences("userMessage", MODE_PRIVATE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loginOut:
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("ifFirst","true");
                editor.commit();
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                ManageActivityUtils.finishAll();
                break;
            case R.id.btn_back:
                this.finish();
                break;
        }
    }
}
