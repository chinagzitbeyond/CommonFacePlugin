package com.example.commonplugin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.cloud.huiyansdkface.sampledemo.FaceVerifyDemoActivity;

public class MainActivity extends AppCompatActivity {

    private String secret;
    private String appId;
    private String licence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,FaceVerifyDemoActivity.class);
        intent.putExtra("secret",secret);
        intent.putExtra("appId",appId);
        intent.putExtra("licence",licence);

        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            if(requestCode == 1){
                String userImageStr = data.getStringExtra("userImageStr");
                Log.e(MainActivity.class.getName(),"刷脸特征："+ userImageStr);

            }
        }

    }
}