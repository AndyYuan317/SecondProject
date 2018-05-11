package com.wingplus.coomohome.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.WeixinUtil;

/**
 * 弹出窗
 *
 * @author leaffun.
 *         Create on 2017/12/14.
 */
public class AlertActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alert);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
            case R.id.cancel:
                finish();
                break;
            case R.id.session:
                inviteByWx(SendMessageToWX.Req.WXSceneSession);
                finish();
                break;
            case R.id.timeline:
                inviteByWx(SendMessageToWX.Req.WXSceneTimeline);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    /**
     * 通过微信分享
     *
     * @param type
     */
    private void inviteByWx(final int type) {
        if (RuntimeConfig.userValid()) {
            String qrCode = RuntimeConfig.user.getQrCode();
            if (qrCode != null && !qrCode.isEmpty()) {
                Glide.with(AlertActivity.this).asBitmap().load(qrCode).into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {


                        Bitmap bitmap = drawBitmap(resource);
                        WeixinUtil.wxShareImg(bitmap, type);
                    }
                });
            }
        }
    }

    /**
     * 绘制图片
     *
     * @param bmp
     * @return
     */
    private Bitmap drawBitmap(Bitmap bmp) {
        Bitmap newb = null;
        try {
            Bitmap bg = decodeResource(getResources(), R.drawable.invite_share_bg);
            Bitmap qrCodeBmp = Bitmap.createScaledBitmap(bmp, 540, 540, false);
            int width = bg.getWidth();
            int height = bg.getHeight();
            int qrCodeBmpWidth = qrCodeBmp.getWidth();
            int qrCodeBmpHeight = qrCodeBmp.getHeight();
            LogUtil.e("AlertActivity", "w:" + width + "h:" + height + "[=====qr]" + "w:" + qrCodeBmpWidth + "h:" + qrCodeBmpHeight);


            newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas cv = new Canvas(newb);

            Rect src = new Rect(0, 0, width, height);
            Rect des = new Rect(0, 0, width, height);
            cv.drawBitmap(bg, src, des, null);

            cv.drawBitmap(qrCodeBmp, width / 4, 646, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newb;
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
}
