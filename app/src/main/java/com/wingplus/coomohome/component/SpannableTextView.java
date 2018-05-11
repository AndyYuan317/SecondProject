package com.wingplus.coomohome.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.PriceFixUtil;

/**
 * 可设置不同大小、颜色String的TextView
 *
 * @author leaffun.
 *         Create on 2017/9/6.
 */
public class SpannableTextView extends android.support.v7.widget.AppCompatTextView {
    private String preString = "";
    private boolean mCanSpannable = true;
    private boolean aftTwo = false;

    public SpannableTextView(Context context) {
        super(context);
    }

    public SpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpannableTextView);// 由attrs 获得 TypeArray
        preString = ta.getString(R.styleable.SpannableTextView_pre_string);
        aftTwo = ta.getBoolean(R.styleable.SpannableTextView_aft_two, false);
        ta.recycle();
    }

    public SpannableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpannableTextView);// 由attrs 获得 TypeArray
        preString = ta.getString(R.styleable.SpannableTextView_pre_string);
        aftTwo = ta.getBoolean(R.styleable.SpannableTextView_aft_two, false);
        ta.recycle();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!mCanSpannable || text.equals("")) {
            super.setText(text, type);
            return;
        }

        String string = handleStr(text);

        super.setText(string, type);
    }

    private String handleStr(CharSequence text) {
        String string;
        if (preString == null || "".equals(preString)) {
            if (text.toString().startsWith("-")) {
                if (aftTwo) {
                    string = "- " + getResources().getString(R.string.currency_symbol) + PriceFixUtil.formatPay(Double.parseDouble(text.toString().substring(1)));
                } else {
                    string = "- " + getResources().getString(R.string.currency_symbol) + PriceFixUtil.format(Double.parseDouble(text.toString().substring(1)));
                }
            } else {
                if (aftTwo) {
                    string = getResources().getString(R.string.currency_symbol) + PriceFixUtil.formatPay(Double.parseDouble(text.toString()));
                } else {
                    string = getResources().getString(R.string.currency_symbol) + PriceFixUtil.format(Double.parseDouble(text.toString()));
                }
            }
        } else {
            string = preString + text.toString();
        }
        return string;
    }

    /**
     * 设置前缀
     *
     * @param preString
     */
    public void setPreString(String preString) {
        this.preString = preString;
        invalidate();
    }

    /**
     * 设置文本可拼接
     *
     * @param canSpannable
     */
    public void setCanSpannable(boolean canSpannable) {
        mCanSpannable = canSpannable;
    }

    /**
     * 获取去除前缀的文本
     *
     * @return
     */
    public String getTextWithoutPre() {
        String string = getText().toString();
        if (preString != null && preString.length() > 0) {
            return string.substring(preString.length());
        }
        if (string.startsWith("-")) {
            return "-" + string.substring(getResources().getString(R.string.currency_symbol).length() + 1);
        } else {
            return string.substring(getResources().getString(R.string.currency_symbol).length());
        }
    }

//    private String styleString(String old, ){
//        String string = getResources().getString(R.string.currency_symbol) + text.toString();
//        int index = string.indexOf("\\.");
//        if (index == -1) {
//            index = string.length();
//            string += ".00";
//        }
//        if (mCanSpannable) super.setText(string, type);
//        SpannableString ss = new SpannableString(string);
//        ss.setSpan(new TextAppearanceSpan(getContext(), R.style.small), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new TextAppearanceSpan(getContext(), R.style.big), 1, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new TextAppearanceSpan(getContext(), R.style.small), index, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return ;
//    }
}