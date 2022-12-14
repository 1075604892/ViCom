package com.vicom.frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vicom.frontend.R;
import com.vicom.frontend.activity.LoginActivity;
import com.vicom.frontend.activity.RegisterActivity;
import com.vicom.frontend.activity.ReplyListActivity;
import com.vicom.frontend.entity.Post;
import com.vicom.frontend.fragment.CommunityListFragment;
import com.vicom.frontend.fragment.FindFragment;
import com.vicom.frontend.fragment.ListFragment;
import com.vicom.frontend.fragment.OptionFragment;
import com.vicom.frontend.fragment.PostListFragment;
import com.vicom.frontend.fragment.TalkFragment;
import com.vicom.frontend.fragment.UserListFragment;
import com.vicom.frontend.sqlite.DBHelper;
import com.vicom.frontend.sqlite.DBManger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static TalkFragment talkFragment = new TalkFragment();
    private static Fragment listFragment = new ListFragment();
    public static FindFragment findFragment = new FindFragment();
    public static OptionFragment optionFragment = new OptionFragment();

    //次级Fragment
    private UserListFragment userListFragment = new UserListFragment();
    private PostListFragment postListFragment = new PostListFragment();
    public static CommunityListFragment communityListFragment = new CommunityListFragment(1);

    public static PostListFragment myPostListFragment = new PostListFragment(1);

    private LinearLayout talkLinear;
    private LinearLayout listLinear;
    private LinearLayout findLinear;
    private LinearLayout optionLinear;

    private ImageButton talkButton;
    private ImageButton listButton;
    private ImageButton findButton;
    private ImageButton optionButton;

    //数据库
    private DBHelper dbHelper;

    //标题
    private static TextView title;

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        //创建数据库
        dbHelper = new DBHelper(this, 1);
        dbHelper.getWritableDatabase();

        title = (TextView) findViewById(R.id.textView);

        //添加界面
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.id_content, talkFragment);
        fragmentTransaction.add(R.id.id_content, listFragment);
        fragmentTransaction.add(R.id.id_content, findFragment);
        fragmentTransaction.add(R.id.id_content, optionFragment);

        //fragment中的fragment
        fragmentTransaction.add(R.id.id_item_normal_community_list, new CommunityListFragment());
        fragmentTransaction.add(R.id.id_user_list, userListFragment);
        fragmentTransaction.add(R.id.id_community_list, communityListFragment);
        fragmentTransaction.add(R.id.id_post_list, postListFragment);

        fragmentTransaction.add(R.id.id_my_post_list, myPostListFragment);

        //结束

        resetTab(fragmentTransaction);

        fragmentTransaction.show(talkFragment);
        fragmentTransaction.commit();

        //选择视图
        talkLinear = (LinearLayout) findViewById(R.id.id_tab_talk);
        listLinear = (LinearLayout) findViewById(R.id.id_tab_list);
        findLinear = (LinearLayout) findViewById(R.id.id_tab_find);
        optionLinear = (LinearLayout) findViewById(R.id.id_tab_option);

        //选择按钮
        talkButton = (ImageButton) findViewById(R.id.id_tab_talk_img);
        listButton = (ImageButton) findViewById(R.id.id_tab_list_img);
        findButton = (ImageButton) findViewById(R.id.id_tab_find_img);
        optionButton = (ImageButton) findViewById(R.id.id_tab_option_img);

        talkLinear.setOnClickListener(this);
        listLinear.setOnClickListener(this);
        findLinear.setOnClickListener(this);
        optionLinear.setOnClickListener(this);
    }

    public void hid() {
        System.out.println("nini");
    }

    private static void changeTab(int i) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //将所有fragment关闭
        resetTab(fragmentTransaction);
        System.out.println("切换界面");
        switch (i) {
            case 0:
                fragmentTransaction.show(talkFragment);
                break;
            case 1:
                fragmentTransaction.show(listFragment);
                break;
            case 2:
                fragmentTransaction.show(findFragment);
                break;
            case 3:
                fragmentTransaction.show(optionFragment);
                break;
            default:
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_talk:
                changeTab(0);
                break;
            case R.id.id_tab_list:
                changeTab(1);
                break;
            case R.id.id_tab_find:
                changeTab(2);
                break;
            case R.id.id_tab_option:
                changeTab(3);
                break;
            default:
                break;
        }
    }


    public void registerTab(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginTab(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        System.out.println("登出");
        Long uid = (Long) DBManger.getInstance(MainActivity.this).selectCookie().get("uid");
        DBManger.getInstance(MainActivity.this).deleteCookie(uid);

        optionFragment.unLoginUI();
        talkFragment.unLoginUIAndHandle();
        findFragment.unLoginUIAndHandle();
    }

    private static void resetTab(FragmentTransaction fragmentTransaction) {
        setTitle("vicom");
        fragmentTransaction.hide(talkFragment);
        fragmentTransaction.hide(listFragment);
        fragmentTransaction.hide(findFragment);
        fragmentTransaction.hide(optionFragment);
    }

    public static void setTitle(String text) {
        title.setText(text);
    }

    public void search(View view) {
        String name = ((EditText) findViewById(R.id.et_search)).getText().toString();
        System.out.println("开始搜索内容:" + name);

        //隐藏搜索图片
        findViewById(R.id.id_unSearch_home).setVisibility(View.GONE);

        userListFragment.postSearchUserData(name);
        communityListFragment.postSearchCommunitiesData(name);
        postListFragment.postSearchPostsData(name);

        //收起小键盘
        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MainActivity.this.getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}