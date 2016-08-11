package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.gdin.hk.hungry.MainActivity;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bean.MySecurityResignBean;
import cn.gdin.hk.hungry.R;
import utils.ManageActivityUtils;
import utils.MySecurityUtil;

/**
 * Created by cxk on 2016/5/8.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_loginName, et_passWord;
    private Button btn_login, btn_resign;
    private String loginName, passWord;
    private MySecurityResignBean mySecurityResignBean = new MySecurityResignBean();
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private static final int FINISH = 0;
    private SharedPreferences sp;
    private long firstTime=0;
    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISH:
                    //解密数据
                    String base64ToString = MySecurityUtil.Base64ToString((String) msg.obj);
                    String json = MySecurityUtil.decrypt(base64ToString);
                    Log.e("aaaaaaa", "成功登录" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getString("success").equals("true")) {
                            sp = getSharedPreferences("userMessage", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("loginName", jsonObject.getString("userId"));
                            editor.putString("loginAccount",et_loginName.getText().toString());
                            editor.putString("ifFirst", "false");
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "密码错误或者帐号不存在", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.login_activity);
        ManageActivityUtils.addActivity(this);
        //初始化各个控件ID
        initIDs();
        //判断是否已经登录过
            sp = getSharedPreferences("userMessage", MODE_PRIVATE);
            String ifFirst = sp.getString("ifFirst", "true");
            if (ifFirst.equals("false")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
        }
    }

    private void initIDs() {
        et_loginName = (EditText) this.findViewById(R.id.login_et_loginName);
        et_passWord = (EditText) this.findViewById(R.id.login_et_Password);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_resign = (Button) this.findViewById(R.id.btn_resign);
        btn_login.setOnClickListener(this);
        btn_resign.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Log.e("AAAAA", "点击了");
                loginName = et_loginName.getText().toString();
                passWord = et_passWord.getText().toString();
                //加密数据
                String string = "login###";
                String md5String = MySecurityUtil.string2MD5(string);
                String md5loginName = MySecurityUtil.string2MD5(loginName);
                String md5loginPassword = MySecurityUtil.string2MD5(passWord);
                mySecurityResignBean.setSign(md5String);
                mySecurityResignBean.setTel(md5loginName);
                mySecurityResignBean.setPasswd(md5loginPassword);
                String json = gson.toJson(mySecurityResignBean);
                Log.e("AAAAA", json);
                //encrypt加密
                String encryptJson = MySecurityUtil.encrypt(json);
                //加密后在String2Base64
                String encryptJsonToBase64 = MySecurityUtil.String2Base64(encryptJson);
                String url = "http://202.171.212.154:8080/hh/login.action?json=" + encryptJsonToBase64;
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
                            //将所得到的传回回去
                            Message message = Message.obtain();
                            message.obj = response.body().string();
                            message.what = FINISH;
                            mhandler.sendMessage(message);
                        }
                    }
                });


                break;
            case R.id.btn_resign:
                Intent intent = new Intent(LoginActivity.this, ResignActivity.class);
                startActivity(intent);
                break;
        }
    }

    //实现点击两次返回键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            long secondTime=System.currentTimeMillis();
            if(secondTime-firstTime>2000){
                Toast.makeText(this,"請按一次退出程序",Toast.LENGTH_SHORT).show();
                firstTime=secondTime;
                return true;
            }else{
                ManageActivityUtils.finishAll();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
