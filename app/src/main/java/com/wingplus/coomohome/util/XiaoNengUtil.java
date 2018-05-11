package com.wingplus.coomohome.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wingplus.coomohome.BuildConfig;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.XiaoNengChatActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;

import cn.xiaoneng.uiapi.EPlusFunctionType;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.uiapi.OnUnreadmsgListener;
import cn.xiaoneng.xpush.XPush;

/**
 * 小能客服
 * Created by leaffun on 2018/3/26.
 */
public class XiaoNengUtil {

    public static String SITEID = "kf_10173";// 企业id, 示例kf_9979,kf_8002,kf_3004,zf_1000,yy_1000
    public static String SDKKEY = "8a607658-64a9-4935-9ed3-0e02ba7258df";// 示例FB7677EF-00AC-169D-1CAD-DEDA35F9C07B

    public static String SETTINGID1 = "kf_10173_1521788787146";// 客服组id示例kf_9979_1452750735837
    public static String GROUP_NAME = "在线客服1";// 客服组默认名称

    public static String FUNC_NAME_ORDER = "最近订单";
    public static String FUNC_NAME_GOODS = "最近商品";

    /**
     * 小能客服初始化
     */
    public static void init() {
        boolean equals = MallApplication.getContext().getPackageName().equals(UIUtils.getCurrentProcessName());
        if (equals) {
            Ntalker.getBaseInstance().enableDebug(BuildConfig.DEBUG);//是否开启debug模式
            Ntalker.getBaseInstance().initSDK(MallApplication.getContext(), SITEID, SDKKEY);//初始化sdk, return 0 为正常
            setExtraFunc();//设置额外功能
            setXPush(); //设置XPush
        } else {
            LogUtil.e("小能初始化", "初始化发生在非主线程，放弃初始化");
        }
    }


    private static void setXPush() {
        XPush.setNotificationClickToActivity(MallApplication.getContext(), XiaoNengChatActivity.class);
        XPush.setNotificationShowIconId(MallApplication.getContext(), 0);
        XPush.setNotificationShowTitleHead(MallApplication.getContext(), MallApplication.getContext().getResources().getString(R.string.app_name));//getResources().getString(R.string.app_name)

//		XPush.enableHuaweiPush(getApplicationContext(), true);
//		XPush.setHuaweiPushParams(getApplicationContext(), "10556196");
//		XPush.enableXiaomiPush(getApplicationContext(), true);
//		XPush.setXiaomiPushParams(getApplicationContext(), getPackageName(), "2882303761517480753", "5641748066753");

    }

    public static void requestPermissions(Activity activity) {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        Ntalker.getExtendInstance().ntalkerSystem().requestPermissions(activity, permissions);
    }

    public static void login() {
        if (RuntimeConfig.userValid()) {
            Ntalker.getBaseInstance().login(RuntimeConfig.user.getUserCode(), RuntimeConfig.user.getUserName(), 0);
        }
    }

    public static void logout() {
        Ntalker.getBaseInstance().logout();
    }

    private static void setExtraFunc() {
        Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.DEFAULT_VIDEO);

        Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.SELFDEFINE, FUNC_NAME_ORDER, R.drawable.chat_order_style);

//        Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.SELFDEFINE, FUNC_NAME_GOODS, R.drawable.chat_goods_style);
    }

    public static void setHeadImg(Activity activity) {
        if (RuntimeConfig.userValid()) {
            Glide.with(activity).asBitmap().load(RuntimeConfig.user.getHeadImgUrl()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    Ntalker.getExtendInstance().settings().setUsersHeadIcon(resource);
                }
            });
        }
    }

    public static void setHeadImgCircle(boolean yesOrNo) {
        Ntalker.getExtendInstance().settings().setHeadIconCircle(MallApplication.getContext(), yesOrNo);
    }

    public static void setUnReadMsgListener(OnUnreadmsgListener listener){
        Ntalker.getExtendInstance().message().setOnUnreadmsgListener(listener);
    }
}
