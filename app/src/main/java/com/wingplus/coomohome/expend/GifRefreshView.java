package com.wingplus.coomohome.expend;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.UIUtils;

/**
 * @author leaffun.
 *         Create on 2017/11/28.
 */
public class GifRefreshView extends FrameLayout implements IHeaderView {

    private Context context;
    private ImageView gif;

    public GifRefreshView(@NonNull Context context) {
        this(context, null);
    }

    public GifRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setGif(Object src){
        GlideUtil.GlideGif(context, src).into(gif);
    }

    private void init(Context context){
        this.context = context;
        gif = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        gif.setLayoutParams(layoutParams);
        gif.setAdjustViewBounds(true);
        gif.setMaxHeight(UIUtils.dip2px(40));
        gif.setMaxWidth(UIUtils.dip2px(40));
        gif.setImageDrawable(UIUtils.getDrawable(R.drawable.header));

        AnimationDrawable drawable = (AnimationDrawable) gif.getDrawable();
        drawable.start();
//        GlideUtil.GlideWithPlaceHolder(context, null).into(gif);
//        GlideUtil.GlideGif(context, R.drawable.shuaxin_6).into(gif);
        addView(gif);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
    }

    @Override
    public void onFinish(OnAnimEndListener animEndListener) {
        animEndListener.onAnimEnd();
    }

    @Override
    public void reset() {
    }
}
