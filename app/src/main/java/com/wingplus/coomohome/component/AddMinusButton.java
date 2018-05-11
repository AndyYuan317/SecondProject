package com.wingplus.coomohome.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;

/**
 * 加减控件
 *
 * @author leaffun.
 *         Create on 2017/9/5.
 */
public class AddMinusButton extends LinearLayout {

    private TextView number;

    private NumberUpdateListener mNumberUpdateListener;

    private boolean canNegative = false;
    private ImageView minus;

    public AddMinusButton(Context context) {
        super(context);
        initView(context);
    }

    public AddMinusButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddMinusButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.component_add_minus_button, this);
        minus = linearLayout.findViewById(R.id.minus);
        number = linearLayout.findViewById(R.id.number);
        ImageView add = linearLayout.findViewById(R.id.add);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNumber(-1);
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNumber(1);
            }
        });

        updateNumber(1);
    }

    private void updateNumber(int i) {
        int val = getNumber() + i;

        if (canNegative || val > 0) {
            if (mNumberUpdateListener != null) {
                mNumberUpdateListener.numberUpdate(val, i);
            }else{
                number.setText(String.valueOf(val));
            }
        }

    }

    public int getNumber() {
        CharSequence text = number.getText();
        if (text == null || text.equals("")) {
            return 0;
        }
        return Integer.parseInt(text.toString());
    }

    /**
     * 初始化数值
     * @param value
     */
    public void initNumber(int value){
        number.setText(String.valueOf(value));
    }

    /**
     * 更新数值
     * @param value
     */
    public void setNumber(int value) {
        updateNumber(value);
    }

    public void canBeNegative(boolean canBeNegative) {
        canNegative = canBeNegative;
    }


    public void setNumberUpdateListener(NumberUpdateListener numberUpdateListener) {
        mNumberUpdateListener = numberUpdateListener;
    }

    public interface NumberUpdateListener {
        /**
         *
         * @param number 更新后的值
         * @param changeVal 改变的值（可能为负数）
         */
        void numberUpdate(int number, int changeVal);
    }
}