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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.activity.PostListActivity;
import com.vicom.frontend.entity.Community;
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

public class CommunityListFragment extends Fragment {
    private View view;

    //聊天列表
    private List<Community> communities = new ArrayList<Community>();

    private int type = 0;

    public CommunityListFragment() {
        type = 0;
    }

    public CommunityListFragment(int type) {
        this.type = type;
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

        view = inflater.inflate(R.layout.fragment_community_list, container, false);

        if (type == 0) {
            JSONObject json = new JSONObject();
            try {
                json.put("id", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            postFollowCommunitiesData(json);
        }

        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.item_normal_community_list, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.mTitleTv.setText(communities.get(position).getName());
            holder.contentTv.setText(communities.get(position).getDescription());
            holder.imageView.setImageURL(MyConfiguration.HOST + "/" + communities.get(position).getCover_path());
            holder.item.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PostListActivity.class);
                intent.putExtra("cid", communities.get(position).getCid());
                intent.putExtra("name", communities.get(position).getName());
                intent.putExtra("description", communities.get(position).getDescription());
                intent.putExtra("cover", communities.get(position).getCover_path());
                intent.putExtra("followNum", communities.get(position).getFollowNum());
                intent.putExtra("isFollowed", communities.get(position).getIsFollowed());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return communities.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTv;
        MyImageView imageView;
        TextView contentTv;
        LinearLayout item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.normal_community_name);
            imageView = itemView.findViewById(R.id.normal_community_image);
            contentTv = itemView.findViewById(R.id.normal_community_content);
            item = itemView.findViewById(R.id.community_tab);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //System.out.println("主线程收到子线程处理消息的结果");
            mRecyclerView = view.findViewById(R.id.favor_community_list);

            String str = msg.obj.toString().replace("[", "").replace("]", "");
            String[] result = str.split("\\},");
            for (int i = 0; i < result.length; i++) {
                if (i < result.length - 1) {
                    result[i] += "}";
                }
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    Community community = new Community();
                    community.setName(jsonObject.getString("name"));
                    community.setCid(jsonObject.getString("id"));
                    community.setDescription(jsonObject.getString("description"));
                    community.setCover_path(jsonObject.getString("cover"));
                    community.setFollowNum(jsonObject.getString("followNum"));
                    community.setIsFollowed(jsonObject.getString("isFollowed"));
                    communities.add(community);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (!communities.isEmpty()) {
                TextView tvTitle = ((TextView) view.findViewById(R.id.tv_title));
                if (type == 0) {
                    tvTitle.setText("关注社区");
                } else {
                    tvTitle.setText("相关社区");
                }
                tvTitle.setVisibility(View.VISIBLE);
            }

            mMyAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mMyAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    };

    public void postFollowCommunitiesData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/community/favoriteCommunities").post(requestBody)
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

    public void postSearchCommunitiesData(String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                    json.put("uid", DBManger.getInstance(getContext()).getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/community/search").post(requestBody)
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