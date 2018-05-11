package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;

/**
 * 邀请好友
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class InviteFriendActivity extends BaseActivity implements View.OnClickListener {

    private ImageView qrCodeImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        initView();
        initData();
    }

    private void initView() {
        qrCodeImg = findViewById(R.id.qrCode);
    }

    private void initData() {
        if (RuntimeConfig.userValid()) {
            String qrCode = RuntimeConfig.user.getQrCode();
            if (qrCode != null && !qrCode.isEmpty()) {
                GlideUtil.GlideWithPlaceHolder(InviteFriendActivity.this, qrCode).into(qrCodeImg);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.invite_friend:
                Intent qrInt = new Intent(InviteFriendActivity.this, AlertActivity.class);
                startActivity(qrInt);
                overridePendingTransition(R.anim.dialog_enter,R.anim.dialog_exit);
                break;
//            case R.id.regular:
//                Intent regular = new Intent(getApplicationContext(), RegularActivity.class);
//                regular.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_INVITED);
//                startActivity(regular);
//                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
