package com.wingplus.coomohome.util;

import android.app.Activity;
import android.content.Intent;

import com.wingplus.coomohome.activity.MainActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.DataConfig;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.web.entity.User;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.LoginResult;

import cn.jpush.android.api.JPushInterface;

/**
 * @author leaffun.
 *         Create on 2017/11/2.
 */
public class APIUtil {

    /**
     * 加入购物车
     *
     * @param type      单品
     * @param id        商品编号
     * @param productId 货号
     * @param num       数量
     * @param ui        回调
     */
    public static void addCart(final int type, final long id, final String productId, final int num, final CallBack<BaseResult> ui) {
        if (!RuntimeConfig.userValid()) {
            ToastUtil.toast("请先登录");
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    BaseResult result = new BaseResult();
                    result.setResult(APIConfig.CODE_AUTHENTICATION_ERR);
                    ui.handleResult(result);
                }
            });
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_ADD),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("type", "" + type)
                                .addParam("id", "" + id)
                                .addParam("productId", productId)
                                .addParam("num", "" + num)
                                .getParams());
                final BaseResult baseResult = GsonUtil.fromJson(rs, BaseResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        RuntimeConfig.notifyCartNum();
                        RuntimeConfig.notifyCartGoods();
                        ui.handleResult(baseResult);
                    }
                });
            }
        });
    }

    public interface CallBack<T extends BaseResult> {
        void handleResult(T result);
    }

    /**
     * 社交账号登录
     */
    public static void socialLogin(final Activity activity) {
        final String weixinId = RuntimeConfig.weixinUser.getOpenid();
        final String userName = RuntimeConfig.weixinUser.getNickname();
        final String sex = String.valueOf(RuntimeConfig.weixinUser.getSex());
        final String headImgUrl = RuntimeConfig.weixinUser.getHeadimgurl();

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_WEIXIN),
                        new ParamsBuilder()
                                .addParam("weixinId", weixinId)
                                .addParam("nickName", userName)
                                .addParam("sex", sex)
                                .addParam("headImgUrl", headImgUrl)
                                .getParams());
                RuntimeConfig.weixinUser = null;
                LoginResult loginResult = GsonUtil.fromJson(rs, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    saveToken(loginResult.getData());
                    JPushUtil.pushRegistrationID(RuntimeConfig.user.getToken());
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("from", "LoginActivity");
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    ToastUtil.toastByCode(loginResult);
                }
            }
        });

    }

    /**
     * 保存用户登录信息
     *
     * @param user 用户
     */
    public static void saveToken(User user) {
        RuntimeConfig.user = user;
        String absolutePath = MallApplication.getContext().getCacheDir().getAbsolutePath();
        AndroidFileUtil.writeStringToFile("true", absolutePath, DataConfig.PUSH_FILE_NAME);
        AndroidFileUtil.writeStringToFile(user.getToken(), absolutePath, DataConfig.TOKEN_FILE_NAME);
        RuntimeConfig.FIRST_STARTUP = true;
        XiaoNengUtil.login();
    }

    public static void bindWeixin(final Activity activity) {
        final String weixinId = RuntimeConfig.weixinUser.getOpenid();

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_USER_BIND_WEIXIN),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("weixinId", weixinId)
                                .getParams());
                RuntimeConfig.weixinUser = null;
                LoginResult loginResult = GsonUtil.fromJson(rs, LoginResult.class);
                if (loginResult != null && loginResult.getResult() == APIConfig.CODE_SUCCESS) {
                    RuntimeConfig.user = loginResult.getData();
                }
                activity.finish();
                ToastUtil.toastByCode(loginResult);
            }
        });
    }
}
