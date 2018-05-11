package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 方形Image
 * @author leaffun.
 *         Create on 2017/9/17.
 */
public class SquareImage extends android.support.v7.widget.AppCompatImageView {
    public SquareImage(Context context) {
        super(context);
    }

    public SquareImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
