package com.vicom.frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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
import com.vicom.frontend.fragment.CommunityListFragment;
import com.vicom.frontend.fragment.FindFragment;
import com.vicom.frontend.fragment.ListFragment;
import com.vicom.frontend.fragment.OptionFragment;
import com.vicom.frontend.fragment.TalkFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Fragment talkFragment = new TalkFragment();
    private static Fragment listFragment = new ListFragment();
    private static Fragment findFragment = new FindFragment();
    private static Fragment optionFragment = new OptionFragment();

    private LinearLayout talkLinear;
    private LinearLayout listLinear;
    private LinearLayout findLinear;
    private LinearLayout optionLinear;

    private ImageButton talkButton;
    private ImageButton listButton;
    private ImageButton findButton;
    private ImageButton optionButton;

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

    public void hid(){
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

    private static void resetTab(FragmentTransaction fragmentTransaction) {
        setTitle("聊吧");
        fragmentTransaction.hide(talkFragment);
        fragmentTransaction.hide(listFragment);
        fragmentTransaction.hide(findFragment);
        fragmentTransaction.hide(optionFragment);
    }

    public static void setTitle(String text) {
        title.setText(text);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}