package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;

/**
 * tabs
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class TabGroup extends LinearLayout {
    private LinearLayout mHome;
    private LinearLayout mCategory;
    private LinearLayout mSurprise;
    private LinearLayout mCart;
    private LinearLayout mMine;

    private int[] imageRes;
    private int[] imageResActived;

    private int mIndex;
    private ViewGroup[] mItems;

    private int mLabelColor;
    private int mLabelColorSelected;
    private TabSelectListener mTabSelectListener;

    public TabGroup(Context context) {
        super(context);
        initView(context);
    }

    public TabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_tab_group, this, true);
        mHome = view.findViewById(R.id.tab_home);
        mCategory = view.findViewById(R.id.tab_category);
        mSurprise = view.findViewById(R.id.tab_surprise);
        mCart = view.findViewById(R.id.tab_cart);
        mMine = view.findViewById(R.id.tab_mine);


        mLabelColor = getResources().getColor(R.color.gray);
        mLabelColorSelected = getResources().getColor(R.color.colorPrimary);
        if (MallApplication.hasPromotion) {
            mItems = new ViewGroup[]{mHome, mCategory, mSurprise, mCart, mMine};
            imageRes = new int[]{R.drawable.tab_home
                    , R.drawable.tab_commodity
                    , R.drawable.tab_activity
                    , R.drawable.tab_shoppingcart
                    , R.drawable.tab_user};

            imageResActived = new int[]{R.drawable.tab_home_pre
                    , R.drawable.tab_commodity_pre
                    , R.drawable.tab_activity_pre
                    , R.drawable.tab_shoppingcart_pre
                    , R.drawable.tab_user_pre};
        } else {
            mItems = new ViewGroup[]{mHome, mCategory, mCart, mMine};
            imageRes = new int[]{R.drawable.tab_home
                    , R.drawable.tab_commodity
                    , R.drawable.tab_shoppingcart
                    , R.drawable.tab_user};

            imageResActived = new int[]{R.drawable.tab_home_pre
                    , R.drawable.tab_commodity_pre
                    , R.drawable.tab_shoppingcart_pre
                    , R.drawable.tab_user_pre};
            mSurprise.setVisibility(GONE);
        }

        for (int i = 0; i < mItems.length; i++) {
            ViewGroup item = mItems[i];
            final int index = i;
            item.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    changeSelected(index);
                }

            });
        }
    }

    public int getIndex() {
        return mIndex;
    }

    /**
     * 修改主页面的底部标签选中，并且改变对应的页面内容
     *
     * @param index 顺序
     */
    public void changeSelected(int index) {
        if (index != getIndex()) {
            if (mTabSelectListener != null) {
                if (mTabSelectListener.onTabSelected(index)) {
                    updateSelected(index);
                }
            }
        }
    }

    public void updateSelected(int index) {
        mIndex = index;
        ViewGroup item = mItems[mIndex];
        new TabGroup.ViewHolder(item).setSelected(index, true);
        for (int i = 0; i < mItems.length; i++) {
            if (i == mIndex) {
                continue;
            }
            ViewGroup viewGroup = mItems[i];
            new TabGroup.ViewHolder(viewGroup).setSelected(i, false);
        }
    }

    public void updateNum(int index, int num) {
        ViewGroup viewGroup = mItems[index];
        new TabGroup.ViewHolder(viewGroup).setmNum(num);
    }

    public void updateLabel(int index, String label) {
        ViewGroup viewGroup = mItems[index];
        new TabGroup.ViewHolder(viewGroup).setmLabel(label);
    }

    class ViewHolder {

        private ImageView mIcon;
        private TextView mLabel;
        private TextView mNum;
        public ViewHolder(View view) {
            mIcon = view.findViewById(R.id.icon);
            mLabel = view.findViewById(R.id.label);
            mNum = view.findViewById(R.id.num);
        }

        void setSelected(int index, boolean selected) {
            if (selected) {
                mIcon.setImageResource(imageResActived[index]);
                mLabel.setTextColor(mLabelColorSelected);
            } else {
                mIcon.setImageResource(imageRes[index]);
                mLabel.setTextColor(mLabelColor);
            }
        }

        void setmNum(int num) {
            if (mNum != null) {
                if (num == 0) {
                    mNum.setVisibility(INVISIBLE);
                } else {
                    mNum.setVisibility(VISIBLE);
                    mNum.setText(String.valueOf(num));
                }
            }
        }

        public TextView getmNum() {
            return mNum;
        }

        public void setmLabel(String name) {
            mLabel.setText(name);
        }
    }


    public void setOnTabChangeListener(TabGroup.TabSelectListener tabSelectListener) {
        mTabSelectListener = tabSelectListener;
    }

    public interface TabSelectListener {
        /**
         * 返回true表示作出UI变化
         *
         */
        boolean onTabSelected(int index);
    }


}
