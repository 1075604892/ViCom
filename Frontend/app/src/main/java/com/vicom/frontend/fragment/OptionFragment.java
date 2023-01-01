package com.vicom.frontend.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MainActivity;
import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.activity.PostListActivity;
import com.vicom.frontend.activity.ReplyListActivity;
import com.vicom.frontend.entity.Community;
import com.vicom.frontend.entity.Post;
import com.vicom.frontend.entity.User;
import com.vicom.frontend.sqlite.DBManger;
import com.vicom.frontend.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OptionFragment extends Fragment {
    private View view;

    private int type = 0;

    public OptionFragment() {
        type = 0;
    }

    public OptionFragment(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.option_tab, container, false);

        Long uid = (Long) DBManger.getInstance(getContext()).selectCookie().get("uid");
        String cookie = (String) DBManger.getInstance(getContext()).selectCookie().get("cookie");
        if (uid != -1) {
            postUserData(uid, cookie);
        }

        return view;
    }

    public void unLoginUI() {
        view.findViewById(R.id.unlogin_pad).setVisibility(View.VISIBLE);
        view.findViewById(R.id.id_user_info).setVisibility(View.GONE);
    }

    public void loginUI() {
        view.findViewById(R.id.unlogin_pad).setVisibility(View.GONE);
        view.findViewById(R.id.id_user_info).setVisibility(View.VISIBLE);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());

                User user = new User();
                user.setUsername(jsonObject.getString("username"));
                user.setNickname(jsonObject.getString("nickname"));
                user.setSex(jsonObject.getString("sex"));
                user.setIntroduce(jsonObject.getString("introduce"));
                user.setIcon(jsonObject.getString("icon"));

                ((TextView) view.findViewById(R.id.id_user_info_nickname)).setText(user.getNickname());
                ((TextView) view.findViewById(R.id.id_user_info_introduce)).setText(user.getIntroduce());
                System.out.println();
                ((MyImageView) view.findViewById(R.id.id_user_info_icon)).setImageURL(MyConfiguration.HOST + "/" + user.getIcon());

                if ("0".equals(user.getSex())) {
                    view.findViewById(R.id.id_female_icon).setVisibility(View.VISIBLE);
                } else if ("1".equals(user.getSex())) {
                    view.findViewById(R.id.id_male_icon).setVisibility(View.VISIBLE);
                }

                view.findViewById(R.id.unlogin_pad).setVisibility(View.GONE);
                view.findViewById(R.id.id_user_info).setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void postUserData(Long uid, String cookie) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送请求");
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", uid);
                    json.put("cookie", cookie);
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