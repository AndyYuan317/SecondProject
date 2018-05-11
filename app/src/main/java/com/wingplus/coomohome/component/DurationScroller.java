package com.wingplus.coomohome.component;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 周期性滚动条
 * Created by wangyn on 16/4/10.
 */
public class DurationScroller extends Scroller {

    private double scrollFactor = 1;

    public DurationScroller(Context context) {
        super(context);
    }

    public DurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int)(duration * scrollFactor));
    }
}
