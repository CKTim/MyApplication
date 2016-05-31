package activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cxk.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cxk on 2016/4/15.
 */
public class EditaddressActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private Button btn_back, btn_finish;
    private EditText et_Name, et_Surname, et_email, et_phone, et_city, et_district, et_detailAddress;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editaddress_activity);
        //如果安卓5.0设置状态栏为orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orange));
        }
        initIDs();
        sp = this.getSharedPreferences("address", MODE_PRIVATE);
        if (sp == null) {
            Log.e("aaaaaaaaaa", "为空");
        } else {
            et_Name.setText(sp.getString("name", ""));
            et_Surname.setText(sp.getString("surname", ""));
            et_email.setText(sp.getString("email", ""));
            et_phone.setText(sp.getString("phone", ""));
            et_city.setText(sp.getString("city", ""));
            et_district.setText(sp.getString("district", ""));
            et_detailAddress.setText(sp.getString("detailAddress", ""));
        }

    }

    private void initIDs() {
        tv_title = (TextView) this.findViewById(R.id.actionbar_title);
        btn_back = (Button) this.findViewById(R.id.btn_back);
        btn_finish = (Button) this.findViewById(R.id.btn_finish);
        et_Name = (EditText) this.findViewById(R.id.et_Name);
        et_Surname = (EditText) this.findViewById(R.id.et_Surname);
        et_email = (EditText) this.findViewById(R.id.et_email);
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_city = (EditText) this.findViewById(R.id.et_city);
        et_district = (EditText) this.findViewById(R.id.et_district);
        et_detailAddress = (EditText) this.findViewById(R.id.et_detailAddress);
        btn_back.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        tv_title.setText("My Address");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_finish:
                sp=getSharedPreferences("address",MODE_PRIVATE);
                String name = et_Name.getText().toString();
                String surname = et_Surname.getText().toString();
                String email = et_email.getText().toString();
                String phone = et_phone.getText().toString();
                String city = et_city.getText().toString();
                String district = et_district.getText().toString();
                String detailAddress = et_detailAddress.getText().toString();
                editor = sp.edit();
                editor.putString("name", name);
                editor.putString("surname", surname);
                editor.putString("email", email);
                editor.putString("phone", phone);
                editor.putString("city", city);
                editor.putString("district", district);
                editor.putString("detailAddress", detailAddress);
                editor.putString("ifFirst","false");
                editor.commit();
                this.finish();
                break;
        }
    }
}
