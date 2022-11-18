package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.entity.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostListActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<Post>();
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_post_list);

        Intent intent = getIntent();
        //获取Intent中暂存的数据
        String name = intent.getStringExtra("name");
        String cid = intent.getStringExtra("cid");

        //修改UI
        ((TextView) findViewById(R.id.tvtitle)).setText(name);

        //获取帖子
        JSONObject json = new JSONObject();
        try {
            json.put("cid", cid);
            json.put("page", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postPostsData(json);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(PostListActivity.this, R.layout.item_post_list, null);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.usernameTv.setText(posts.get(position).getUsername());
            holder.contentTv.setText(posts.get(position).getContent());
            holder.titleTv.setText(posts.get(position).getTitle());

            holder.itemPostView.setOnClickListener(v -> {
                Intent intent = new Intent(PostListActivity.this, ReplyListActivity.class);
                intent.putExtra("pid", posts.get(position).getId());
                intent.putExtra("content", posts.get(position).getTitle() + "\n" +posts.get(position).getContent());
                intent.putExtra("picUrl", posts.get(position).getPicUrl());
                intent.putExtra("username", posts.get(position).getUsername());

                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTv;
        TextView titleTv;
        TextView contentTv;
        View itemPostView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.post_username);
            titleTv = itemView.findViewById(R.id.post_title);
            contentTv = itemView.findViewById(R.id.post_content);
            itemPostView = itemView.findViewById(R.id.item_post_tab);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mRecyclerView = findViewById(R.id.post_list_by_community);

            String str = msg.obj.toString().replace("[", "").replace("]", "");
            String[] result = str.split("\\},");
            for (int i = 0; i < result.length; i++) {
                if (i < result.length - 1) {
                    result[i] += "}";
                }
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    Post post = new Post();
                    post.setId(jsonObject.getString("id"));
                    post.setContent(jsonObject.getString("content"));
                    post.setPicUrl(jsonObject.getString("picUrl"));
                    post.setTitle(jsonObject.getString("title"));
                    post.setUsername(jsonObject.getString("username"));

                    posts.add(post);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(PostListActivity.this, 1, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };

    public void postPostsData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/post/queryPostsByCid").post(requestBody)
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

    public void quit(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}