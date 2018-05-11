package com.wingplus.coomohome.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.util.DisplayUtil;

import java.util.ArrayList;

/**
 * 自定义排行页控件:均布模式
 *
 * @author ben
 */
public class MyFlowLayout extends ViewGroup {

    //均布模式
    private boolean sameMode = false;

    private Line mLine;// 当前行对象
    private int mUsedWidth;// 当前行已使用的宽度

    private int mHorizontalSpacing = DisplayUtil.dip2px(getContext(), 10);// 水平间距
    private int mVerticalSpacing = DisplayUtil.dip2px(getContext(), 10);// 竖直间距

    private int MAX_LINE = 100;// 允许最多的行数

    private ArrayList<Line> mLineList;// 行的集合

    private boolean mNeedLayout = true;//需要onlayout布局

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public MyFlowLayout(Context context) {
        super(context);
        init();

    }

    public void init() {


        if (mLineList != null) {
            mLineList = null;
        }
        mLineList = new ArrayList<Line>();
    }

    // 设置位置的回调
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!mNeedLayout || changed) {
// ！！！！！！！！！不再布局了，顺便问下这里的changed是怎么来的，根据什么判断得到的	！！！！！！！！！！！！	
            mNeedLayout = false;
            // 获取控件左上角的位置
            int left = getPaddingLeft();
            int top = getPaddingTop();

            for (int i = 0; i < mLineList.size(); i++) {
                Line line = mLineList.get(i);
                line.layoutView(left, top);
                // 顶端的高度值,是以上所有行的高度的累加+行的间距
                top += line.maxHeight + mVerticalSpacing;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//		mLineList.clear();
        // 获取可用宽度(减去左右边距)
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingRight();
        // 获取可用高度(减去上线边距)
        int heightSize = MeasureSpec.getSize(heightMeasureSpec)
                - getPaddingTop() - getPaddingBottom();
        // 获取宽度模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 获取高度模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 获取所有孩子数量
        int childCount = getChildCount();
//重置参数
//        System.out.println("总高度：" + 0 + "总集合”：" + mLineList.size());
        mLine = null;
        mLineList.clear();
        mUsedWidth = 0;

        for (int i = 0; i < childCount; i++) {
            // 获取当前索引的子控件
            View childView = getChildAt(i);

//			final View child = getChildAt(i);
            if (childView.getVisibility() == GONE) {
//                System.out.println("孩子不在");
                continue;
            }

            // 定义子控件宽高模式, 父控件为确定模式,那子控件就为至多模式, 其余模式子控件和父控件一致
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
                    widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                            : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    heightSize,
                    heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST
                            : heightMode);

            // 根据确定好的宽高模式重新测量控件
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            // 获取测量后的宽高
            int childWidth = childView.getMeasuredWidth();

            if (mLine == null) {
                mLine = new Line();
            }

            mUsedWidth += childWidth;
            if (mUsedWidth < widthSize) {
                mLine.addView(childView);
                mUsedWidth += mHorizontalSpacing;

                if (mUsedWidth > widthSize) {
                    // 加上水平间距后如果大于宽度最大值,就需要换行了
                    if (!newLine()) {
                        // 如果创建行失败, 跳出循环,不再添加
                        break;
                    }
                }
            } else {
                if (mLine.getViewCount() == 0) {
                    // 如果当前行还没有添加任何控件, 即使控件宽度超出最大值,也必须在当前行添加, 保证每行至少一个控件
                    mLine.addView(childView);
                    // 此时控件独占一行, 需要换行后继续添加控件
                    if (!newLine()) {
                        break;
                    }
                } else {
                    // 当前行已经有控件, 只不过如果再新增控件会导致超出最大宽度, 此时要换行了
                    if (!newLine()) {
                        break;
                    }

                    mLine.addView(childView);
                    // 换行后mUsedWidth归零, 需要重新初始化
                    mUsedWidth += childWidth + mHorizontalSpacing;
                }
            }

        }

        // 如果是最后一行, 控件宽度没有超出最大值, 所以不会走newLine方法, 可能导致没有将最后一个Line对象添加到集合中
        if (mLine != null && !mLineList.contains(mLine)
                && mLine.getViewCount() > 0) {
            mLineList.add(mLine);
        }

        // 计算整个控件的宽高
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int totalHeight = 0;
        // 计算所有行的总高度
        for (int i = 0; i < mLineList.size(); i++) {
            totalHeight += mLineList.get(i).maxHeight;
        }

        // 总高度 = 所有行高度+ 行间距 + 上下边距
        totalHeight += mVerticalSpacing * (mLineList.size() - 1)
                + getPaddingTop() + getPaddingBottom();

        // 设置整个控件的宽高
        setMeasuredDimension(totalWidth, totalHeight);
    }

    /**
     * 创建新的一行
     */
    private boolean newLine() {
        // 将最新的行对象添加到集合中
        mLineList.add(mLine);
        if (mLineList.size() < MAX_LINE) {// 判断是否超过最大行数
            mUsedWidth = 0;
            mLine = new Line();

            return true;
        }else{
            // 如果目前行数已经达到最大允许的行数, 就返回false,表示创建失败
            return false;
        }

    }

    /**
     * 将每一行控件封装为一个对象
     */
    class Line {
        public int maxHeight;// 当前行中最高view的高度
        public int totalWidth;// 当前行所有控件的总宽度
        public ArrayList<View> viewList = new ArrayList<View>();// 当前行控件集合

        // 获取当前行控件总数
        public int getViewCount() {
            return viewList.size();
        }

        // 给当前行添加一个控件
        public void addView(View view) {
            // 获取控件宽高
            int height = view.getMeasuredHeight();
            int width = view.getMeasuredWidth();

            totalWidth += width;
            maxHeight = maxHeight < height ? height : maxHeight;

            // 给集合中添加控件
            viewList.add(view);
        }

        // 设置当前行中每个控件的位置,同时根据留白分配每个控件的宽度
        public void layoutView(int left, int top) {
            int count = getViewCount();
            // 有效宽度
            int validWidth = getMeasuredWidth() - getPaddingStart()
                    - getPaddingEnd();
            // 计算留白宽度
            int surplusWidth = validWidth - totalWidth - mHorizontalSpacing
                    * (count - 1);
            if (surplusWidth >= 0) {
                int splitWidth = (int) (surplusWidth / count + 0.5f);
                if (!sameMode) {
                    splitWidth = 0;
                }
                for (int i = 0; i < count; i++) {
                    View childView = viewList.get(i);
                    int measuredWidth = childView.getMeasuredWidth();
                    int measuredHeight = childView.getMeasuredHeight();

                    // 将宽度分配给每个控件
                    measuredWidth += splitWidth;
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            measuredHeight, MeasureSpec.EXACTLY);

                    // 重新测量控件宽高
                    childView.measure(widthMeasureSpec, heightMeasureSpec);

                    // 为了保证高度比较小的控件可以居中显示, 需要计算此控件应该距顶部偏移的高度
                    int surplusHeight = maxHeight - measuredHeight;
                    int topOffset = (int) (surplusHeight / 2 + 0.5f);

                    if (topOffset < 0) {
                        topOffset = 0;
                    }

                    // 设置控件的位置
                    childView.layout(left, top + topOffset, left
                            + measuredWidth, top + topOffset + measuredHeight);
                    // 更新left值,作为下一个控件的left位置
                    left += measuredWidth + mHorizontalSpacing;
                }
            } else {
                // 当某个控件独占一行时,才有可能走到此处
                View childView = viewList.get(0);
                childView.layout(left, top,
                        left + childView.getMeasuredWidth(),
                        top + childView.getMeasuredHeight());
            }
        }
    }

    public void setHorizontalSpacing(int x) {
        this.mHorizontalSpacing = x;
    }

    public void setVerticalSpacing(int y) {
        this.mHorizontalSpacing = y;
    }
}
