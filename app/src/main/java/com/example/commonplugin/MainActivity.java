package com.example.commonplugin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.cloud.huiyansdkface.sampledemo.FaceVerifyDemoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,FaceVerifyDemoActivity.class));
    }
}