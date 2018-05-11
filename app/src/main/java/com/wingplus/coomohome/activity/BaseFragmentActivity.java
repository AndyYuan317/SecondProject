package com.wingplus.coomohome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.util.NetUtil;
import com.wingplus.coomohome.util.ToastUtil;

/**
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class BaseFragmentActivity extends FragmentActivity {
    MyReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            registerBroadcast();
            if (!NetUtil.IsActivityNetWork(getApplicationContext())) {
                ToastUtil.toast("请检查您的网络连接");
                if (!isMainActivity()) {
                    finish();
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
        try {
            System.gc();
            System.runFinalization();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerBroadcast() {
        // 注册广播接收者
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("exit_app");
        registerReceiver(receiver, filter);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("exit_app")) {
                LogUtil.e("exit_app " + getClass().getSimpleName(), "退出登录");
                finish();
            }
        }
    }

    protected boolean isMainActivity() {
        return false;
    }
}
