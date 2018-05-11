package com.wingplus.coomohome.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;

/**
 * @author leaffun.
 *         Create on 2017/11/14.
 */
public class GlideUtil {

    public static RequestBuilder<Drawable> GlideWithPlaceHolder(Context context, Object object) {
        return Glide.with(MallApplication.getContext()).load(object).apply(new RequestOptions().placeholder(R.drawable.load_err_empty).dontAnimate());
    }

    public static RequestBuilder<Drawable> GlideInstance(Context context, Object object) {
        return Glide.with(context).load(object).apply(new RequestOptions().dontAnimate());
    }

    public static RequestBuilder<GifDrawable> GlideGif(Context context, Object object) {
        return Glide.with(MallApplication.getContext()).asGif().apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).load(object);
    }


}
