package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.entity.Post;
import com.vicom.frontend.entity.SubPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReplyListActivity extends AppCompatActivity {
    private List<SubPost> subPostsWithoutReply = new ArrayList<SubPost>();
    private List<SubPost> replies = new ArrayList<SubPost>();


    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_reply_list);

        Intent intent = getIntent();
        //获取Intent中暂存的数据
        String pid = intent.getStringExtra("pid");

        //修改UI
        ((TextView) findViewById(R.id.tvtitle)).setText("帖子详情");

        //获取帖子
        JSONObject json = new JSONObject();
        try {
            json.put("pid", pid);
            json.put("page", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postRepliesData(json);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(ReplyListActivity.this, R.layout.item_reply_list, null);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SubPost thisSubPost = subPostsWithoutReply.get(position);
            holder.usernameTv.setText(thisSubPost.getUsername());
            holder.contentTv.setText(thisSubPost.getContent());
            holder.itemReplyView.setOnClickListener(v -> {
                System.out.println(thisSubPost.getId());
            });

            List<SubPost> thisReplies = replies.stream().filter(reply -> reply.getRid().equals(thisSubPost.getId())).collect(Collectors.toList());

            if (thisReplies.size() == 1) {
                holder.replyBoxView.setVisibility(View.VISIBLE);
                holder.reply1Tv.setText(thisReplies.get(0).getUsername() + ": " + thisReplies.get(0).getContent());
            } else if (thisReplies.size() == 2) {
                holder.replyBoxView.setVisibility(View.VISIBLE);
                holder.reply2Tv.setVisibility(View.VISIBLE);
                holder.reply1Tv.setText(thisReplies.get(0).getUsername() + ": " + thisReplies.get(0).getContent());
                holder.reply2Tv.setText(thisReplies.get(1).getUsername() + " 回复 "
                        + thisReplies.get(1).getReplyName() + " : " + thisReplies.get(1).getContent());
            } else if (thisReplies.size() > 2) {
                holder.replyBoxView.setVisibility(View.VISIBLE);
                holder.reply2Tv.setVisibility(View.VISIBLE);
                holder.reply1Tv.setText(thisReplies.get(0).getUsername() + ": " + thisReplies.get(0).getContent());
                holder.reply2Tv.setText(thisReplies.get(1).getUsername() + " 回复 "
                        + thisReplies.get(1).getReplyName() + " : " + thisReplies.get(1).getContent());
                holder.replyMoreTv.setVisibility(View.VISIBLE);
            }

            /*ArrayList<SubPost> thisReplies = new ArrayList<>();
            for (SubPost subPost : replies) {
                if (subPost.getType().equals("2")
                        && subPost.getRid().equals(subPostsWithoutReply.get(position).getId())) {
                    //第一条评论
                    holder.replyBoxView.setVisibility(View.VISIBLE);
                    holder.reply1Tv.setText(subPost.getUsername() + ": " + subPost.getContent());
                    thisReplies.add(subPost);
                } else if (thisReplies.size() == 1 &&
                        subPost.getType().equals("2")
                        && thisReplies.get(0).getId().equals(subPost.getRid())) {
                    //第二条评论
                    holder.reply2Tv.setText(subPost.getUsername() + ": " + subPost.getContent());
                    holder.reply2Tv.setVisibility(View.VISIBLE);
                    thisReplies.add(subPost);
                } else if (thisReplies.size() == 2 &&
                        subPost.getType().equals("2") &&
                        (thisReplies.get(0).getId().equals(subPost.getRid()) ||
                                thisReplies.get(1).getId().equals(subPost.getRid()))
                ) {
                    holder.replyMoreTv.setVisibility(View.VISIBLE);
                }
            }*/
        }

        @Override
        public int getItemCount() {
            return subPostsWithoutReply.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTv;
        TextView contentTv;
        View itemReplyView;

        //回复
        View replyBoxView;
        TextView reply1Tv;
        TextView reply2Tv;
        TextView replyMoreTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.reply_username);
            contentTv = itemView.findViewById(R.id.reply_content);
            itemReplyView = itemView.findViewById(R.id.item_reply_tab);

            //回复
            replyBoxView = itemView.findViewById(R.id.reply_box);
            reply1Tv = itemView.findViewById(R.id.reply_1);
            reply2Tv = itemView.findViewById(R.id.reply_2);
            replyMoreTv = itemView.findViewById(R.id.reply_more);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mRecyclerView = findViewById(R.id.reply_list_by_post);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(ReplyListActivity.this, DividerItemDecoration.VERTICAL));

            //添加主楼
            Intent intent = getIntent();
            SubPost subPostHead = new SubPost();
            subPostHead.setId(intent.getStringExtra("pid"));
            subPostHead.setContent(intent.getStringExtra("content"));
            subPostHead.setPicUrl(intent.getStringExtra("picUrl"));
            subPostHead.setUsername(intent.getStringExtra("username"));

            subPostsWithoutReply.add(subPostHead);

            String str = msg.obj.toString().replace("[", "").replace("]", "");
            String[] result = str.split("\\},");
            for (int i = 0; i < result.length; i++) {
                if (i < result.length - 1) {
                    result[i] += "}";
                }
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    SubPost subPost = new SubPost();
                    subPost.setId(jsonObject.getString("id"));
                    subPost.setContent(jsonObject.getString("content"));
                    subPost.setPicUrl(jsonObject.getString("picUrl"));
                    subPost.setUsername(jsonObject.getString("username"));
                    subPost.setReplyName(jsonObject.getString("replyName"));

                    subPost.setType(jsonObject.getString("type"));
                    subPost.setRid(jsonObject.getString("rid"));

                    if (subPost.getType().equals("1")) {
                        subPostsWithoutReply.add(subPost);
                    } else if (subPost.getType().equals("2")) {
                        replies.add(subPost);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ReplyListActivity.this, 1, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };

    public void postRepliesData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/post/querySubPostsByPid").post(requestBody)
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