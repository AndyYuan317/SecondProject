package com.wingplus.coomohome.config;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.TabGroup;
import com.wingplus.coomohome.fragment.main.CartFragment;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.BadgeUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.entity.User;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.CartNumResult;
import com.wingplus.coomohome.web.weixin.AccessToken;
import com.wingplus.coomohome.web.weixin.WeixinUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class RuntimeConfig {

    private static int pushNumber = 0;

    public static boolean pushEnabled = true;

    public static boolean FIRST_STARTUP = true;

    public static boolean FIRST_INSTALLED_OPEN = false;

    public static User user = new User();

    public static String VALID_CODE_PREFIX = "";

    public static int SCREEN_WIDTH = 720;

    public static LatLng myLocation = null;

    public static LatLng storeSelected = null;

    /**
     * 记录上次登录发送短信时间，
     * 当前时间不超过记录时间 60s ，则不允许再次发送
     */
    public static long sendValidCodeTime = 0;

    public static String searchWord = "";

    public static List<Category> mainCate = null;

    /**
     * 微信相关
     */
    public static AccessToken accessToken = null;

    public static WeixinUser weixinUser = null;

    public static LatLng USER_ADDRESS_LATLNG = null;

    public static String USER_ADDRESS = "上海";

    public static TabGroup tabGroup = null;

    public static List<CartFragment> cartFragments = new ArrayList<>();

    static {
        //RuntimeConfig在Application初始化调用一次，执行静态代码块，
        setDefaultUser();

        checkInstallOpen();
    }

    /**
     * 检查是否安装后第一次打开APP,是第一次则记录为false
     */
    private static void checkInstallOpen() {
        String string = AndroidFileUtil.readFileByLines(MallApplication.getContext().getFilesDir().getAbsolutePath() + "/" + DataConfig.INSTALLED_OPEN_FILE_NAME);

        FIRST_INSTALLED_OPEN = !"false".equals(string);
    }

    public static void setInstalled(){
        if(FIRST_INSTALLED_OPEN){
            AndroidFileUtil.writeStringToFile("false",MallApplication.getContext().getFilesDir().getAbsolutePath(), DataConfig.INSTALLED_OPEN_FILE_NAME);
            FIRST_INSTALLED_OPEN = false;
        }
    }

    public static boolean userValid() {
        boolean b = user.getToken() != null && user.getUserName() != null && user.getLvName() != null && user.getToken().length() > 0 && user.getUserName().length() > 0;
        return b;
    }

    public static void setDefaultUser() {
        user.setToken("");
    }

    public static void setValidCodePrefix(String prefix) {
        VALID_CODE_PREFIX = prefix;
    }

    public static void notifyCartNum() {
        if (RuntimeConfig.tabGroup != null) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_CNT)
                            , new ParamsBuilder()
                                    .addParam("token", user.getToken())
                                    .getParams());
                    final CartNumResult result = GsonUtil.fromJson(rs, CartNumResult.class);
                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                tabGroup.updateNum(3, result.getData().getCnt());
                            }
                        });

                    }
                }
            });
        }
    }


    public static void notifyCartGoods() {
        if (cartFragments != null && cartFragments.size() > 0) {
            for (CartFragment cartFragment : cartFragments) {
                cartFragment.refreshData();
            }
        }
    }

    /**
     * 注册购物车
     *
     * @param cart
     */
    public static void registerCartFragment(CartFragment cart) {
        if (cart != null && !cartFragments.contains(cart)) {
            cartFragments.add(cart);
        }
    }

    /**
     * 注销购物车
     *
     * @param cart
     */
    public static void unRegisterCartFragment(CartFragment cart) {
        if (cart != null && cartFragments.contains(cart)) {
            cartFragments.remove(cart);
        }
    }

    public static void setPushEnabled(boolean enabled) {
        pushEnabled = enabled;
        if (RuntimeConfig.pushEnabled) {
            MallApplication.startPush();
        } else {
            MallApplication.stopPush();
        }

        String absolutePath = MallApplication.getContext().getCacheDir().getAbsolutePath();
        AndroidFileUtil.writeStringToFile(String.valueOf(RuntimeConfig.pushEnabled), absolutePath, DataConfig.PUSH_FILE_NAME);
    }

    public static boolean setStoreSelected(LatLng latLng) {
        storeSelected = new LatLng(latLng.latitude, latLng.longitude);
        return true;
    }

    public static void setPushNumber(Context context, int i) {
        if (i == -1) {
            pushNumber += i;
        } else {
            pushNumber = i;
        }

        if (pushNumber < 0) {
            pushNumber = 0;
        }
        BadgeUtil.setBadge(context, pushNumber);
    }
}
