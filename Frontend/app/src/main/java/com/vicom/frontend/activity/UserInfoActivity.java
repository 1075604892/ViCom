package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.entity.User;
import com.vicom.frontend.fragment.PostListFragment;
import com.vicom.frontend.sqlite.DBManger;
import com.vicom.frontend.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    private Long uid;
    private User user;
    private static FragmentManager fragmentManager;
    private PostListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        //获取Intent中暂存的数据
        uid = Long.valueOf(intent.getStringExtra("uid"));

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new PostListFragment(2, uid);
        fragmentTransaction.add(R.id.id_my_post_list, fragment);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

        //修改UI
        ((TextView) findViewById(R.id.tvtitle)).setText("用户详情");

        //获取帖子
        postUserData(uid);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());

                user = new User();
                user.setUsername(jsonObject.getString("username"));
                user.setEmail(jsonObject.getString("email"));
                user.setNickname(jsonObject.getString("nickname"));
                user.setSex(jsonObject.getString("sex"));
                user.setIntroduce(jsonObject.getString("introduce"));
                user.setIcon(jsonObject.getString("icon"));

                ((TextView) findViewById(R.id.id_user_info_nickname)).setText(user.getNickname());
                ((TextView) findViewById(R.id.id_user_info_introduce)).setText(user.getIntroduce());
                ((MyImageView) findViewById(R.id.id_user_info_icon)).setImageURL(MyConfiguration.HOST + "/" + user.getIcon());

                if ("0".equals(user.getSex())) {
                    findViewById(R.id.id_female_icon).setVisibility(View.VISIBLE);
                } else if ("1".equals(user.getSex())) {
                    findViewById(R.id.id_male_icon).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void quit(View view) {
        onBackPressed();
    }

    public void postUserData(Long uid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送请求");
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", uid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/user/info").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();

                    JSONObject responseJson = new JSONObject(response.body().string());

                    //处理返回内容
                    Message message = new Message();
                    message.what = responseJson.getInt("code");

                    if (responseJson.getInt("code") == 0) {
                        message.obj = responseJson.getString("msg");
                    } else {
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
