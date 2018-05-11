package com.wingplus.coomohome.util;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.web.result.BaseResult;

/**
 * 吐司工具类
 *
 * @author leaffun.
 *         Create on 2017/9/6.
 */
public class ToastUtil {


    private static Toast toast;

    private static void toast(final Context context, final String text, final boolean ifLong) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, "toast", Toast.LENGTH_SHORT);
                    toast = new Toast(context);
                    toast.setGravity(Gravity.BOTTOM, 0, UIUtils.dip2px(205));
                    View view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
                    toast.setView(view);

                }
                View view = toast.getView();
                TextView txt = view.findViewById(R.id.txt);
                String str = text == null ? "" : text;
                txt.setText(str);
                LogUtil.i("toastContent",str);
                if (ifLong) {
                    toast.setDuration(Toast.LENGTH_LONG);
                }
                toast.show();
            }
        });

    }

    /**
     * 需要长时间就添加第三个参数["long"]
     *
     * @param context
     * @param text
     */
    public static void toast(Context context, String... text) {
        toast(context, text[0], text.length > 1 && "long".equals(text[1]));
    }

    /**
     * 需要长时间就添加第三个参数[1]
     *
     * @param context
     * @param text
     */
    public static void toast(Context context, int... text) {
        toast(context, text[0] + "", text.length > 1 && text[1] == 1);
    }

    /**
     * 响应网络请求结果
     *
     * @param result
     */
    public static void toastByCode(BaseResult result) {
        if (result == null) {
            if (NetUtil.IsActivityNetWork(MallApplication.getContext())) {
                toast(MallApplication.getContext(), "服务器更新中,请稍后再试");
            } else {
                toast(MallApplication.getContext(), MallApplication.getContext().getString(R.string.word_please_check_network));
            }
            return;
        }

        switch (result.getResult()) {
            case APIConfig.CODE_ERR:
                toast(MallApplication.getContext(), result.getMessage() == null ? "请求失败，请稍后再试" : result.getMessage());
                break;
            case APIConfig.CODE_SUCCESS:
                toast(MallApplication.getContext(), result.getMessage() == null ? "请求成功" : result.getMessage());
                break;
            case APIConfig.CODE_AUTHENTICATION_ERR:
                toast(MallApplication.getContext(), "请先登录");
                if(RuntimeConfig.userValid()) {
                   gotoReLogin();
                }
                break;
            case APIConfig.CODE_PARAMETER_LOST:
                toast(MallApplication.getContext(), "参数丢失，请重新登录");
                break;
            case APIConfig.CODE_TOKEN_OUT_TIME:
                toast(MallApplication.getContext(), "登录超时,请重新登录");
                if(RuntimeConfig.userValid()) {
                    gotoReLogin();
                }
                break;
            case APIConfig.CODE_UNKNOWN_ERR:
                toast(MallApplication.getContext(), "服务器错误: 9999");
            default:
                toast(MallApplication.getContext(), result.getMessage() == null ? "未知错误" : result.getMessage());
                break;
        }
    }

    public static void toast(String string){
        toast(MallApplication.getContext(), string);
    }

    /**
     * 被挤出登录状态时，触发重登录操作
     */
    private static void gotoReLogin() {
        AndroidFileUtil.deleteToken();
        RuntimeConfig.setDefaultUser();
        RuntimeConfig.weixinUser = null;
        Intent login = new Intent(MallApplication.getContext(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        login.putExtra("back","goMain");
        MallApplication.getContext().startActivity(login);

        Intent exit = new Intent();
        exit.setAction("exit_app");
        MallApplication.getContext().sendBroadcast(exit);
    }
}
