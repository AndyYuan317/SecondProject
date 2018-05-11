package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.CheckUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.JPushUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.WeixinUtil;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.LoginResult;
import com.wingplus.coomohome.web.result.StringDataResult;

/**
 * 绑定
 *
 * @author leaffun.
 *         Create on 2017/12/14.
 */
public class BindActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneEdit;
    private EditText ver;
    private TextView getVer;
    private String mobile;
    private int sec;
    private boolean canGetCode = true;
    private boolean alreadyGetCode = false;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bind);
        initView();
    }


    private void initView() {
        phoneEdit = findViewById(R.id.phone);
        ver = findViewById(R.id.verification);
        getVer = findViewById(R.id.get_verification);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.get_verification:
                sendVerificationCode();
                break;
            case R.id.bind:
                checkThenBind();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    /**
     * 校验手机号，验证码
     */
    private void checkThenBind() {
        if (!alreadyGetCode) {
            ToastUtil.toast(this, "请先获取验证码");
            return;
        }
        if (!checkPhone()) return;
        doBindTask();
    }

    private void doBindTask() {
        mobile = phoneEdit.getText().toString().trim();
        final String code = ver.getText().toString().trim();

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_BIND_MOBILE),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("mobile", mobile)
                                .addParam("smsId", RuntimeConfig.VALID_CODE_PREFIX)
                                .addParam("validCode", code)
                                .getParams());
                LoginResult loginResult = GsonUtil.fromJson(result, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    RuntimeConfig.user = loginResult.getData();
                }
                ToastUtil.toastByCode(loginResult);
                finish();

            }
        });
    }

    private boolean checkPhone() {
        mobile = phoneEdit.getText().toString().trim();
        if (!CheckUtil.isMobileNO(mobile)) {
            ToastUtil.toast(this, "请输入正确的手机号");
            return false;
        }
        return true;
    }

    private void sendVerificationCode() {
        if (canGetCode && checkPhone()) {
            RuntimeConfig.sendValidCodeTime = System.currentTimeMillis();
            sec = 60;
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COMMON_SEND_VALID_CODE),
                            new ParamsBuilder()
                                    .addParam("phone", mobile)
                                    .getParams());
                    final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                alreadyGetCode = true;
                                RuntimeConfig.setValidCodePrefix(result.getData());
                                getCodeDownSec();
                                ToastUtil.toast(getApplicationContext(), "发送成功");
                            } else {
                                RuntimeConfig.sendValidCodeTime = 0;
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });

                }
            });
        }
    }

    private void getCodeDownSec() {
        canGetCode = false;
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    LogUtil.i("sec", sec + "");
                    getVer.setText(sec + "s");
                    sec -= 1;

                    if (sec >= 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        canGetCode = true;
                        getVer.setText("获取验证码");
                        runnable = null;
                        handler = null;
                    }
                }
            };
        }
        handler.post(runnable);
    }
}
