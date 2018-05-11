package com.wingplus.coomohome.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.ImageHelper;
import com.wingplus.coomohome.util.UIUtils;


/**
 * 显示可删除按钮的图像
 * Created by wangyn on 16/5/10.
 */
public class RemovableImage extends FrameLayout {

    private SquareImage mImage;
    private ImageView mDelete;

    private Drawable mDrawable;
    private String imageFile;

    private OnRemovableListener mOnRemovableListener;

    public RemovableImage(Context context) {
        this(context, null);
    }

    public RemovableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_removable_image, this);
        mImage = findViewById(R.id.image);
        mDelete = findViewById(R.id.btnDelete);
        Glide.with(context)
                .load(R.drawable.delete_j)
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(context, UIUtils.dip2px(12), RoundCornersTransformation.CornerType.ALL)))
                .into(mDelete);
    }

    public void setImageBitmap(Bitmap bitmap) {
        setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    public void setImageDrawable(Drawable drawable) {
        mDrawable = drawable;
        initView();
    }

    public void setImageResource(int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }

    public void setImageFile(String filePath) {
        imageFile = filePath;
        Glide.with(getContext()).asBitmap().load(imageFile).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                LogUtil.e("removeable图片申请内存大小", "" + resource.getAllocationByteCount());
                setImageBitmap(resource);
            }
        });
    }

    public void setOnRemovableListener(OnRemovableListener listener) {
        mOnRemovableListener = listener;
        if (mOnRemovableListener != null) {
            mDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRemovableListener.remove(RemovableImage.this);
                }
            });
        }
    }

    public String getImageFileName() {
        return imageFile;
    }

    private void initView() {
        if (mDrawable != null) {
            mImage.setImageDrawable(mDrawable);
        }
    }

    public interface OnRemovableListener {
        void remove(RemovableImage me);
    }
}
