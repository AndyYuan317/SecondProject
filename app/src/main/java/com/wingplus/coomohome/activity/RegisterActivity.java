package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.CheckUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.JPushUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.WeixinUtil;
import com.wingplus.coomohome.util.XiaoNengUtil;
import com.wingplus.coomohome.web.result.StringDataResult;
import com.wingplus.coomohome.web.entity.User;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.LoginResult;

/**
 * 注册页
 *
 * @author leaffun.
 *         Create on 2017/9/27.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private boolean canGetCode = true;
    private boolean alreadyGetCode = false;

    private Handler handler;
    private Runnable runnable;
    private int sec;

    private TextView getCode;
    private EditText phoneEdit;
    private EditText verificationEdit;
    private String mobile;
    private boolean isGoMain = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        getCode = findViewById(R.id.get_verification);
        phoneEdit = findViewById(R.id.phone);
        verificationEdit = findViewById(R.id.verification);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                try {
                    isGoMain = getIntent().getStringExtra("back").equals("goMain");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isGoMain) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.login:
//                转登录
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.wechat:
                WeixinUtil.wxLogin();
                break;
            case R.id.register:
                checkThenRegister();
                break;
            case R.id.get_verification:
                sendVerificationCode();
                break;
            case R.id.protocol:
                Intent pro = new Intent(getApplicationContext(), RegularActivity.class);
                pro.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_PROTOCOL);
                startActivity(pro);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            default:
                break;
        }
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
        sec = 60;
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    LogUtil.i("sec", sec + "");
                    getCode.setText(sec + "s");
                    sec -= 1;

                    if (sec >= 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        canGetCode = true;
                        getCode.setText("获取验证码");
                        runnable = null;
                        handler = null;
                    }
                }
            };
        }
        handler.post(runnable);
    }

    private void checkThenRegister() {
        if (!alreadyGetCode) {
            ToastUtil.toast(this, "请先获取验证码");
            return;
        }
        if (!checkPhone()) {
            return;
        }

        doRegisterTask();
    }

    private void doRegisterTask() {
        LogUtil.i(getClass().getSimpleName(), "手机注册");

        mobile = phoneEdit.getText().toString().trim();
        final String code = verificationEdit.getText().toString().trim();

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_REGISTER),
                        new ParamsBuilder()
                                .addParam("mobile", mobile)
                                .addParam("smsId", RuntimeConfig.VALID_CODE_PREFIX)
                                .addParam("validCode", code)
                                .getParams());
                LoginResult loginResult = GsonUtil.fromJson(resultString, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    APIUtil.saveToken(loginResult.getData());
                    JPushUtil.pushRegistrationID(RuntimeConfig.user.getToken());
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("from", "LoginActivity");
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    finish();
                } else {
                    ToastUtil.toastByCode(loginResult);
                }
            }
        });
    }
}
