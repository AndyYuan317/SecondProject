package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.StringDataResult;

/**
 * 意见反馈
 *
 * @author leaffun.
 *         Create on 2017/10/14.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private EditText feedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.send:
                sendFeedBack();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initView() {
        feedback = findViewById(R.id.feedback);
    }

    private void sendFeedBack() {
        final String s = feedback.getText().toString().trim();
        if (s.trim().length() > 0) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_FEEDBACK)
                            , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("content", s)
                                    .getParams());
                    final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("发送成功");
                                finish();
                                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });
                }
            });
        } else {
            ToastUtil.toast(getApplicationContext(), "请输入您的意见和建议");
        }

    }
}
