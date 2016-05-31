package activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cxk.myapplication.R;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bean.CategoryFoodBean;
import bean.MySecurityHeatBean;
import bean.MySecurityResignBean;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/5/8.
 */
public class ResignActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_loginName, et_loginPassword, et_loginNickname, et_repeated_loginPassword;
    private String loginName, loginPassword, loginNickname, repeated_loginPassword;
    private Button btn_resign,btn_back;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MySecurityResignBean mySecurityResignBean = new MySecurityResignBean();
    private static final int FINISH = 0;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getString("success").equals("true")) {
                            Toast.makeText(ResignActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            ResignActivity.this.finish();
                        } else {
                            Toast.makeText(ResignActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resign_activity);
        //初始化各个控件ID
        initIDs();
    }

    private void initIDs() {
        et_loginName = (EditText) this.findViewById(R.id.resign_et_LoginName);
        et_loginPassword = (EditText) this.findViewById(R.id.resign_et_password);
        et_loginNickname = (EditText) this.findViewById(R.id.resign_et_nickname);
        et_repeated_loginPassword = (EditText) this.findViewById(R.id.resign_et_password2);
        btn_resign = (Button) this.findViewById(R.id.btn_resign);
        btn_back= (Button) this.findViewById(R.id.btn_back);
        btn_resign.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resign:
                if (et_loginNickname.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ResignActivity.this, "昵称为空", Toast.LENGTH_SHORT).show();
                } else if (et_loginName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ResignActivity.this, "帐号为空", Toast.LENGTH_SHORT).show();
                } else if (et_loginPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ResignActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                } else if (et_repeated_loginPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ResignActivity.this, "重复密码为空", Toast.LENGTH_SHORT).show();
                } else if (!et_repeated_loginPassword.getText().toString().trim().equals(et_loginPassword.getText().toString().trim())) {
                    Toast.makeText(ResignActivity.this, "两次输入的密码不一样", Toast.LENGTH_SHORT).show();
                } else {
                    loginName = et_loginName.getText().toString();
                    loginPassword = et_loginPassword.getText().toString();
                    loginNickname = et_loginNickname.getText().toString();
                    repeated_loginPassword = et_repeated_loginPassword.getText().toString();
                    //加密数据
                    String string = "register@" + loginNickname;
                    String md5String = MySecurityUtil.string2MD5(string);
                    String md5loginName = MySecurityUtil.string2MD5(loginName);
                    String md5loginPassword = MySecurityUtil.string2MD5(loginPassword);
                    mySecurityResignBean.setName(loginNickname);
                    mySecurityResignBean.setSign(md5String);
                    mySecurityResignBean.setTel(md5loginName);
                    mySecurityResignBean.setPasswd(md5loginPassword);
                    String json = gson.toJson(mySecurityResignBean);
                    Log.e("AAAAA", json);
                    //encrypt加密
                    String encryptJson = MySecurityUtil.encrypt(json);
                    //加密后在String2Base64
                    String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                    String url = "http://202.171.212.154:8080/hh/register.action?json=" + encryptJsonToBase64;
                    Log.e("AAAAAAA", url);
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
                                Log.e("aaaaaaa", "成功");
                                //将所得到的传回回去
                                Message message = Message.obtain();
                                message.obj = response.body().string();
                                message.what = FINISH;
                                mhandler.sendMessage(message);
                            }
                        }
                    });
                }

                break;
            case R.id.btn_back:
                this.finish();
                break;
        }
    }
}
