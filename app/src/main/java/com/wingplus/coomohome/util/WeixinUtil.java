package com.wingplus.coomohome.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.BuildConfig;

/**
 * @author leaffun.
 *         Create on 2017/10/31.
 */
public class WeixinUtil {


    public static void wxLogin() {

        if (!MallApplication.mWxApi.isWXAppInstalled()) {
            ToastUtil.toast("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = BuildConfig.Weixin.state;
        MallApplication.mWxApi.sendReq(req);
    }

    public static void wxBind() {

        if (!MallApplication.mWxApi.isWXAppInstalled()) {
            ToastUtil.toast("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = BuildConfig.Weixin.state_bind;
        MallApplication.mWxApi.sendReq(req);
    }

    public static void wxShareImg(Bitmap bitmap, int type) {
        shareBitmapToWx(bitmap, type);
    }


    private static void shareBitmapToWx(Bitmap bmp, int type) {
        try {
            if (bmp == null) {
                ToastUtil.toast("分享图片失败");
                return;
            }
            WXImageObject imageObject = new WXImageObject(bmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imageObject;

            Bitmap thumb = Bitmap.createScaledBitmap(bmp, 300, 300, true);
            bmp.recycle();
            msg.setThumbImage(thumb);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "img";
            req.message = msg;
            req.scene = type;

            MallApplication.mWxApi.sendReq(req);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
