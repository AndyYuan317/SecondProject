package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tencent.bugly.beta.Beta;
import com.wingplus.coomohome.BuildConfig;
import com.wingplus.coomohome.Listener.WebBannerListener;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.TabGroup;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.fragment.FragmentFactory;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.CheckUtil;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.XiaoNengUtil;
import com.wingplus.coomohome.web.entity.WebBanner;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.AdvResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import static com.wingplus.coomohome.application.MallApplication.startPush;

/**
 * Mall主框架
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private FragmentPagerAdapter mFragmentAdapter;
    private int pageNumber = 4;//默认底部四个页签
    private int pageOffScreenLimit = 4;//viewpager缓存数量
    private TabGroup tabs;
    private View popup;

    private PopupWindow adPopup;
    private AdvResult secondAdv = null;
    private int advType = -1;
    private int advNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RuntimeConfig.SCREEN_WIDTH = DisplayUtil.getScreenWidth(this);

        if (RuntimeConfig.pushEnabled) {
            startPush();
        }

        initView();
        initEvent();
        initData();
        MallApplication.activity = this;

        Beta.initDelay = 1 * 1000;
        Beta.init(getApplicationContext(), BuildConfig.DEBUG);
        XiaoNengUtil.setHeadImg(MainActivity.this);
        XiaoNengUtil.login();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkNeedBindWxOrMobile();

        closeRegAdvIfUserValid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentFactory.clearFragment();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);//ps: move activity to back but not to destroyed.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean isMainActivity() {
        return true;
    }

    public void initView() {
        viewPager = findViewById(R.id.content);
        tabs = findViewById(R.id.tabs);
        tabs.updateSelected(0);
        RuntimeConfig.tabGroup = tabs;
    }

    private void initEvent() {
        viewPager.setAdapter(mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pageOffScreenLimit);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = (BaseFragment) mFragmentAdapter.getItem(position);
                tabs.updateSelected(position);//monkey测试发现index错乱
//                fragment.setStatusBarColor(MainActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs.setOnTabChangeListener(new TabGroup.TabSelectListener() {
            @Override
            public boolean onTabSelected(int index) {
                viewPager.setCurrentItem(index, false);
                return true;
            }
        });
    }

    private void initData() {
        checkNeedShowHomeAdv();

        checkNeedShowRegAdv();
    }

    /**
     * 用户已经处于登录状态时关闭广告
     */
    private void closeRegAdvIfUserValid() {
        if (RuntimeConfig.userValid()) {
            if (advType == Constant.BannerType.REG && adPopup != null && adPopup.isShowing()) {
                advType = -1;
                adPopup.dismiss();
                adPopup = null;
            }
            checkNeedShowRegAdv();
        }
    }

    /**
     * 检查是否需要弹出首页广告
     * 每次启动后只弹出一次
     */
    private void checkNeedShowHomeAdv() {
        if (RuntimeConfig.FIRST_STARTUP) {
            RuntimeConfig.FIRST_STARTUP = false;
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HOME_ADV),
                            new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("address", RuntimeConfig.USER_ADDRESS)
                                    .getParams());
                    final AdvResult advResult = GsonUtil.fromJson(resultString, AdvResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            showAd(advResult);
                        }
                    }, 1000);
                }
            });


        }
    }

    /**
     * 检查是否需要展示注册提醒广告
     * 安装后只提示一次
     */
    private void checkNeedShowRegAdv() {
        if (RuntimeConfig.FIRST_INSTALLED_OPEN) {
            if (RuntimeConfig.userValid()){
                RuntimeConfig.setInstalled();
            }
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HOME_REG_ADV),
                            new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("address", RuntimeConfig.USER_ADDRESS)
                                    .getParams());
                    final AdvResult advResult = GsonUtil.fromJson(resultString, AdvResult.class);
                    if (advResult != null && advResult.getData() != null) {
                        if (!RuntimeConfig.userValid()) {
                            advResult.getData().setType(Constant.BannerType.REG);
                        } else {
                            advResult.getData().setType(Constant.BannerType.COUPON);
                        }
                    }
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            showAd(advResult);
                        }
                    });
                }
            });
        }
    }

    /**
     * 检查是否需要绑定手机号或者微信号
     * 只提醒一次
     */
    private void checkNeedBindWxOrMobile() {
        if (RuntimeConfig.userValid()) {

            //判断是第一次登录则继续
            String string = AndroidFileUtil.readFileByLines(MallApplication.getContext().getFilesDir().getAbsolutePath() + "/" + DataConfig.BIND_TIP_FILE_NAME);
            if ("loginTimes".equals(string)) {
                return;
            } else {
                AndroidFileUtil.writeStringToFile("loginTimes", MallApplication.getContext().getFilesDir().getAbsolutePath(), DataConfig.BIND_TIP_FILE_NAME);
            }

            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String resultString = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COMMON_REG_NOTIFICATION), null);
                    final StringDataResult tipResult = GsonUtil.fromJson(resultString, StringDataResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            String mobile = RuntimeConfig.user.getMobile();
                            String weixinId = RuntimeConfig.user.getWeixinId();

                            String tip = "为了确保您第一时间收到订单信息，请绑定**";
                            if (tipResult != null && tipResult.getResult() == APIConfig.CODE_SUCCESS) {
                                tip = tipResult.getMessage();
                            }
                            if (mobile == null || !CheckUtil.isMobileNO(mobile)) {
                                //todo 接口获取提示语，提示绑定手机
                                tip = tip.replaceAll("\\*\\*", "手机");
                            } else if (weixinId == null || weixinId.length() == 0) {
                                //todo 接口获取提示语，提示绑定微信
                                tip = tip.replaceAll("\\*\\*", "微信");
                            }

                            new AlertDialog.Builder(MainActivity.this, R.style.AlertSelf).setTitle(tip)
//                        .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton("马上前往", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 前往个人资料页面
                                            Intent intent = new Intent(MainActivity.this, MineProfileActivity.class);
                                            intent.putExtra("bind", true);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);

                                        }
                                    })
                                    .setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_close:
                cancelPopup();
                break;
            default:
                break;
        }
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) { //be called when no exit_y cache pager
            return FragmentFactory.createFragment(MallApplication.getPostion(position));
        }

        @Override
        public int getCount() {
            return pageNumber + (MallApplication.hasPromotion ? 1 : 0);
        }

        public BaseFragment getNowFragment() {
            return null;
        }
    }


    /**
     * --------------广告框-----------------
     */
    private void showPopup() {
//        if (!"WelcomeActivity".equals(getIntent().getStringExtra("from"))) return;
        if (adPopup.isShowing()) {
            adPopup.dismiss();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adPopup.showAtLocation(popup, Gravity.BOTTOM, 0, 0);
            }
        }, 1000);
    }

    private void cancelPopup() {
        adPopup.dismiss();
        adPopup = null;
        popup = null;

        advNumber++;
        //展示了两个，则清空待展示广告，避免循环。
        if (advNumber >= 2) {
            secondAdv = null;
        }

        if (secondAdv != null) {
            showAd(secondAdv);
        }
    }

    /**
     * 展示首页广告
     *
     * @param advResult
     */
    private synchronized void showAd(AdvResult advResult) {
        //已经在展示一个，则存储另一个。
        if (adPopup != null) {
            secondAdv = advResult;
            return;
        }

        if (advResult != null && advResult.getResult() == APIConfig.CODE_SUCCESS) {
            initAd(advResult.getData());
            showPopup();
        } else {
            //因为是广告部分，所以加载失败不给予提示
        }
    }

    //初始化 |广告框|
    private void initAd(WebBanner adv) {
        popup = getLayoutInflater().inflate(R.layout.popup_home_ad, null);
        adPopup = new PopupWindow(popup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //adPopup.setAnimationStyle(R.style.popupAnim);
        adPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        initDraw(adv);
    }

    //设置数据
    private void initDraw(final WebBanner adv) {
        advType = adv.getType();

        popup.findViewById(R.id.popup_close).setOnClickListener(MainActivity.this);
        ImageView advImage = popup.findViewById(R.id.popup_ad);
        GlideUtil.GlideWithPlaceHolder(MainActivity.this, adv.getImgUrl()).into(advImage);
        advImage.setOnClickListener(WebBannerListener.getListener(MainActivity.this, adv.getType(), adv.getParams()));
    }

}
