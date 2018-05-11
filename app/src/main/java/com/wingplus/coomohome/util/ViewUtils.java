package com.wingplus.coomohome.util;

import android.support.design.widget.TabLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.IBottomView;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.footer.BallPulseView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.expend.GifBottomView;
import com.wingplus.coomohome.expend.GifRefreshView;

import java.lang.reflect.Field;

import static com.wingplus.coomohome.util.DisplayUtil.dip2px;

public class ViewUtils {
    /**
     * 移除爹
     */
    public static void removeParent(View v) {
        //  先找到爹 再通过爹去移除孩子 --> 来解除关系
        ViewParent parent = v.getParent();
        //所有的控件 都有爹 爹一般情况下就是ViewGoup（特殊如：屏幕）
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            group.removeView(v);
        }
    }

    /**
     * 设置指示器长度
     *
     * @param tabLayout
     */
    public static void reflex(final TabLayout tabLayout, final boolean isCustomView) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp40 = dip2px(tabLayout.getContext(), 40);
                    int dp52 = dip2px(tabLayout.getContext(), 52);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);


                        if (isCustomView) {
                            Field mCustomView = tabView.getClass().getDeclaredField("mCustomView");
                            LogUtil.i("ViewUtils", "mCustomView" + (mCustomView == null));
                            if(mCustomView != null){
                                mCustomView.setAccessible(true);
                            }else{
                                LogUtil.i("ViewUtils", "mCustomView.setAccessible() on null");
                            }

                            LinearLayout ll = (LinearLayout) mCustomView.get(tabView);
                            ll.measure(0, 0);

                            int measuredWidth = ll.getMeasuredWidth();
                            tabView.setPadding(0, 0, 0, 0);


                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                            params.width = measuredWidth;
                            int margin = (tabView.getMeasuredWidth() - measuredWidth) / 2;
                            params.leftMargin = margin;
                            params.rightMargin = margin;
                            tabView.setLayoutParams(params);

                        } else {

                            //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                            Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                            LogUtil.i("ViewUtils", "mTextView" + (mTextViewField == null));
                            if(mTextViewField != null){
                                mTextViewField.setAccessible(true);
                            }else {
                                LogUtil.i("ViewUtils", "mTextView.setAccessible() on null");
                            }
                            TextView mTextView = (TextView) mTextViewField.get(tabView);

                            tabView.setPadding(0, 0, 0, 0);


                            int tabWidth = 0;
                            int fontWidth = dp40;
                            mTextView.measure(0, 0);
                            tabWidth = tabView.getMeasuredWidth();
                            //这里因项目要求定为40dp
                            //要的效果是   字多宽线就多宽，所以测量mTextView的宽度(因为是wrap_content模式)
//                        fontWidth = mTextView.getWidth();
//                        TextPaint paint = mTextView.getPaint();
//                        width = (int) paint.measureText(mTextView.getText().toString());


                            //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                            params.width = fontWidth;
                            int margin = (tabWidth - fontWidth) / 2;
                            params.leftMargin = margin;
                            params.rightMargin = margin;
                            tabView.setLayoutParams(params);
                        }
                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    LogUtil.i("ViewUtils", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 获取全局下拉刷新头布局
     *
     * @return
     */
    public static IHeaderView getRefreshHead() {
//        SinaRefreshView sinaRefreshView = new SinaRefreshView(MallApplication.getContext());
//        sinaRefreshView.setTextColor(UIUtils.getColor(R.color.titleGrey));
//        return sinaRefreshView;
        return new GifRefreshView(MallApplication.getContext());
    }

    /**
     * 获取全局上拉加载脚布局
     *
     * @return
     */
    public static IBottomView getLoadMoreFoot() {
//        BallPulseView pulseView = new BallPulseView(MallApplication.getContext());
//        pulseView.setAnimatingColor(UIUtils.getColor(R.color.colorPrimary));
//        pulseView.setIndicatorColor(UIUtils.getColor(R.color.white));
//        return pulseView;
        return new GifBottomView(MallApplication.getContext());
    }
}
