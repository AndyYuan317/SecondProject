package com.wingplus.coomohome.application;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.pingplusplus.android.Pingpp;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wingplus.coomohome.component.ProgressDialog;
import com.wingplus.coomohome.config.BuildConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.XiaoNengUtil;

import cn.jpush.android.api.DefaultPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import static com.wingplus.coomohome.util.MapUtil.startLockMe;

/**
 * @author leaffun.
 *         Create on 2017/8/29.
 */
public class MallApplication extends MultiDexApplication {
    public static Activity activity;
    private static MallApplication application;
    private static Handler handler;
    private static int mainThreadId;
    private Context context;
    public static IWXAPI mWxApi;
    private static ProgressDialog progressDialog;
    private static boolean shows;


    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myPid();

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        //初始化第三方SDK
        boolean equals = getContext().getPackageName().equals(UIUtils.getCurrentProcessName());
        if (equals) {
            initThirdAspect();
        }

        //设置默认用户
        RuntimeConfig.setDefaultUser();


        //开始定位
        startLockMe();

        //7.0适配
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initThirdAspect() {
        //腾讯bugly
        buglySetting();

        //注册微信登录
        registerToWX();


        //极光IM初始化
//        JMessageClient.setDebugMode(com.wingplus.coomohome.BuildConfig.DEBUG);
//        JMessageClient.init(this);

        //极光推送初始化
        JPushInterface.setDebugMode(com.wingplus.coomohome.BuildConfig.DEBUG);
        JPushInterface.init(this);


        //在使用百度地图SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this);

        //ping++开启调试模式
        Pingpp.DEBUG = com.wingplus.coomohome.BuildConfig.DEBUG;

        setJPushNotificationView();

        XiaoNengUtil.init();
    }

    private void buglySetting() {
        Beta.autoInit = false;//在mainActivity中检查
        Bugly.init(getApplicationContext(), "db88b25e1b", com.wingplus.coomohome.BuildConfig.DEBUG);
    }

    private void registerToWX() {
        mWxApi = WXAPIFactory.createWXAPI(this, BuildConfig.Weixin.APP_ID, false);
        mWxApi.registerApp(BuildConfig.Weixin.APP_ID);
    }


    private void setJPushNotificationView() {
//        CustomPushNotificationBuilder builder = new
//                CustomPushNotificationBuilder(context,
//                R.layout.customer_notitfication_layout,
//                R.id.icon,
//                R.id.title,
//                R.id.text);
//        // 指定定制的 Notification Layout
//        builder.statusBarDrawable = R.drawable.your_notification_icon;
//        // 指定最顶层状态栏小图标
//        builder.layoutIconDrawable = R.drawable.your_2_notification_icon;
//        // 指定下拉状态栏时显示的通知图标
//        JPushInterface.setPushNotificationBuilder(2, builder);
        JPushInterface.setDefaultPushNotificationBuilder(new DefaultPushNotificationBuilder());
    }

    /**
     * 开启推送
     */
    public static void startPush() {
        if (JPushInterface.isPushStopped(getContext())) {
            JPushInterface.resumePush(getContext());
        }
    }

    /**
     * 关闭推送
     */
    public static void stopPush() {
        JPushInterface.stopPush(getContext());
    }

    /**
     * 获取到本应用的对象
     *
     * @return
     */
    public static MallApplication getApplication() {
        return application;
    }

    /**
     * 获取上下文
     */
    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * 获取消息处理器
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * 获取主线程id
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static String getApplicationId() {
        return MallApplication.getApplication().getPackageName();
    }

    /*首页页签展示*/
    public static boolean hasPromotion = true;

    /**
     * 获取首页Fragment实例需要的position
     *
     * @param position
     * @return
     */
    public static int getPostion(int position) {
        if (!MallApplication.hasPromotion && position > 1) position += 1;
        return position;
    }

    /*首页视频代理缓存*/
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MallApplication app = (MallApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheFilesCount(2)
                .maxCacheSize(1024 * 1024 * 100)//最多100M
                .build();
    }

    public static void showProgressDialog(final boolean show, final Activity activity) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                shows = show;
                if ((progressDialog == null || !progressDialog.isShowing()) && activity != null) {
                    progressDialog = new ProgressDialog(activity);
                }
                if (progressDialog != null) {
                    if (shows) {
                        progressDialog.show();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

}
