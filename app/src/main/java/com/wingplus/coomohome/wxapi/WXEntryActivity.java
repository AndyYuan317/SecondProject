package com.wingplus.coomohome.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.BuildConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.web.weixin.AccessToken;
import com.wingplus.coomohome.web.weixin.WeixinUser;

import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_AUTH_DENIED;
import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_OK;
import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_USER_CANCEL;

/**
 * 微信登录响应
 *
 * @author leaffun.
 *         Create on 2017/10/26.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        try {
            MallApplication.mWxApi.handleIntent(getIntent(), this);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtil.i("微信启动", "onNewIntent");
        MallApplication.mWxApi.handleIntent(intent, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.i("微信请求", "发起");
    }

    @Override
    public void onResp(final BaseResp baseResp) {
        LogUtil.i("微信返回码", ((SendAuth.Resp) baseResp).errCode + "");
        final boolean isLogin = BuildConfig.Weixin.state.equals(((SendAuth.Resp) baseResp).state);
        final boolean isBind = BuildConfig.Weixin.state_bind.equals(((SendAuth.Resp) baseResp).state);
        switch (((SendAuth.Resp) baseResp).errCode) {
            case ERR_OK:
                if (!(isLogin || isBind)) {
                    ToastUtil.toast("微信授权失败");
                    return;
                } else {
                    break;
                }
            case ERR_USER_CANCEL:
                ToastUtil.toast("用户取消");
                finish();
                return;
            case ERR_AUTH_DENIED:
            default:
                finish();
                return;
        }


        final String code = ((SendAuth.Resp) baseResp).code;

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDateFromNetByGet(BuildConfig.Weixin.ACCESS_TOKEN_URL_GET.replace("CODE", code), null);

                final AccessToken accessToken = GsonUtil.parseJson(rs, AccessToken.class);

                if (accessToken != null && accessToken.getErrcode() == null) {
                    RuntimeConfig.accessToken = accessToken;
                    final String userRs = HttpUtil.GetDateFromNetByGet(BuildConfig.Weixin.USER_INFO_URL_GET
                                    .replace("ACCESS_TOKEN", RuntimeConfig.accessToken.getAccess_token())
                                    .replace("OPENID", RuntimeConfig.accessToken.getOpenid())
                            , null);


                    WeixinUser weixinUser = GsonUtil.parseJson(userRs, WeixinUser.class);
                    if (weixinUser != null && weixinUser.getErrcode() == null) {
                        LogUtil.i(getClass().getSimpleName(), "微信授权成功");
                        RuntimeConfig.weixinUser = weixinUser;
                        if (isBind) {
                            APIUtil.bindWeixin(WXEntryActivity.this);
                        } else if (isLogin) {
                            APIUtil.socialLogin(WXEntryActivity.this);
                        }
                    } else {
                        ToastUtil.toast("微信授权失败");
                    }

                } else {
                    ToastUtil.toast("微信授权失败");
                }
            }
        });

        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
