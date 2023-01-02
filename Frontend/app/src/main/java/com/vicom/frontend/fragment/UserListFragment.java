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

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.activity.PostListActivity;
import com.vicom.frontend.activity.UserInfoActivity;
import com.vicom.frontend.entity.Community;
import com.vicom.frontend.entity.User;
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

public class UserListFragment extends Fragment {
    private View view;
    private List<User> users = new ArrayList<>();

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.talk_tab, container, false);

        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_community_list, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.mTitleTv.setText(users.get(position).getNickname());
            holder.imageView.setImageURL(MyConfiguration.HOST + "/" + users.get(position).getIcon());
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), UserInfoActivity.class);
                intent.putExtra("uid", users.get(position).getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTv;
        MyImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.community_name);
            imageView = itemView.findViewById(R.id.community_image);
        }
    }

    public void postSearchUserData(String name) {
        users.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");

                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/user/search").post(requestBody)
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //System.out.println("主线程收到子线程处理消息的结果");
            mRecyclerView = view.findViewById(R.id.community_list);

            String str = msg.obj.toString().replace("[", "").replace("]", "");
            String[] result = str.split("\\},");
            for (int i = 0; i < result.length; i++) {
                if (i < result.length - 1) {
                    result[i] += "}";
                }
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    User user = new User();
                    user.setUsername(jsonObject.getString("username"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setId(jsonObject.getString("id"));
                    user.setNickname(jsonObject.getString("nickname"));
                    user.setIcon(jsonObject.getString("icon"));
                    user.setStatus(jsonObject.getString("status"));
                    user.setLevel(jsonObject.getString("level"));
                    user.setSex(jsonObject.getString("sex"));
                    users.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!users.isEmpty()) {
                TextView tvTitle = ((TextView) view.findViewById(R.id.tv_title));
                tvTitle.setText("相关用户");
                tvTitle.setVisibility(View.VISIBLE);
            }

            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };
}