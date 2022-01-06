package com.tencent.cloud.huiyansdkface.sampledemo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tencent.cloud.huiyansdkface.facelight.api.WbCloudFaceContant;
import com.tencent.cloud.huiyansdkface.facelight.api.WbCloudFaceVerifySdk;
import com.tencent.cloud.huiyansdkface.facelight.api.listeners.WbCloudFaceVerifyLoginListener;
import com.tencent.cloud.huiyansdkface.facelight.api.listeners.WbCloudFaceVerifyResultListener;
import com.tencent.cloud.huiyansdkface.facelight.api.result.WbFaceError;
import com.tencent.cloud.huiyansdkface.facelight.api.result.WbFaceVerifyResult;
import com.tencent.cloud.huiyansdkface.facelight.process.FaceVerifyStatus;
import com.tencent.cloud.huiyansdkface.wehttp2.WeLog;
import com.tencent.cloud.huiyansdkface.wehttp2.WeOkHttp;
import com.tencent.cloud.huiyansdkface.wehttp2.WeReq;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FaceVerifyDemoActivity extends Activity {
    private static final String TAG = "FaceVerifyDemoActivity";
    private static final int SETTING_ACTIVITY = 2;

    //此处防重复点击时间间隔为demo模拟，合作方可根据自己情况确定间隔时间
    private static final long CLICK_GAP = 1000;
    private long clickTime = 0;

    private Button faceVerifyReflect;
    private EditText nameEt;
    private EditText idNoEt;
    private TextView sitEnv;
    private ImageView settingIv;

    private ProgressDialog progressDlg;

    private boolean isShowSuccess;
    private boolean isShowFail;
    private boolean isRecordVideo;
    private boolean isPlayVoice;
    private String color;
    private String language;

    private AppHandler appHandler;
    private TokenUseCase tokenUseCase;
    private AuthUseCase authUseCase;
    private SignUseCase signUseCase;

    private String name;
    private String id;
    private String secret;
    private String grant_type = "client_credential";
    private String version = "1.0.0";
    private String appId;
    private String type = "SIGN";


    //此处为demo模拟，请输入标识唯一用户的userId
    private String userId = "WbFaceVerifyAll" + System.currentTimeMillis();
    //此处为demo模拟，请输入32位随机数
    private String nonce = "52014832029547845621032584562012";
    //此处为demo使用，由合作方提供包名申请，统一下发
    private String licence;
    private String compareType;
    private String token;
    private String model;
    private String order = "testReflect" + System.currentTimeMillis();
    private String face;
    private String userImageString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Intent intent = getIntent();
        this.secret = intent.getStringExtra("secret");
        this.appId = intent.getStringExtra("appId");
        this.licence = intent.getStringExtra("licence");



        appHandler = new AppHandler(this);
        tokenUseCase = new TokenUseCase(appHandler);
        authUseCase = new AuthUseCase(appHandler);
        signUseCase = new SignUseCase(appHandler);

        initViews();
        initHttp();
        setListeners();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                8);
    }

    private void initViews() {
        initProgress();

        faceVerifyReflect = (Button) findViewById(R.id.faceVerifyLight);
        nameEt = (EditText) findViewById(R.id.et_name);
        idNoEt = (EditText) findViewById(R.id.et_idNo);

        settingIv = (ImageView) findViewById(R.id.wbcf_demo_setting);
        sitEnv = (TextView) findViewById(R.id.sit_env_logo);

        

    }

    private void initProgress() {
        if (progressDlg != null) {
            progressDlg.dismiss();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            progressDlg = new ProgressDialog(this);
        } else {
            progressDlg = new ProgressDialog(this);
            progressDlg.setInverseBackgroundForced(true);
        }
        progressDlg.setMessage("加载中...");
        progressDlg.setIndeterminate(true);
        progressDlg.setCanceledOnTouchOutside(false);
        progressDlg.setCancelable(true);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setCancelable(false);
    }

    private void setListeners() {
        sitEnv.setText("v4.4.1.2");

        //默认简体中文
        language = WbCloudFaceContant.LANGUAGE_ZH_CN;
        //默认选择白色模式
        color = WbCloudFaceContant.WHITE;
        //默认不展示成功/失败页面
        isShowSuccess = false;
        isShowFail = false;
        //默认不录制视频
        isRecordVideo = false;
        //默认不播放提示语
        isPlayVoice = false;
        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceContant.ID_CRAD
        //仅活体检测  WbCloudFaceContant.NONE
        //默认公安网纹图片对比
        compareType = WbCloudFaceContant.ID_CARD;

        settingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle data = new Bundle();
                data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, isShowSuccess);
                data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, isShowFail);
                data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, isRecordVideo);
                data.putBoolean(WbCloudFaceContant.PLAY_VOICE, isPlayVoice);
                data.putString(WbCloudFaceContant.COMPARE_TYPE, compareType);
                data.putString(WbCloudFaceContant.COLOR_MODE, color);
                data.putString(WbCloudFaceContant.LANGUAGE, language);
                intent.putExtras(data);
                intent.setClass(FaceVerifyDemoActivity.this, SettingActivity.class);
                startActivityForResult(intent, SETTING_ACTIVITY);
            }
        });

        faceVerifyReflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建议对拉起人脸识别按钮做防止重复点击的操作
                //避免用户快速点击导致二次登陆，二次拉起刷脸等操作
                if (System.currentTimeMillis() - clickTime < CLICK_GAP) {
                    Log.e(TAG, "duplicate click faceGradeFaceId!");
                    return;
                }
                checkOnId(AppHandler.DATA_MODE_DESENSE);
            }
        });
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

    public void getAuth(String mode,String sign){
        Log.e(TAG, "mode:"+mode+":token:"+token);
        openCloudFaceService(FaceVerifyStatus.Mode.GRADE, appId, order, sign, face);
    }

    public void getToken(String mode,String token){

        this.token = token;
        this.model = mode;
        signUseCase.execute(mode, appId, token, type, version,nonce,userId);

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

    //特别注意：此方法仅供demo使用，合作方开发时需要自己的后台提供接口获得faceId
    public void getFaceId(final FaceVerifyStatus.Mode mode, final String sign) {
        Log.d(TAG, "start getFaceId");

        final String order = "testReflect" + System.currentTimeMillis();
        //此处为demo使用体验，实际生产请使用控制台给您分配的appid
//        final String appId = "TIDAacCm";


        if (compareType.equals(WbCloudFaceContant.NONE)) {
            Log.d(TAG, "仅活体检测不需要faceId，直接拉起sdk");
            openCloudFaceService(mode, appId, order, sign, "");
            return;
        }

//        String url = "https://miniprogram-kyc.tencentcloudapi.com/api/server/getAdvFaceId?orderNo="+order;
        String url = "https://miniprogram-kyc.tencentcloudapi.com/api/server/getfaceid";

        Log.d(TAG, "get faceId url=" + url);

        GetFaceId.GetFaceIdParam param = new GetFaceId.GetFaceIdParam();
        param.orderNo = order;
        param.webankAppId = appId;
        param.version = "1.0.0";
        param.userId = userId;
        param.sign = sign;
        param.sourcePhotoType = "1";
        param.sourcePhotoStr = "";
        param.nonce = nonce;

        if (compareType.equals(WbCloudFaceContant.ID_CARD)) {
            Log.d(TAG, "身份证对比" + url);
            param.name = name;
            param.idNo = id;
        }
        Log.e("AppHandler", "请求:[" + param.toJson() + "]," );
        GetFaceId.requestExec(myOkHttp, url, param, new WeReq.Callback<GetFaceId.GetFaceIdResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, WeReq.ErrType errType, int i, String s, IOException e) {
                progressDlg.dismiss();
                Log.d(TAG, "faceId请求失败:code=" + i + ",message=" + s);
                Toast.makeText(FaceVerifyDemoActivity.this, "登录异常(faceId请求失败:code=" + i + ",message=" + s + ")", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(WeReq weReq, GetFaceId.GetFaceIdResponse getFaceIdResponse) {
                if (getFaceIdResponse != null) {
                    String code = getFaceIdResponse.code;
                    if (code.equals("0")) {
                        GetFaceId.Result result = getFaceIdResponse.result;
                        if (result != null) {
                            String faceId = result.faceId;
                            if (!TextUtils.isEmpty(faceId)) {
                                Log.d(TAG, "faceId请求成功:" + faceId);
//                                openCloudFaceService(mode, appId, order, sign, faceId);
                                face = faceId;
                                authUseCase.execute(model,appId,token,"NONCE",version,nonce,userId);

                            } else {
                                progressDlg.dismiss();
                                Log.e(TAG, "faceId为空");
                                Toast.makeText(FaceVerifyDemoActivity.this, "登录异常(faceId为空)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDlg.dismiss();
                            Log.e(TAG, "faceId请求失败:getFaceIdResponse result is null.");
                            Toast.makeText(FaceVerifyDemoActivity.this, "登录异常(faceId请求失败:getFaceIdResponse result is null)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDlg.dismiss();
                        Log.e(TAG, "faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg);
                        Toast.makeText(FaceVerifyDemoActivity.this, "登录异常(faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg + ")", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDlg.dismiss();
                    Log.e(TAG, "faceId请求失败:getFaceIdResponse is null.");
                    Toast.makeText(FaceVerifyDemoActivity.this, "登录异常(faceId请求失败:getFaceIdResponse is null)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void checkOnId(String mode) {
        //此处为demo使用体验，实际生产请使用控制台给您分配的appId
//        String appId = "TIDAacCm";
        String appId = "IDASKxzd";

        if (compareType.equals(WbCloudFaceContant.ID_CARD)) {
            name = nameEt.getText().toString().trim();
            id = idNoEt.getText().toString().trim();
            if (name != null && name.length() != 0) {
                if (id != null && id.length() != 0) {
                    if (id.contains("x")) {
                        id = id.replace('x', 'X');
                    }

                    IdentifyCardValidate vali = new IdentifyCardValidate();
                    String msg = vali.validate_effective(id);
                    if (msg.equals(id)) {
                        Log.i(TAG, "Param right!");
                        Log.i(TAG, "Called Face Verify Sdk MODE=" + mode);
                        progressDlg.show();
                        tokenUseCase.execute(appId, secret, grant_type, version,mode);
                    } else {
                        Toast.makeText(FaceVerifyDemoActivity.this, "用户证件号错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(FaceVerifyDemoActivity.this, "用户证件号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(FaceVerifyDemoActivity.this, "用户姓名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            //自带源和活体对比不检测姓名 身份证
            Log.i(TAG, "No need check Param!");
            name = "";
            id = "";
            Log.i(TAG, "Called Face Verify Sdk!" + mode);
            progressDlg.show();
            tokenUseCase.execute(appId, secret, grant_type, version,mode);
        }
    }


    //拉起刷脸sdk
    public void openCloudFaceService(FaceVerifyStatus.Mode mode, String appId, String order, String sign, String faceId) {
        Log.d(TAG, "openCloudFaceService");
        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                faceId,
                order,
                appId,
                "1.0.0",
                nonce,
                userId,
                sign,
                mode,
                licence);
        Log.e(TAG,faceId+":"+order+":"+appId+":"+"1.0.0"+nonce+":"+userId+":"+sign+":"+mode+":"+licence);

        data.putSerializable(WbCloudFaceContant.INPUT_DATA, inputData);
        data.putString(WbCloudFaceContant.LANGUAGE, language);
        //是否展示刷脸成功页面，默认不展示
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, isShowSuccess);
        //是否展示刷脸失败页面，默认不展示
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, isShowFail);
        //颜色设置,sdk内置黑色和白色两种模式，默认白色
        //如果客户想定制自己的皮肤，可以传入WbCloudFaceContant.CUSTOM模式,此时可以配置ui里各种元素的色值
        //定制详情参考app/res/colors.xml文件里各个参数
        data.putString(WbCloudFaceContant.COLOR_MODE, color);
        //是否需要录制上传视频 默认不需要
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, isRecordVideo);
        //是否播放提示音，默认不播放
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, isPlayVoice);
        //识别阶段合作方定制提示语,可不传，此处为demo演示
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_LIVE, "人脸识别使用!");
        //上传阶段合作方定制提示语,可不传，此处为demo演示
        data.putString(WbCloudFaceContant.CUSTOMER_TIPS_UPLOAD, "人脸识别使用!");
        //合作方长定制提示语，可不传，此处为demo演示
        //如果需要展示长提示语，需要邮件申请
        data.putString(WbCloudFaceContant.CUSTOMER_LONG_TIP, "人脸识别使用！");
        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceContant.ID_CRAD
        //仅活体检测  WbCloudFaceContant.NONE
        //默认公安网纹图片比对
        data.putString(WbCloudFaceContant.COMPARE_TYPE, compareType);
        //sdk log开关，默认关闭，debug调试sdk问题的时候可以打开
        //【特别注意】上线前请务必关闭sdk log开关！！！
        data.putBoolean(WbCloudFaceContant.IS_ENABLE_LOG, true);

        Log.d(TAG, "WbCloudFaceVerifySdk initSdk");
        WbCloudFaceVerifySdk.getInstance().initSdk(FaceVerifyDemoActivity.this, data, new WbCloudFaceVerifyLoginListener() {
            @Override
            public void onLoginSuccess() {
                //登录sdk成功
                Log.i(TAG, "onLoginSuccess");
                progressDlg.dismiss();

                //拉起刷脸页面
                WbCloudFaceVerifySdk.getInstance().startWbFaceVerifySdk(FaceVerifyDemoActivity.this, new WbCloudFaceVerifyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        //得到刷脸结果
                        if (result != null) {
                            if (result.isSuccess()) {
                                userImageString = result.getUserImageString();
                                Log.d(TAG, "刷脸成功! Sign=" + result.getSign() + "; liveRate=" + result.getLiveRate() +
                                        "; similarity=" + result.getSimilarity() + "userImageString=" + userImageString);
                                if (!isShowSuccess) {
                                    Toast.makeText(FaceVerifyDemoActivity.this, "刷脸成功", Toast.LENGTH_SHORT).show();
                                    //跳转到主界面Activity中
                                    Intent intent = new Intent();
                                    intent.putExtra("userImageStr",userImageString);
                                    setResult(2,intent);
                                    finish();
                                }
                            } else {
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    Log.d(TAG, "刷脸失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        Log.d(TAG, "对比失败，liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity());
                                    }
                                    if (!isShowSuccess) {
                                        Toast.makeText(FaceVerifyDemoActivity.this, "刷脸失败!" + error.getDesc(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Log.e(TAG, "sdk返回error为空！");
                                }
                            }
                        } else {
                            Log.e(TAG, "sdk返回结果为空！");
                        }
                        //测试用代码
                        //不管刷脸成功失败，只要结束了，都更新userId
                        Log.d(TAG, "更新userId");
                        userId = "WbFaceVerifyREF" + System.currentTimeMillis();
                        //刷脸结束后，释放资源
                        WbCloudFaceVerifySdk.getInstance().release();

                    }
                });
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                //登录失败
                Log.i(TAG, "onLoginFailed!");
                progressDlg.dismiss();
                if (error != null) {
                    Log.d(TAG, "登录失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        Toast.makeText(FaceVerifyDemoActivity.this, "传入参数有误！" + error.getDesc(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FaceVerifyDemoActivity.this, "登录刷脸sdk失败！" + error.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "sdk返回error为空！");
                }
            }
        });
    }

    public void hideLoading() {
        if (progressDlg != null) {
            progressDlg.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTING_ACTIVITY) {
            isShowSuccess = data.getBooleanExtra(WbCloudFaceContant.SHOW_SUCCESS_PAGE, false);
            isShowFail = data.getBooleanExtra(WbCloudFaceContant.SHOW_FAIL_PAGE, false);
            isRecordVideo = data.getBooleanExtra(WbCloudFaceContant.VIDEO_UPLOAD, false);
            isPlayVoice = data.getBooleanExtra(WbCloudFaceContant.PLAY_VOICE, false);
            compareType = data.getStringExtra(WbCloudFaceContant.COMPARE_TYPE);
            color = data.getStringExtra(WbCloudFaceContant.COLOR_MODE);
            language = data.getStringExtra(WbCloudFaceContant.LANGUAGE);
            Log.d(TAG, "setting language=" + language);

            if (compareType.equals(WbCloudFaceContant.NONE)) {
                nameEt.setText("");
                idNoEt.setText("");
            }
        }
    }




}
