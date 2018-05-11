package com.wingplus.coomohome.component;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 可以自动轮播的ViewPager,与{@link CirclePageIndicator}配套使用实现循环模式
 * Created by wangyn on 16/4/10.
 */
public class AutoScrollViewPager extends ViewPager {
    public static final int DEFAULT_INTERVAL = 3500;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    /**
     * do nothing when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_NONE = 0;
    /**
     * cycle when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_CYCLE = 1;
    /**
     * deliver event to parent when sliding at the last or first item
     **/
    public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

    /**
     * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     **/
    private long interval = DEFAULT_INTERVAL;
    /**
     * auto scroll direction, default is {@link #RIGHT}
     **/
    private int direction = RIGHT;
    /**
     * whether automatic cycle when auto scroll reaching the last or first item, default is true
     **/
    private boolean isCycle = true;
    /**
     * whether stop auto scroll when touching, default is true
     **/
    private boolean stopScrollWhenTouch = true;
    /**
     * how to process when sliding at the last or first item, default is {@link #SLIDE_BORDER_MODE_NONE}
     **/
    private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
    /**
     * whether animating when auto scroll at the last or first item
     **/
    private boolean isBorderAnimation = true;
    /**
     * whether animating when auto scroll
     **/
    private boolean autoScrollAnimation = true;
    /**
     * scroll factor for auto scroll animation, default is 1.0
     **/
    private double autoScrollFactor = 3.0;
    /**
     * scroll factor for swipe scroll animation, default is 1.0
     **/
    private double swipeScrollFactor = 1.0;

    private Handler handler;
    private boolean isAutoScroll = false;
    private boolean isStopByTouch = false;
    private float touchX = 0f, downX = 0f;
    private DurationScroller scroller = null;

    public static final int SCROLL_WHAT = 0;

    public AutoScrollViewPager(Context paramContext) {
        super(paramContext);
        init();
    }

    public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    private void init() {
        handler = new MyHandler(this);
        setViewPagerScroller();
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 当ViewPager为循环模式的时候，第一张图片和倒数第二张相同，第二张图片和最后一张图片相同。
                // 当用户切换到第一张的的末尾是图片自动切换到倒数第二张，当用户切换到最后一张图片时自动切换到第二张图片。
                // 自动切换事件采用无动画模式，在切换完成后自动执行。
                // 这个事件只能onPageScrolled方法中执行，因为只有在这个方法中才会等待切换动画的执行完成，onPageSelected在切换开始时就会改变position的数值。
// leaffun：无限循环正常切换解决方案
//                if (slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
//                    PagerAdapter adapter = getAdapter();
//                    final int pageCount = adapter == null ? 0 : adapter.getCount();
//                    if (position == pageCount - 1) {
//                        setCurrentItem(1, false);
//                    }
//                    LogUtil.i("positionOffset", positionOffset + "");
//                    if (position == 0 && positionOffset == 0f) {//leaffun：增加了&& positionOffset == 0f判断，左侧滑入时等待滑入结束再切换。
//                        setCurrentItem(pageCount - 2, false);
//                    }
//                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // leaffun: 无限循环最终解决方案
                // 这里主要是解决在onPageScrolled出现的闪屏问题
                // (positionOffset为0的时候，并不一定是切换完成，所以动画还在执行，强制再次切换，就会闪屏)
                // leaffun2: 因为需要等待滚动完成的时间，导致指示器延时，所以在指示器中提前跳跃，解决延时体验问题。
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:// 空闲状态，没有任何滚动正在进行（表明完成滚动）
                        if (slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
                            PagerAdapter adapter = getAdapter();
                            final int pageCount = adapter == null ? 0 : adapter.getCount();
                            if (getCurrentItem() == pageCount - 1) {
                                setCurrentItem(1, false);
                            }
                            if (getCurrentItem() == 0) {
                                setCurrentItem(pageCount - 2, false);
                            }
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:// 正在拖动page状态
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:// 手指已离开屏幕，自动完成剩余的动画效果
                        break;
                }
            }
        });
    }

    /**
     * start auto scroll, first scroll delay time is {@link #getInterval()}
     */
    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage((long) (interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
    }

    /**
     * start auto scroll
     *
     * @param delayTimeInMills first scroll delay time
     */
    public void startAutoScroll(int delayTimeInMills) {
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }

    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }

    /**
     * set the factor by which the duration of sliding animation will change while swiping
     */
    public void setSwipeScrollDurationFactor(double scrollFactor) {
        swipeScrollFactor = scrollFactor;
    }

    /**
     * set the factor by which the duration of sliding animation will change while auto scrolling
     */
    public void setAutoScrollDurationFactor(double scrollFactor) {
        autoScrollFactor = scrollFactor;
    }

    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    /**
     * 生成循环模式的ViewPager数据源，将第一张图片重复放在最后，将最后一张图片重复放在开始。
     *
     * @param orgSource 原始数据
     * @param <T>       数据类型
     * @return 循环的数据源
     */
    public static <T> List<T> GetCircleModePagerSource(List<T> orgSource) {
        if (orgSource == null || orgSource.size() <= 1) {
            return orgSource;
        } else {
            List<T> retSource = new ArrayList<>();
            retSource.add(orgSource.get(orgSource.size() - 1));
            for (T item : orgSource) {
                retSource.add(item);
            }
            retSource.add(orgSource.get(0));

            return retSource;
        }
    }

    /**
     * set ViewPager scroller to change animation duration when sliding
     */
    private void setViewPagerScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            scroller = new DurationScroller(getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * scroll only once
     */
    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }

        int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, isBorderAnimation);
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, isBorderAnimation);
            }
        } else {
            setCurrentItem(nextItem, autoScrollAnimation);//leaffun 自动切换时，是否使用动画
        }
    }

    /**
     * 当滚动模式为SLIDE_BORDER_MODE_TO_PARENT时,当本体事件滑动结束后,将事件传导到上级容器中。
     * 当stopScrollWhenTouch为true时，手指触碰时停止自动滑动。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);

        if (stopScrollWhenTouch) {
            if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
                startAutoScroll();
            }
        }

        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
            touchX = ev.getX();
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downX = touchX;
            }
            int currentItem = getCurrentItem();
            PagerAdapter adapter = getAdapter();
            int pageCount = adapter == null ? 0 : adapter.getCount();

            if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return super.dispatchTouchEvent(ev);
            }
        }
        getParent().requestDisallowInterceptTouchEvent(true);

        return super.dispatchTouchEvent(ev);
    }

    private static class MyHandler extends Handler {

        private final WeakReference<AutoScrollViewPager> autoScrollViewPager;

        public MyHandler(AutoScrollViewPager autoScrollViewPager) {
            this.autoScrollViewPager = new WeakReference<>(autoScrollViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    AutoScrollViewPager pager = this.autoScrollViewPager.get();
                    if (pager != null) {
                        pager.scroller.setScrollDurationFactor(pager.autoScrollFactor);
                        pager.scrollOnce();
                        pager.scroller.setScrollDurationFactor(pager.swipeScrollFactor);
                        pager.sendScrollMessage(pager.interval + pager.scroller.getDuration());
                    }
                default:
                    break;
            }
        }
    }

    /**
     * get auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * get auto scroll direction
     *
     * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public int getDirection() {
        return (direction == LEFT) ? LEFT : RIGHT;
    }

    /**
     * set auto scroll direction
     *
     * @param direction {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * whether automatic cycle when auto scroll reaching the last or first item, default is true
     *
     * @return the isCycle
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * set whether automatic cycle when auto scroll reaching the last or first item, default is true
     *
     * @param isCycle the isCycle to set
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * whether stop auto scroll when touching, default is true
     *
     * @return the stopScrollWhenTouch
     */
    public boolean isStopScrollWhenTouch() {
        return stopScrollWhenTouch;
    }

    /**
     * set whether stop auto scroll when touching, default is true
     *
     * @param stopScrollWhenTouch
     */
    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }

    /**
     * get how to process when sliding at the last or first item
     *
     * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE}, {@link #SLIDE_BORDER_MODE_TO_PARENT},
     * {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
     */
    public int getSlideBorderMode() {
        return slideBorderMode;
    }

    /**
     * set how to process when sliding at the last or first item
     *
     * @param slideBorderMode {@link #SLIDE_BORDER_MODE_NONE}, {@link #SLIDE_BORDER_MODE_TO_PARENT},
     *                        {@link #SLIDE_BORDER_MODE_CYCLE}, default is {@link #SLIDE_BORDER_MODE_NONE}
     */
    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }

    /**
     * whether animating when auto scroll at the last or first item, default is true
     *
     * @return
     */
    public boolean isBorderAnimation() {
        return isBorderAnimation;
    }

    /**
     * set whether animating when auto scroll at the last or first item, default is true
     *
     * @param isBorderAnimation
     */
    public void setBorderAnimation(boolean isBorderAnimation) {
        this.isBorderAnimation = isBorderAnimation;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int height = 0;
//        for(int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if(h > height) height = h;
//        }
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
}
