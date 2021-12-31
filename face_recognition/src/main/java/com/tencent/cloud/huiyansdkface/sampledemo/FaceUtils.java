package com.tencent.cloud.huiyansdkface.sampledemo;

import android.content.Context;
import android.content.Intent;

public class FaceUtils {

    String faceStr = null;
    public String getFaceStr(Context context, FaceVerifyDemoActivity faceVerifyDemoActivity){

        context.startActivity(new Intent(context,faceVerifyDemoActivity.getClass()));
        new FaceVerifyDemoActivity.FaceCallBackStr() {
            @Override
            public String getCallBackStr(String str) {
                faceStr = str;
                return str;
            };
        };
        return faceStr;
    }

}
