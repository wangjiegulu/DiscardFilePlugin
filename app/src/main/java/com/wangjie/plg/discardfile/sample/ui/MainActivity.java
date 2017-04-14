package com.wangjie.plg.discardfile.sample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.R;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEt;
    private EditText passwordEt;

    @Override
//    @Discard
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEt = (EditText) findViewById(R.id.activity_main_username_et);
        passwordEt = (EditText) findViewById(R.id.activity_main_password_et);
        setTestAccount();
    }

    @Discard
    private void setTestAccount(){
        usernameEt.setText("wangjie");
        passwordEt.setText("111111");
    }

}
