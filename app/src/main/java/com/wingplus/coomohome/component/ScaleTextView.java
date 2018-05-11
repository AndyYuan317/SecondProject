package com.wingplus.coomohome.component;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.util.DisplayUtil;

/**
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class ScaleTextView extends android.support.v7.widget.AppCompatTextView {

    private float textSize;

    public ScaleTextView(Context context) {
        super(context);
        init();
    }

    public ScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableString ss = new SpannableString(text.toString());
//        AbsoluteSizeSpan what = new AbsoluteSizeSpan(35, false);
//        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
//        ss.setSpan(what, 0, text.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(styleSpan, 0, text.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextAppearanceSpan(getContext(), R.style.big), 0, text.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextAppearanceSpan(getContext(), R.style.small), text.toString().length() - 1, text.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.setText(ss, type);
    }


    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //测量字符串的长度
                float measureWidth = getPaint().measureText(String.valueOf(getText()));
                //得到TextView 的宽度
                int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                //当前size大小
                textSize = getTextSize();
                if (width < measureWidth) {
                    textSize = (width / measureWidth) * textSize;
                }
                //注意，使用像素大小设置
//                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void measureText(String s){
        setText(s);
        //测量字符串的长度
        float measureWidth = getPaint().measureText(String.valueOf(getText()));
        //得到TextView 的宽度
        int width = DisplayUtil.dip2px(getContext(), 96-12-12);
        //当前size大小
        textSize = getTextSize();
        if (width < measureWidth) {
            textSize = (width / measureWidth) * textSize;
        }
        LogUtil.i("textSize", textSize+"");
        setText(s);
    }
}
