package com.wingplus.coomohome.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.wingplus.coomohome.R;

/**
 * 加中划线的TextView
 *
 * @author leaffun.
 *         Create on 2017/9/5.
 */
public class StrikeTextView extends AppCompatTextView {
    public StrikeTextView(Context context) {
        super(context);
        initView();
    }

    public StrikeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StrikeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        String s = getResources().getString(R.string.currency_symbol) + text;
        super.setText(s, type);
    }
}
