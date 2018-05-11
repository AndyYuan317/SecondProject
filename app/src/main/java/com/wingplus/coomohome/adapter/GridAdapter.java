package com.wingplus.coomohome.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.ZoomImageActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.MyGlide;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.UIUtils;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private List<String> imgs;
    private Activity activity;

    public GridAdapter(List<String> imgs, Activity activity) {
        this.imgs = imgs;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return imgs == null ? 0 : imgs.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View inflate = LinearLayout.inflate(activity, R.layout.item_good_detail_commit_grid_img, null);
        final ImageView img = inflate.findViewById(R.id.imageView);
        GlideUtil.GlideInstance(MallApplication.getContext(), imgs.get(i))
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(MallApplication.getContext(), UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(img);

        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zoomInt = new Intent(activity, ZoomImageActivity.class);
                zoomInt.putExtra("resource", imgs.get(i));
                activity.startActivity(zoomInt);
            }
        });
        return inflate;
    }
}