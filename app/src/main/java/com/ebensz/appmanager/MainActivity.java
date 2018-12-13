package com.ebensz.appmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView showTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showTv = (TextView) findViewById(R.id.showTv);
        Intent intent = getIntent();
        if (intent != null) {
            String pushPage = intent.getStringExtra("PushPage");
            String listType = intent.getStringExtra("ListType");
            String listName = intent.getStringExtra("ListName");
            showTv.setText(pushPage + " ==  " + listType + " == " + listName);
        }

    }

}
