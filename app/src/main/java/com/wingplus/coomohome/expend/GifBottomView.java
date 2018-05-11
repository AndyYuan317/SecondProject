package com.wingplus.coomohome.expend;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lcodecore.tkrefreshlayout.IBottomView;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.UIUtils;

/**
 * @author leaffun.
 *         Create on 2017/11/28.
 */
public class GifBottomView extends FrameLayout implements IBottomView {

    private Context context;
    private ImageView gif;

    public GifBottomView(Context context) {
        this(context, null);
    }

    public GifBottomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setGif(Object src){
        GlideUtil.GlideGif(context, src).into(gif);
    }

    private void init(Context context){
        this.context = context;
        gif = new ImageView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(UIUtils.dip2px(40), UIUtils.dip2px(40));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        gif.setLayoutParams(layoutParams);
        gif.setImageDrawable(UIUtils.getDrawable(R.drawable.bottom));

        AnimationDrawable drawable = (AnimationDrawable) gif.getDrawable();
        drawable.start();
//        GlideUtil.GlideGif(context, R.drawable.jiazai).into(gif);
        addView(gif);
    }


    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingUp(float fraction, float maxBottomHeight, float bottomHeight) {

    }

    @Override
    public void startAnim(float maxBottomHeight, float bottomHeight) {

    }

    @Override
    public void onPullReleasing(float fraction, float maxBottomHeight, float bottomHeight) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void reset() {

    }
}
