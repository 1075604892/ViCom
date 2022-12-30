package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.entity.Post;
import com.vicom.frontend.sqlite.DBManger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostListActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<Post>();
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    private String name;
    private Long cid;

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
        name = intent.getStringExtra("name");
        cid = Long.valueOf(intent.getStringExtra("cid"));

        //修改UI
        ((TextView) findViewById(R.id.tvtitle)).setText(name);
        findViewById(R.id.id_jump_to_add_post).setVisibility(View.VISIBLE);

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
                intent.putExtra("content", posts.get(position).getTitle() + "\n" + posts.get(position).getContent());
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
        if (findViewById(R.id.id_add_post_tab).getVisibility() == View.VISIBLE) {
            findViewById(R.id.post_list_by_community).setVisibility(View.VISIBLE);
            findViewById(R.id.id_jump_to_add_post).setVisibility(View.VISIBLE);
            findViewById(R.id.id_add_post_tab).setVisibility(View.GONE);
            mPathList.clear();
        } else {
            onBackPressed();
        }
    }

    public void addPostTab(View view) {
        System.out.println("发帖" + "cid: " + cid + ", 社区名: " + name);
        findViewById(R.id.post_list_by_community).setVisibility(View.GONE);
        findViewById(R.id.id_jump_to_add_post).setVisibility(View.GONE);
        findViewById(R.id.id_add_post_tab).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private static final int REQUEST_CODE = 1;


    //选择照片
    public void choosePicture(View view) {
        System.out.println("上传照片");
        Intent albumIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, REQUEST_CODE); // 打开系统相册
    }

    private List<String> mPathList = new ArrayList<>(); // 头像文件的路径列表

    //从选择照片的界面返回后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                // 把指定Uri的图片复制一份到内部存储空间，并返回存储路径
                String imagePath = saveImage(intent.getData());
                mPathList.add(imagePath);
            }
        }
    }

    // 把指定Uri的图片复制一份到内部存储空间，并返回存储路径
    private String saveImage(Uri uri) {
        String[] filePathColumns = {MediaStore.Images.Media.DATA};

        // 到数据库中查询 , 查询 _data 列字段信息
        Cursor cursor = getContentResolver().query(
                uri,
                filePathColumns,
                null,
                null,
                null);
        cursor.moveToFirst();
        // 获取 _data 列所在的列索引
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        // 获取图片的存储路径
        String imagePath = cursor.getString(columnIndex);
        System.out.println("FilePath: " + imagePath);


        /*String uriStr = uri.toString();
        System.out.println("sdsadsadasdadsa:" + uriStr.toString());
        String imageName = uriStr.substring(uriStr.lastIndexOf("/") + 1);
        String imagePath = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), imageName);*/

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ImageView pic = findViewById(R.id.id_pic_1);

            switch (mPathList.size()) {
                case 1:
                    pic = findViewById(R.id.id_pic_2);
                    break;
                case 2:
                    pic = findViewById(R.id.id_pic_3);
                    break;
                case 3:
                    pic = findViewById(R.id.id_pic_4);
                    findViewById(R.id.id_pic_add).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

            pic.setImageBitmap(bitmap);
            pic.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("图片路径:" + imagePath);

        return imagePath;
    }

    public void submitPost(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("cid", cid);
                    json.put("uid", DBManger.getInstance(PostListActivity.this).getUid());
                    json.put("cookie", DBManger.getInstance(PostListActivity.this).getCookie());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //MediaType type = MediaType.parse("application/json;charset=utf-8");
                //RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    System.out.println("路径:" + mPathList.get(0));

                    File file = new File(mPathList.get(0));

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            // 此处可添加上传 参数
                            // photoFile 表示上传参数名,logo.png 表示图片名字
                            .addFormDataPart("images",file.getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"), file))//文件
                            .build();

                    Request request = new Request.Builder()
                            .url(MyConfiguration.HOST + "/post/")
                            .post(requestBody)
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
                        System.out.println("上传成功");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}