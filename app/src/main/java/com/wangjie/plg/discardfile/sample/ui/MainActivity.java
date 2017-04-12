package com.wangjie.plg.discardfile.sample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.R;

public class MainActivity extends AppCompatActivity {

    @Override
    @Discard
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
