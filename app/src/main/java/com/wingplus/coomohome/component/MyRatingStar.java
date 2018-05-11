package com.wingplus.coomohome.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.util.DisplayUtil;

/**
 * 评星
 *
 * @author leaffun.
 *         Create on 2017/9/22.
 */
public class MyRatingStar extends LinearLayout {
    private int stars = 5;
    private boolean canChosen = true;
    private int starSize = 15;

    private int rating;

    private UpdateRatingListener updateRatingListener;

    public MyRatingStar(Context context) {
        super(context);
        initView(context);
    }

    public MyRatingStar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyRatingStar);// 由attrs 获得 TypeArray
        canChosen = ta.getBoolean(R.styleable.MyRatingStar_can_chose, true);
        starSize = ta.getDimensionPixelSize(R.styleable.MyRatingStar_star_size, 15);
        ta.recycle();
        initView(context);
    }

    public MyRatingStar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyRatingStar);// 由attrs 获得 TypeArray
        canChosen = ta.getBoolean(R.styleable.MyRatingStar_can_chose, true);
        starSize = ta.getDimensionPixelSize(R.styleable.MyRatingStar_star_size, 15);
        ta.recycle();
        initView(context);
    }


    private void initView(Context context) {
        for (int i = 0; i < stars; i++) {
            SquareImage star = new SquareImage(context);
            LayoutParams params = new LayoutParams(starSize, starSize);
            int px = DisplayUtil.dip2px(context, 3);
            params.setMargins(px, 0, px, 0);
            star.setLayoutParams(params);
            star.setBackground(context.getResources().getDrawable(R.drawable.icon_lisr_starno));
            final int finalI = i;
            if (canChosen) {
                star.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setRating(finalI + 1);
                    }
                });
            }
            addView(star);
        }
    }

    /**
     * 设置总星星数
     * @param num
     */
    private void setStars(int num) {
        stars = num;
        invalidate();
    }

    /**
     * 评星
     * @param num 点亮星星数
     */
    public void setRating(int num) {
        for (int i = 0; i < stars; i++) {
            if (i < getChildCount()) {
                getChildAt(i).setBackground(getResources().getDrawable(i < num ? R.drawable.icon_lisr_starlight : R.drawable.icon_lisr_starno));
            }
        }
        rating = num;
        if(updateRatingListener != null){
            updateRatingListener.updateRating(rating);
        }

    }

    public int getRatingNum(){
        return rating;
    }

    public void setUpdateRatingListener(UpdateRatingListener updateRatingListener){
        this.updateRatingListener = updateRatingListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    public interface UpdateRatingListener{
        void updateRating(int rating);
    }
}
