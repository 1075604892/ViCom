package com.vicom.frontend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vicom.frontend.MyConfiguration;
import com.vicom.frontend.R;
import com.vicom.frontend.entity.SubPost;
import com.vicom.frontend.fragment.OptionFragment;
import com.vicom.frontend.sqlite.DBManger;
import com.vicom.frontend.view.MyImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_change_user_info);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        String username = intent.getStringExtra("username");
        String nickname = intent.getStringExtra("nickname");
        Integer sex = Integer.valueOf(intent.getStringExtra("sex"));
        String introduce = intent.getStringExtra("introduce");
        String icon = intent.getStringExtra("icon");
        String email = intent.getStringExtra("email");

        //修改UI
        ((TextView) findViewById(R.id.tvtitle)).setText("修改个人信息");

        ((TextView) findViewById(R.id.et_info_username)).setText(username);
        ((TextView) findViewById(R.id.et_info_nickname)).setText(nickname);
        ((TextView) findViewById(R.id.et_info_introduce)).setText(introduce);
        ((TextView) findViewById(R.id.et_info_email)).setText(email);
        ((MyImageView) findViewById(R.id.IV_user_icon_change)).setImageURL(MyConfiguration.HOST + "/" + icon);
        RadioGroup rgSex = findViewById(R.id.info_userSex);
        int count = rgSex.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton rb = (RadioButton) rgSex.getChildAt(i);
            if (i == sex) {
                rb.setChecked(true);
                break;
            }
        }


        //隐藏密码框
        ((EditText) findViewById(R.id.et_info_pass_old)).setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((EditText) findViewById(R.id.et_info_pass)).setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((EditText) findViewById(R.id.et_info_pass_repeat)).setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void submitChange(View view) {
        String email = ((EditText) findViewById(R.id.et_info_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_info_pass)).getText().toString();
        String passwordRepeat = ((EditText) findViewById(R.id.et_info_pass_repeat)).getText().toString();

        String nickname = ((EditText) findViewById(R.id.et_info_nickname)).getText().toString();
        //获取性别
        RadioGroup rgSex = findViewById(R.id.info_userSex);
        int count = rgSex.getChildCount();
        int sex = 2;
        for (int i = 0; i < count; i++) {
            RadioButton rb = (RadioButton) rgSex.getChildAt(i);
            if (rb.isChecked()) {
                sex = i;
                break;
            }
        }


        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
            json.put("nickname", nickname);
            json.put("sex", sex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postChangeData(json);
    }

    public void quit(View view) {
        onBackPressed();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //System.out.println("主线程收到子线程处理消息的结果");
            Toast.makeText(ChangeActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();

            if (msg.what == 1) {
                //注册成功，退出注册界面
                if (msg.obj.toString().equals("修改成功") && imagePath != null) {
                    postIcon(imagePath);
                } else if (msg.obj.toString().equals("修改头像成功")) {
                    onBackPressed();
                } else if (msg.obj.toString().equals("修改成功")) {
                    onBackPressed();
                }
            }
        }

        ;
    };

    private static final int REQUEST_CODE = 1;
    String imagePath;

    public void uploadIcon(View view) {
        System.out.println("上传用户头像");
        Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, REQUEST_CODE); // 打开系统相册
    }

    //从选择照片的界面返回后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                // 把指定Uri的图片复制一份到内部存储空间，并返回存储路径
                imagePath = saveImage(intent.getData());
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

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ImageView pic = findViewById(R.id.IV_user_icon_change);

            pic.setImageBitmap(bitmap);
            pic.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("图片路径:" + imagePath);

        return imagePath;
    }

    public void postChangeData(JSONObject json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, json.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(MyConfiguration.HOST + "/user/change").post(requestBody)
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

    public void postIcon(String imagePath) {
        String username = ((EditText) findViewById(R.id.et_info_username)).getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    RequestBody bodyParams = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

                    OkHttpClient client = new OkHttpClient();

                    MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);

                    File file = new File(imagePath);

                    requestBodyBuilder.addFormDataPart("icon", file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file));

                    requestBodyBuilder.addFormDataPart("user", "", bodyParams);

                    RequestBody requestBody = requestBodyBuilder.build();

                    Request request = new Request.Builder()
                            .url(MyConfiguration.HOST + "/user/")
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
                    }

                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
