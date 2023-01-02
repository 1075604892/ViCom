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
import com.vicom.frontend.activity.ReplyListActivity;
import com.vicom.frontend.entity.Community;
import com.vicom.frontend.entity.Post;
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

public class PostListFragment extends Fragment {
    private List<Post> posts = new ArrayList<Post>();
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    private View view;

    private int type = 0;
    private static Long targetUid;

    public PostListFragment() {
        // Required empty public constructor
        type = 0;
    }

    public PostListFragment(int type) {
        this.type = type;
    }

    public PostListFragment(int type, Long uid) {
        this.type = type;
        targetUid = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post_list, container, false);

        //加载我的发帖内容
        Long uid = (Long) DBManger.getInstance(getContext()).selectCookie().get("uid");
        if (type == 1 && uid != -1) {
            postMyPostsData(uid);
        }else if(type == 2){
            postMyPostsData(targetUid);
        }

        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_post_list, null);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.usernameTv.setText(posts.get(position).getUsername());
            holder.contentTv.setText(posts.get(position).getContent());
            holder.titleTv.setText(posts.get(position).getTitle());
            holder.timeTv.setText(posts.get(position).getReleaseTime());

            String url = posts.get(position).getPicUrl();
            if (url != null && !url.equals("")) {
                String[] urls = url.split(" ");
                for (int i = 0; i < urls.length; i++) {
                    holder.imageViews[i].setVisibility(View.VISIBLE);
                    holder.imageViews[i].setImageURL(MyConfiguration.HOST + "/" + urls[i]);
                }
            }

            System.out.println(MyConfiguration.HOST + "/" + posts.get(position).getIconUrl());
            holder.userIconView.setImageURL(MyConfiguration.HOST + "/" + posts.get(position).getIconUrl());

            holder.itemPostView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ReplyListActivity.class);
                intent.putExtra("pid", posts.get(position).getId());
                intent.putExtra("content", posts.get(position).getTitle() + "\n" + posts.get(position).getContent());
                intent.putExtra("picUrl", posts.get(position).getPicUrl());
                intent.putExtra("username", posts.get(position).getUsername());
                intent.putExtra("iconUrl", posts.get(position).getIconUrl());

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
        TextView timeTv;
        MyImageView[] imageViews = new MyImageView[3];
        MyImageView userIconView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTv = itemView.findViewById(R.id.post_username);
            titleTv = itemView.findViewById(R.id.post_title);
            contentTv = itemView.findViewById(R.id.post_content);
            itemPostView = itemView.findViewById(R.id.item_post_tab);
            timeTv = itemView.findViewById(R.id.id_post_releaseTime);

            imageViews[0] = itemView.findViewById(R.id.image1);
            imageViews[1] = itemView.findViewById(R.id.image2);
            imageViews[2] = itemView.findViewById(R.id.image3);

            userIconView = itemView.findViewById(R.id.user_image_1);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mRecyclerView = view.findViewById(R.id.post_list);

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
                    post.setReleaseTime(jsonObject.getString("releaseTime"));
                    post.setIconUrl(jsonObject.getString("iconUrl"));

                    posts.add(post);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!posts.isEmpty()) {
                TextView tvTitle = ((TextView) view.findViewById(R.id.tv_title));
                tvTitle.setText("相关帖子");
                tvTitle.setVisibility(View.VISIBLE);
            }

            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };

    public void cleanPostList() {
        posts.clear();
    }

    public void postMyPostsData(Long uid) {
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
                            .url(MyConfiguration.HOST + "/post/queryPostsByUid").post(requestBody)
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

    public void postSearchPostsData(String name) {
        posts.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送请求");
                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/post/search").post(requestBody)
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