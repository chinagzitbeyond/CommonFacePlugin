package com.example.commonplugin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.cloud.huiyansdkface.sampledemo.FaceVerifyDemoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,FaceVerifyDemoActivity.class));
        FaceVerifyDemoActivity.FaceCallBackStr faceCallBackStr = new FaceVerifyDemoActivity.FaceCallBackStr() {
            @Override
            public String getCallBackStr(String str) {
                Log.d(MainActivity.class.getName(),str);
                return str;
            }
        };

    }
}