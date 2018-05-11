package com.wingplus.coomohome.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.DisplayUtil;

/**
 * 竖直虚线
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class DashedLineView extends View {
    public Context context;
    private Paint paint;
    private Path path;
    private PathEffect effects;

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        path = new Path();
        effects = new DashPathEffect(new float[]{10, 4, 10, 4}, 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.coupon_time));
        paint.setStrokeWidth(DisplayUtil.dip2px(context, 2));

        path.moveTo(0, 0);
        path.lineTo(0, getMeasuredHeight());

        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

}
