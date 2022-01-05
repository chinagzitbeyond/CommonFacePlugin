package com.tencent.cloud.huiyansdkface.sampledemo;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.cloud.huiyansdkface.wehttp2.WeLog;
import com.tencent.cloud.huiyansdkface.wehttp2.WeOkHttp;
import com.tencent.cloud.huiyansdkface.wehttp2.WeReq;

import java.io.IOException;
import java.io.Serializable;

public class TokenUseCase {

    private static final String TAG = "TokenUseCase";

    private AppHandler handler;

    public TokenUseCase(AppHandler handler) {
        this.handler = handler;
        initHttp();
    }

    private WeOkHttp myOkHttp = new WeOkHttp();

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(2000, 2000, 2000)
                //添加PIN
                .log(WeLog.Level.BODY);
    }

    public void execute(String appId, String secret, String grant_type, String version, String mode) {
        final String url = getUrl(appId, secret, grant_type,version);

        requestExec(url, new WeReq.Callback<TokenUseCase.TokenResponse>() {
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
            public void onSuccess(WeReq weReq, TokenUseCase.TokenResponse signResponse) {
                if (signResponse != null) {
                    String sign = signResponse.access_token;
                    processBody(mode, sign);
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

    private void processBody(String mode, String sign) {
        if (TextUtils.isEmpty(sign) || "null".equals(sign)) {
            handler.sendSignError(AppHandler.ERROR_DATA, "sign is null.");
        } else {
            Log.d(TAG, "签名请求成功:" + sign);
            handler.sendTokenSuccess(mode,sign);
        }
    }

    public static class TokenResponse implements Serializable {
        public String access_token;
    }

    public void requestExec(String url, WeReq.Callback<TokenUseCase.TokenResponse> callback) {
        myOkHttp.get(url).execute(callback);
    }

    private String getUrl(String appId, String secret, String grant_type, String version) {
        //特别注意：此方法仅供demo使用，合作方开发时需要自己的后台提供接口生成签名
        final String s = "https://miniprogram-kyc.tencentcloudapi.com/api/oauth2/access_token?app_id="+appId+"&secret="+secret+"&grant_type="+grant_type+"&version="+version;
        Log.d(TAG, "get token url=" + s);
        return s;
    }



}
