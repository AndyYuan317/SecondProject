package com.wingplus.coomohome.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.wingplus.coomohome.R;

/**
 * @author leaffun.
 *         Create on 2017/9/14.
 */
public class ResizableImageView extends android.support.v7.widget.AppCompatImageView {

    private float heightScale;

    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ResizableImageView);// 由attrs 获得 TypeArray
        heightScale = ta.getFloat(R.styleable.ResizableImageView_height_scale, -1.0f);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = MeasureSpec.getSize(widthMeasureSpec);
            //高度根据使得图片的宽度充满屏幕计算而得
            int height = (int) Math.ceil((float) width * (heightScale <= -0.0f ? (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth() : heightScale));
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
