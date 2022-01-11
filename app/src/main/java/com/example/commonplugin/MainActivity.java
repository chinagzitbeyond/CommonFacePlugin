package com.example.commonplugin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.cloud.huiyansdkface.sampledemo.FaceVerifyDemoActivity;

public class MainActivity extends AppCompatActivity {

    private String userId = "WbFaceVerifyAll111111";

    //呼拉兔
//    private String secret = "sObqiFiSu4cxwvXkETakEdh58MAqcU8UoZRmUKrlTkHnSGISwqWL02h0eGDDgWBZ";
//    private String appId = "IDASKxzd";
//    private String licence = "Obc8pfTeccNIENqCK47E+w8mq0jPHIuHs/a2UrCf+3qj2EBPC5LEzTixK5CdQcpkjU0gVNZ2II78S7j9WC6XIfQpGHPW5gRn0kJDGDEC4KkVw6Ahv9c3loYcQ8zK1G7y2qIRHRIHMNz6nVaOwxIIgVX+jQOJJ+eDuNNIAdYwVKYBMY23jBjz1e6ELBDOf8cM4MA2JFz5eTCA9Mll5tWg84fOv8FFDH6cYAZma3TzowrQxEwBJkepwpyo1JbGvg9H/zIQxsJkcDP6EbZYy+tOtjTLAdUAX9l+abh3GWN5axyW1armqQPPKBrKL70oaXYeE/mb7zETwz3eS0bz47jysg==";
    //大尾狐
    private String secret = "DRK24ZMm9qS9QrSnZeTw4hwWLL45oUmjzQJz3cN7UO9wFSO0rwJFhkk2YhlSpP71";
    private String appId = "IDA2FSs0";
    private String licence = "EZesLWS+9KfTAWMeDclUJh8Yzd2ggkFmkfgmiVVdqWkndO3GwPSvYosS/nJdJpfGqjX3QeEym/hqScTULJzwi8ng8uFFWLiaMvmpTARaAaKOBtAeLIgGZTyIMlXh+Deb352F1U5s5YMzGgPYWTPwCCJON6/KU7xCuVlpWdfrZTkbxZHs5QVxG6yeFLbMdwlOkJxZ+QOMkX13RlmAYEsGdACp1jl2Oj+mn3au8tSBB5nKgkElu6ADFEsX4PnkSEgbmX3p1dDrDMVGlX71804fZ0tUtEybQlOYUNGMEvfH+1M9Fw03/5T0jCzuxcE5A1dBIPKw22yPNQ3533gzn6zgXw==";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,FaceVerifyDemoActivity.class);
        intent.putExtra("secret",secret);
        intent.putExtra("appId",appId);
        intent.putExtra("licence",licence);
        intent.putExtra("userId",userId);

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