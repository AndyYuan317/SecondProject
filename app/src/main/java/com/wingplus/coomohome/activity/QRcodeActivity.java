package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;

/**
 * 二维码页面
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class QRcodeActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initView();
    }

    private void initView() {
        ImageView back = findViewById(R.id.back);
        ImageView qrCode = findViewById(R.id.qrCode);
        TextView des = findViewById(R.id.des);
//        des.setText("二维码描述");

        GlideUtil.GlideWithPlaceHolder(QRcodeActivity.this, RuntimeConfig.user.getQrCode()).into(qrCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
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
}
