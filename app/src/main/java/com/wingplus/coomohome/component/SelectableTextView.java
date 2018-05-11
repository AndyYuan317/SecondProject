package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.DisplayUtil;

/**
 * 可选择的TextView
 *
 * @author leaffun.
 *         Create on 2017/9/18.
 */
public class SelectableTextView extends android.support.v7.widget.AppCompatTextView {
    public SelectableTextView(Context context) {
        super(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void select(boolean selected) {
        if (selected) {
            setTextColor(getResources().getColor(R.color.colorPrimary));
            setBackground(getResources().getDrawable(R.drawable.bg_text_rectangle_border_primary_1));
        } else {
            setTextColor(getResources().getColor(R.color.titleBlack));
            setBackground(getResources().getDrawable(R.drawable.bg_text_rectangle_border_black_r_1px));
        }
        setTextSize(14);
        int h = DisplayUtil.dip2px(getContext(), 10);
        int v = DisplayUtil.dip2px(getContext(), 5);
        setPadding(h, v, h, v);
    }
}
