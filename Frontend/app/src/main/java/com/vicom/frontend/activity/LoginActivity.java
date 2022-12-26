package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vicom.frontend.MainActivity;
import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.sqlite.DBManger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        //修改UI
        ((EditText) findViewById(R.id.et_login_pass)).setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((TextView) findViewById(R.id.tvtitle)).setText("登录");
    }

    public void submitLogin(View view) {
        String username = ((EditText) findViewById(R.id.et_login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_login_pass)).getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postLoginData(json);
    }

    public void quit(View view) {
        onBackPressed();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //System.out.println("主线程收到子线程处理消息的结果");

            if (msg.what == 1) {
                //登录成功，退出注册界面
                try {
                    Long uid = ((JSONObject) msg.obj).getLong("uid");
                    String cookie = ((JSONObject) msg.obj).getString("cookie");

                    Map<String, Object> map = DBManger.getInstance(LoginActivity.this).selectCookie();

                    if ((Long) map.get("uid") == -1) {
                        DBManger.getInstance(LoginActivity.this).addCookie(uid, cookie);

                        //更新界面UI
                        MainActivity.optionFragment.postUserData(uid, cookie);
                        MainActivity.talkFragment.loginUIAndHandle();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onBackPressed();
            }
        }

        ;
    };

    public void postLoginData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/user/login").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();

                    JSONObject responseJson = new JSONObject(response.body().string());

                    //处理返回内容
                    Message message = new Message();
                    message.what = responseJson.getInt("code");

                    if (responseJson.getInt("code") == 0) {
                        message.obj = responseJson.getString("msg");
                    } else {
                        message.obj = new JSONObject(responseJson.getString("map"));
                    }

                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}