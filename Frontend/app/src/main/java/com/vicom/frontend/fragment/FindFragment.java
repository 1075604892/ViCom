package com.vicom.frontend.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.activity.ReplyListActivity;
import com.vicom.frontend.entity.Post;
import com.vicom.frontend.entity.SubPost;
import com.vicom.frontend.entity.User;
import com.vicom.frontend.sqlite.DBManger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindFragment extends Fragment {
    private View view;
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    private List<SubPost> replies = new ArrayList<SubPost>();

    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_tab, container, false);
        Long uid = DBManger.getInstance(getContext()).getUid();
        if (uid != -1) {
            loginUIAndHandle();
        } else {
            unLoginUIAndHandle();
        }


        // Inflate the layout for this fragment
        return view;
    }

    public void unLoginUIAndHandle() {
        view.findViewById(R.id.id_unLogin_home).setVisibility(View.VISIBLE);
        view.findViewById(R.id.reply_list).setVisibility(View.GONE);
        replies.clear();
    }

    public void loginUIAndHandle() {
        view.findViewById(R.id.id_unLogin_home).setVisibility(View.GONE);
        view.findViewById(R.id.reply_list).setVisibility(View.VISIBLE);
        Long uid = DBManger.getInstance(getContext()).getUid();
        String cookie = DBManger.getInstance(getContext()).getCookie();

        postRepliesData(uid, cookie, 0);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_reply_list2, null);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SubPost thisSubPost = replies.get(position);
            holder.usernameTv.setText(thisSubPost.getUsername());
            holder.contentTv.setText(thisSubPost.getContent());
            holder.itemReplyView.setOnClickListener(v -> {
                postPostInfoData(Long.valueOf(replies.get(position).getPid()));
            });
        }

        @Override
        public int getItemCount() {
            return replies.size();
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
            mRecyclerView = view.findViewById(R.id.reply_list);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

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
                    subPost.setPid(jsonObject.getString("pid"));

                    subPost.setType(jsonObject.getString("type"));
                    subPost.setRid(jsonObject.getString("rid"));

                    replies.add(subPost);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandlerForPostInformation = new Handler() {
        public void handleMessage(Message msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());
                Post post = new Post();
                post.setUsername(jsonObject.getString("username"));
                post.setTitle(jsonObject.getString("title"));
                post.setContent(jsonObject.getString("content"));
                post.setId(jsonObject.getString("id"));
                post.setPicUrl(jsonObject.getString("picUrl"));

                //跳转
                //跳转到相应的帖子
                Intent intent = new Intent(getContext(), ReplyListActivity.class);
                intent.putExtra("pid", post.getId());
                intent.putExtra("content", post.getTitle()+ "\n" +post.getContent());
                intent.putExtra("picUrl", post.getPicUrl());
                intent.putExtra("username", post.getUsername());
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void postRepliesData(Long uid, String cookie, Integer page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取帖子
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", uid);
                    json.put("cookie", cookie);
                    json.put("page", page);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/post/getReplyByUid").post(requestBody)
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

    public void postPostInfoData(Long pid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取帖子详情
                JSONObject json = new JSONObject();
                try {
                    json.put("pid", pid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/post/getPostInformationByPid").post(requestBody)
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

                    mHandlerForPostInformation.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}