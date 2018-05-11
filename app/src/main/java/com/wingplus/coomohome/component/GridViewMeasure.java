package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 测量总高度的GridView
 * @author leaffun.
 *         Create on 2017/9/17.
 */
public class GridViewMeasure extends GridView {
    public GridViewMeasure(Context context) {
        super(context);
    }

    public GridViewMeasure(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewMeasure(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
