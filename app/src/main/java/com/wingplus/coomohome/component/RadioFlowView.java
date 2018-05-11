package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leaffun.
 *         Create on 2017/9/18.
 */
public class RadioFlowView extends MyFlowLayout {
    public RadioFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RadioFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioFlowView(Context context) {
        super(context);
    }

    private List<Integer> count = new ArrayList<>();//记录个数
    private SelectedListener selectedListener;//回调监听
    private SelectableTextView mSTV;//当前选中的view


    public void addChild(String text, String selected) {
        if (count.size() >= 50) return;
        final SelectableTextView stv = new SelectableTextView(getContext());

        if (text.equals(selected)) {
            stv.select(true);
            mSTV = stv;
        } else {
            stv.select(false);
        }
        count.add(count.size());

        stv.setText(text);
        stv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectChild(stv);
            }
        });
        addView(stv);
    }

    private synchronized void selectChild(SelectableTextView stv) {
        boolean selected = true;
        if (selectedListener != null) {
            selected = selectedListener.selected(stv);
        }

        if (selected) {
            if (mSTV != null) {
                mSTV.select(false);
            }
            stv.select(true);
            mSTV = stv;
        }
    }

    public void initData(List<String> data, String selected) {
        if (data == null) return;
        if (data.size() <= 0) return;
        for (String s : data) {
            addChild(s, selected);
        }
    }

    public SelectableTextView getSelectedView() {
        return mSTV;
    }

    public interface SelectedListener {
        boolean selected(SelectableTextView stv);
    }

    public void setSelectedListener(SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }
}
