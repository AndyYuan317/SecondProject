package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.ZoomImageView;

/**
 * 放大缩小
 *
 * @author leaffun.
 *         Create on 2017/9/22.
 */
public class ZoomImageActivity extends BaseActivity implements View.OnClickListener {

    private String imageResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_img);
        imageResource = getIntent().getStringExtra("resource");
        initView();
    }

    private void initView() {
        final ZoomImageView img = findViewById(R.id.img);
        Glide.with(ZoomImageActivity.this).asBitmap().load(imageResource).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                img.setImage(resource);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }
}
