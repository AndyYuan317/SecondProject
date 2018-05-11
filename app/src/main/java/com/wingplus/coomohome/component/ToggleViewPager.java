package com.wingplus.coomohome.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 * Created by wangyn on 15/7/21.
 */
public class ToggleViewPager extends ViewPager {

    private boolean canScroll = false;

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public ToggleViewPager(Context context){
        super(context);
    }

    public ToggleViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y){
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (!canScroll){
            return false;
        }else{
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        if (!canScroll){
            return false;
        }else{
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll){
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item){
        super.setCurrentItem(item);
    }
}
