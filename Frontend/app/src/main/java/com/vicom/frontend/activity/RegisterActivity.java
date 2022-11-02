package com.vicom.frontend.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //隐藏密码框
        //((EditText) findViewById(R.id.et_reg_pass)).setTransformationMethod(PasswordTransformationMethod.getInstance());
        //((EditText) findViewById(R.id.et_reg_pass_repeat)).setTransformationMethod(PasswordTransformationMethod.getInstance());

        setContentView(R.layout.activity_register);
    }

    public void submitRegister(View view) {
        String username = ((EditText) findViewById(R.id.et_reg_username)).getText().toString();
        String email = ((EditText) findViewById(R.id.et_reg_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_reg_pass)).getText().toString();
        String passwordRepeat = ((EditText) findViewById(R.id.et_reg_pass_repeat)).getText().toString();

        String nickname = ((EditText) findViewById(R.id.et_reg_nickname)).getText().toString();
        //获取性别
        //todo

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("email", email);
            json.put("password", password);
            json.put("nickname", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postRegisterData(json);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //System.out.println("主线程收到子线程处理消息的结果");
            Toast.makeText(RegisterActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();

            if (msg.what == 1){
                //注册成功，退出注册界面
            }
        };
    };

    public void postRegisterData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/user/register").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();

                    JSONObject responseJson = new JSONObject(response.body().string());

                    //处理返回内容
                    Message message = new Message();
                    message.what = responseJson.getInt("code");

                    if (responseJson.getInt("code") == 0){
                        message.obj = responseJson.getString("msg");
                    }else{
                        message.obj = responseJson.getString("data");
                    }

                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}