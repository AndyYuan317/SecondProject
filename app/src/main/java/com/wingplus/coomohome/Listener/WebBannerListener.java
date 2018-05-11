package com.wingplus.coomohome.Listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CategoryActivity;
import com.wingplus.coomohome.activity.CouponActivity;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.PromotionActivity;
import com.wingplus.coomohome.activity.RegisterActivity;
import com.wingplus.coomohome.activity.WebActivity;
import com.wingplus.coomohome.config.Constant;

/**
 * @author leaffun.
 *         Create on 2017/10/21.
 */
public class WebBannerListener {

    public static View.OnClickListener getListener(final Activity context, final int type, final String params) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (type) {
                    case Constant.BannerType.PURE_IMAGE:
                        break;
                    case Constant.BannerType.ACTIVITY_DETAIL:
                        intent = new Intent(context, PromotionActivity.class);
                        intent.putExtra(Constant.Key.KEY_ACTIVITY_NAME, params);
                        context.startActivity(intent);
                        break;
                    case Constant.BannerType.GOOD_DETAIL:
                        intent = new Intent(context, GoodsDetailActivity.class);
                        intent.putExtra(Constant.Key.KEY_GOOD_ID_TYPE, Constant.GoodDetailIdType.SN);//从图片调转的详情是sn
                        intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, params);
                        context.startActivity(intent);
                        break;
                    case Constant.BannerType.GOOD_LIST:
                        intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, params);
                        context.startActivity(intent);
                        break;
                    case Constant.BannerType.WEB:
                        intent = new Intent(context, WebActivity.class);
                        intent.putExtra(Constant.Key.KEY_WEB_URL, params);
                        context.startActivity(intent);
                        break;
                    case Constant.BannerType.REG:
                        intent = new Intent(context, RegisterActivity.class);
                        context.startActivity(intent);
                        break;
                    case Constant.BannerType.COUPON:
                        intent = new Intent(context, CouponActivity.class);
                        context.startActivity(intent);
                        break;
                    default:
                        break;
                }
                context.overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            }
        };
    }
}
