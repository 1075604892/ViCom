package com.vicom.frontend.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.vicom.frontend.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏本来的标题框
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_register);


    }
}