package com.tencent.cloud.huiyansdkface.sampledemo;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tencent.cloud.huiyansdkface.wehttp2.WeLog;
import com.tencent.cloud.huiyansdkface.wehttp2.WeOkHttp;
import com.tencent.cloud.huiyansdkface.wehttp2.WeReq;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthUseCase {
    private static final String TAG = "AuthUseCase";

    private AppHandler handler;

    public AuthUseCase(AppHandler handler) {
        this.handler = handler;
        initHttp();
    }

    private WeOkHttp myOkHttp = new WeOkHttp();

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(20, 20, 20)
                //添加PIN
                .log(WeLog.Level.BODY);
    }

    public void execute(String mode, String appId, String access_token, String type, String version, String nonceStr, String userId) {
        final String url = getUrl(appId, access_token, type, version,userId);

        requestExec(url, new WeReq.Callback<AuthUseCase.SignResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, WeReq.ErrType errType, int i, String s, IOException e) {
                requestError(i, s);
            }

            @Override
            public void onSuccess(WeReq weReq, AuthUseCase.SignResponse signResponse) {
                if (signResponse != null) {
                    String sign = signResponse.tickets[0].value;
                    processBody(mode, sign, appId, nonceStr, userId, version);
                } else {
                    requestError(AppHandler.ERROR_DATA, "get response is null");
                }
            }
        });
    }

    private void requestError(int code, String message) {
        Log.d(TAG, "签名请求失败:code=" + code + ",message=" + message);
        handler.sendSignError(code, message);
    }

    private void processBody(String mode, String ticket, String appId, String nonceStr, String userId, String version) {
        if (TextUtils.isEmpty(ticket) || "null".equals(ticket)) {
            handler.sendSignError(AppHandler.ERROR_DATA, "sign is null.");
        } else {
            Log.d(TAG, "签名请求成功:" + ticket);
            List<String> values = new ArrayList<String>();
            values.add(appId);
            values.add(userId);
            values.add(version);
            values.add(nonceStr);
            Log.d(TAG, "appId:" + appId + ":userId:" + userId + ":version:" + version + ":nonceStr:"+ nonceStr);
            String sign = sign(values, ticket);
            handler.sendAuthSuccess(mode, sign);
        }
    }

    public static class SignResponse implements Serializable {
        public AuthUseCase.Tickets[] tickets;     //签名
    }
    public static class Tickets implements Serializable{
        public String value;
    }

    public void requestExec(String url, WeReq.Callback<AuthUseCase.SignResponse> callback) {
        myOkHttp.get(url).execute(callback);
    }

    private String getUrl(String appId, String access_token, String type, String version, String userId) {
        //特别注意：此方法仅供demo使用，合作方开发时需要自己的后台提供接口生成签名
//        final String s = "https://miniprogram-kyc.tencentcloudapi.com" + "/ems-partner/cert/signature?appid=" + appId + "&nonce=" + nonce + "&userid=" + userId;
        final String s = "https://miniprogram-kyc.tencentcloudapi.com/api/oauth2/api_ticket?app_id="+appId+"&access_token="+access_token+"&type="+type+"&version="+version+"&user_id="+userId;
        Log.d(TAG, "get sign url=" + s);
        return s;
    }

    public static String sign(List<String> values, String ticket) {
        if (values == null) {
            throw new NullPointerException("values is null");
        }
        values.removeAll(Collections.singleton(null));// remove null
        values.add(ticket);
        java.util.Collections.sort(values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        return Hashing.sha1().hashString(sb, Charsets.UTF_8).toString().toUpperCase();
    }


}
