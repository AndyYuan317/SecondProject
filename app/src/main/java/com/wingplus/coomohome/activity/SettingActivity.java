package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.XiaoNengUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * 设置
 *
 * @author leaffun.
 *         Create on 2017/10/13.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private SwitchCompat push;
    private ImageView headImg;
    private TextView logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.new_push:
                toggleNewsPush();
                break;
            case R.id.profile:
                if (RuntimeConfig.userValid()) {
                    Intent profile = new Intent(getApplicationContext(), MineProfileActivity.class);
                    startActivity(profile);
                } else {
                    Intent login = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(login);
                }
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.feedback:
                Intent intent = new Intent(getApplicationContext(), FeedBackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.about_us:
                Intent us = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(us);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.protocol:
                Intent pro = new Intent(getApplicationContext(), RegularActivity.class);
                pro.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_PROTOCOL);
                startActivity(pro);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.check_upgrade:
                Beta.checkUpgrade();
                break;
            case R.id.logout:
                XiaoNengUtil.logout();
                AndroidFileUtil.deleteToken();
                AndroidFileUtil.writeStringToFile("loginFirst", MallApplication.getContext().getFilesDir().getAbsolutePath(), DataConfig.BIND_TIP_FILE_NAME);
                RuntimeConfig.setDefaultUser();
                RuntimeConfig.weixinUser = null;
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.putExtra("back","goMain");
                startActivity(login);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);

                Intent exit = new Intent();
                exit.setAction("exit_app");
                sendBroadcast(exit);
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

    public void initView() {
        push = findViewById(R.id.new_push);
        headImg = findViewById(R.id.headImg);
        logout = findViewById(R.id.logout);
    }

    public void initData() {
        logout.setVisibility(RuntimeConfig.userValid() ? View.VISIBLE : View.GONE);

        if (RuntimeConfig.userValid() && RuntimeConfig.user.getHeadImgUrl() != null && RuntimeConfig.user.getHeadImgUrl().length() > 0) {
            GlideUtil.GlideInstance(SettingActivity.this, RuntimeConfig.user.getHeadImgUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.icon_unregisteredavatar))
                    .into(headImg);
        }
        push.setChecked(RuntimeConfig.pushEnabled);
    }

    /**
     * 推送开关
     */
    private void toggleNewsPush() {
        ToastUtil.toast(getApplicationContext(), push.isChecked() ? "开启推送" : "关闭推送");
        RuntimeConfig.setPushEnabled(push.isChecked());
    }

}
