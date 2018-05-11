package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.util.ViewUtils;

/**
 * 预设头脚布局
 * @author leaffun.
 *         Create on 2017/10/24.
 */
public class MyRefreshLayout extends TwinklingRefreshLayout {

    public MyRefreshLayout(Context context) {
        super(context);
        initDefaultView();
    }

    public MyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultView();
    }

    public MyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultView();
    }

    private void initDefaultView(){
        setHeaderView(ViewUtils.getRefreshHead());
        setBottomView(ViewUtils.getLoadMoreFoot());
        setHeaderHeight(40);
        setMaxHeadHeight(80);
        setBottomHeight(30);
        setMaxBottomHeight(80);
        setEnableOverScroll(true);
        setAutoLoadMore(true);
    }

    public void forbidden(){
        setEnabled(false);
        setEnableOverScroll(false);
        setEnableRefresh(false);
        setEnableLoadmore(false);
    }
}
