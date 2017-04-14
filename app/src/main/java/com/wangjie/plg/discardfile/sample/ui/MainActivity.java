package com.wangjie.plg.discardfile.sample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.R;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEt;
    private EditText passwordEt;

    /**
     * method body params:
     * http://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#alter
     */
    @Override
    @Discard(srcCode = "{super.onCreate($1); System.out.println(\"this: \" + $0);}")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEt = (EditText) findViewById(R.id.activity_main_username_et);
        passwordEt = (EditText) findViewById(R.id.activity_main_password_et);
        setTestAccount();
    }

    @Discard(srcCode = "{System.out.println(\"hello world isIncremental = true\");}")
    private void setTestAccount() {
        usernameEt.setText("wangjie");
        passwordEt.setText("111111");
    }

}
