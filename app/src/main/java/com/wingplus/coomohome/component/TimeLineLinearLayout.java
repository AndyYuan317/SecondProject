package com.wingplus.coomohome.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wingplus.coomohome.R;


/**
 * 时间轴式布局
 * Created by leaffun on 2016/9/12.
 */
public class TimeLineLinearLayout extends LinearLayout {
    //=============================================================元素定义
    private Bitmap mFirstIcon;
    private Bitmap mIcon;
    //line location
    private int lineMarginSide; //时间轴线中心点距离左侧边的距离
    private int lineDynamicDimen; //动态调整y方向上与childview内容的偏移量
    //line property
    private int lineStrokeWidth; //
    private int lineColor; //时间轴线的颜色
    //point property
    private int pointSize; //轴点球的半径
    private int pointColor; //轴点球的颜色
    //icon
    private int iconWidth = 60; //默认最后一个球的宽高

    //=============================================================paint
    private Paint linePaint;
    private Paint pointPaint;
    //=============================================================其他辅助参数
    //第一个点的位置
    private int firstX;
    private int firstY;
    //最后一个图的位置
    private int lastX;
    private int lastY;
    //默认垂直
    private int curOrientation = VERTICAL;
    private Context mContext;

    //开关
    private boolean drawLine = true;

    public TimeLineLinearLayout(Context context) {
        this(context, null);
    }

    public TimeLineLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.TimeLineLinearLayout);
        lineMarginSide = attr.getDimensionPixelOffset(R.styleable.TimeLineLinearLayout_line_margin_side, 10);
        lineDynamicDimen = attr.getDimensionPixelOffset(R.styleable.TimeLineLinearLayout_line_dynamic_dimen, 0);
        lineStrokeWidth = attr.getDimensionPixelOffset(R.styleable.TimeLineLinearLayout_line_stroke_width, 2);
        lineColor = attr.getColor(R.styleable.TimeLineLinearLayout_line_color, 0xff3dd1a5);

        pointSize = attr.getDimensionPixelSize(R.styleable.TimeLineLinearLayout_point_size, 8);
        pointColor = attr.getColor(R.styleable.TimeLineLinearLayout_point_color, 0xff3dd1a5);


        int iconRes = attr.getResourceId(R.styleable.TimeLineLinearLayout_icon_src, R.drawable.icon_logisticstatus_no);
        BitmapDrawable temp = (BitmapDrawable) context.getResources().getDrawable(iconRes);
        if (temp != null) mIcon = temp.getBitmap();

        int firstIconRes = attr.getResourceId(R.styleable.TimeLineLinearLayout_icon_src, R.drawable.icon_logisticstatus);
        BitmapDrawable firstTemp = (BitmapDrawable) context.getResources().getDrawable(firstIconRes);
        if (temp != null) mFirstIcon = firstTemp.getBitmap();

        curOrientation = getOrientation();
        attr.recycle();
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{10, 8, 10, 8}, 2);
        linePaint.setPathEffect(effects);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);
        pointPaint.setColor(pointColor);
        pointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawLine) {
            drawTimeLine(canvas);
        }
    }

    private void drawTimeLine(Canvas canvas) {
        int childCount = getChildCount();

        if (childCount > 0) {
            //大于1，证明至少有2个，也就是第一个和第二个之间连成线，第一个和最后一个分别有点/icon
            if (childCount > 1) {
                switch (curOrientation) {
                    case VERTICAL:
                        drawFirstChildViewVertical(canvas);
                        drawBetweenChildViewVertical(canvas);
                        drawLastChildViewVertical(canvas);
                        drawBetweenLineVertical(canvas);
                        break;
                    case HORIZONTAL:
                        break;
                    default:
                        break;
                }
            } else if (childCount == 1) {
                switch (curOrientation) {
                    case VERTICAL:
                        drawFirstChildViewVertical(canvas);
                        break;
                    case HORIZONTAL:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void drawFirstChildViewVertical(Canvas canvas) {
        if (getChildAt(0) != null) {
            int top = getChildAt(0).getTop();
            //记录值
            firstX = lineMarginSide - (iconWidth >> 1);
            firstY = top + getChildAt(0).getPaddingTop() + lineDynamicDimen;     // 加上padding是为了与文字对齐水平线
            //画一个圆
            Rect rect = new Rect(firstX, firstY, firstX + iconWidth, firstY + iconWidth);
            canvas.drawBitmap(mFirstIcon, null, rect, null);
        }
    }

    private void drawBetweenChildViewVertical(Canvas canvas) {
        if (getChildCount() > 2) {
            int count = getChildCount() - 2;
            for (int i = 1; i < count + 1; i++) {
                if (getChildAt(i) != null) {
                    int top = getChildAt(i).getTop();
                    //记录值
                    int childX = lineMarginSide - (iconWidth >> 1);
                    int childY = top + getChildAt(i).getPaddingTop() + lineDynamicDimen;
                    //画一个图
//            canvas.drawBitmap(mIcon, lastX, lastY, null);
                    Rect rect = new Rect(childX, childY, childX + iconWidth, childY + iconWidth);
                    canvas.drawBitmap(mIcon, null, rect, null);
                }
            }
        }

    }

    private void drawLastChildViewVertical(Canvas canvas) {
        if (getChildAt(getChildCount() - 1) != null) {
            int top = getChildAt(getChildCount() - 1).getTop();
            //记录值
            lastX = lineMarginSide - (iconWidth >> 1);
            lastY = top + getChildAt(getChildCount() - 1).getPaddingTop() + lineDynamicDimen;
            //画一个图
//            canvas.drawBitmap(mIcon, lastX, lastY, null);
            Rect rect = new Rect(lastX, lastY, lastX + iconWidth, lastY + iconWidth);
            canvas.drawBitmap(mIcon, null, rect, null);
        }
    }

    private void drawBetweenLineVertical(Canvas canvas) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            if (getChildAt(i) != null) {
                //画剩下的
                firstY = getChildAt(i).getTop() + getChildAt(i).getPaddingTop() + lineDynamicDimen + iconWidth; //加球形半径是为了不与球形重叠
                lastY = getChildAt(i + 1).getTop() + getChildAt(i + 1).getPaddingTop() + lineDynamicDimen; //减球形半径是为了不与下一个球形重叠
                if (i == getChildCount() - 2) {
                    lastY = getChildAt(i + 1).getTop() + getChildAt(i + 1).getPaddingTop() + lineDynamicDimen; //最后一张图片的位置偏低
                }

                Path path = new Path();
                path.moveTo(lineMarginSide, firstY);
                path.lineTo(lineMarginSide, lastY);

                canvas.drawPath(path, linePaint);


//                if (i != 0) {
//                    //画了线，就画圆
//                    int top = getChildAt(i).getTop();
//                    //记录值
//                    int Y = top + getChildAt(i).getPaddingTop() + lineDynamicDimen;
//                    canvas.drawCircle(lineMarginSide, Y, pointSize, pointPaint);
//                }
            }
        }
    }

    public void setIconWidth(int width) {
        this.iconWidth = width;
    }
}

