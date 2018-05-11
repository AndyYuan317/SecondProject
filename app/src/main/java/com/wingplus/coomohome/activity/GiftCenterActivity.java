package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.AutoScrollViewPager;
import com.wingplus.coomohome.component.CirclePageIndicator;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.component.ResizableImageView;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.component.SquareImage;
import com.wingplus.coomohome.component.StrikeTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.WebBanner;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.ActivityInfoResult;
import com.wingplus.coomohome.web.result.BaseResult;

import java.util.List;

/**
 * 礼品中心
 *
 * @author leaffun.
 *         Create on 2017/10/24.
 */
public class GiftCenterActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv;
    private MyRefreshLayout tr;
    private GiftAdapter adapter;

    /**
     * 礼品集合
     */
    private List<GoodShow> gifts;
    /**
     * banner图片
     */
    private List<WebBanner> imgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gift_center);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        tr = findViewById(R.id.tr);
        rv = findViewById(R.id.rv);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //该元素占几个格子
                if (adapter.getItemViewType(position) == 0) {
                    return 2;
                }
                return 2;
            }
        });
        rv.setLayoutManager(manager);
        adapter = new GiftAdapter();
        rv.setAdapter(adapter);
//        rv.addItemDecoration(new SpaceItemDecoration(1));
    }

    private void initEvent() {
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                initData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                getNextPageData();
            }
        });
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                final String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GIFT_INFO)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                .addParam("activityName", "礼品中心")
                                .getParams());
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        ActivityInfoResult result = GsonUtil.fromJson(rs, ActivityInfoResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            gifts = result.getData().getGoods();
                            imgUrl = result.getData().getBanner();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
            }
        });
    }

    private void getNextPageData() {
        tr.finishLoadmore();
    }

    /**
     * ----------------商品适配器------------------------
     */
    private class GiftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new GiftVH(LayoutInflater.from(GiftCenterActivity.this).inflate(R.layout.vh_activity_gift_center, parent, false));
            }
            return new GoodViewHolder(LayoutInflater.from(GiftCenterActivity.this).inflate(R.layout.item_promotion_good, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GiftVH) {
                ((GiftVH) holder).setData(null);
            } else {
                GoodViewHolder goodVH = (GoodViewHolder) holder;
                if (gifts != null && gifts.size() > 0) {
                    goodVH.setData(gifts.get(position - 1));
                }
            }
        }

        @Override
        public int getItemCount() {
            return gifts != null && gifts.size() > 0 ? (1 + gifts.size()) : 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }
    }

//    private class GoodViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout good;
//        private SquareImage good_img;
//        private TextView good_title;
//        private TextView good_intro;
//        private SpannableTextView good_price;
//        private StrikeTextView good_old_price;
//        private TextView good_label;
//        private ImageView add_cart;
//        private CheckBox good_check;
//
//        private long sku;
//
//        GoodViewHolder(View itemView) {
//            super(itemView);
//            good = itemView.findViewById(R.id.good_1);
////            endLine = itemView.findViewById(R.id.end_line);
//            good_img = itemView.findViewById(R.id.good_img);
//            good_title = itemView.findViewById(R.id.good_title);
//            good_intro = itemView.findViewById(R.id.good_intro);
//            good_price = itemView.findViewById(R.id.good_price);
//            good_old_price = itemView.findViewById(R.id.good_old_price);
//            good_label = itemView.findViewById(R.id.good_label);
//            add_cart = itemView.findViewById(R.id.good_add_cart);
//            good_check = itemView.findViewById(R.id.good_check);
//            add_cart.setVisibility(View.GONE);
//        }
//
//        public void setData(final int pos) {
//            GoodShow goodShow = gifts.get(pos);
//            GlideUtil.GlideWithPlaceHolder(GiftCenterActivity.this, goodShow.getImgUrl().get(0)).into(good_img);
//            good_title.setText(goodShow.getName());
//            good_intro.setText(goodShow.getDescription());
//            good_price.setText(PriceFixUtil.format(goodShow.getPrice()));
//            if (PriceFixUtil.checkNeedScribing(goodShow.getPrice(), goodShow.getOriginalPrice())) {
//                good_old_price.setVisibility(View.VISIBLE);
//                good_old_price.setText(PriceFixUtil.format(goodShow.getOriginalPrice()));
//            } else {
//                good_old_price.setVisibility(View.INVISIBLE);
//            }
//            if (goodShow.getTag() != null && goodShow.getTag().size() > 0) {
//                good_label.setVisibility(View.VISIBLE);
//                good_label.setText(String.valueOf(goodShow.getTag().get(0)));
//            } else {
//                good_label.setVisibility(View.INVISIBLE);
//            }
//            sku = goodShow.getId();
//
//            good.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(GiftCenterActivity.this, GoodsDetailActivity.class);
//                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(sku));
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
//                }
//            });
//        }
//    }

    private class GoodViewHolder extends RecyclerView.ViewHolder {
        private CardView good_content;
        private ResizableImageView good_img;
        private TextView good_name;
        private TextView good_intro;
        private SpannableTextView good_price;
        private TextView good_buy_now;

        private long id;
        private String productId;

        GoodViewHolder(View itemView) {
            super(itemView);

            good_content = itemView.findViewById(R.id.good_content);
            good_img = itemView.findViewById(R.id.good_img);
            good_name = itemView.findViewById(R.id.good_name);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_buy_now = itemView.findViewById(R.id.good_buy_now);

            good_buy_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!RuntimeConfig.userValid()) {
                        Intent login = new Intent(GiftCenterActivity.this, LoginActivity.class);
                        startActivity(login);
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, GiftCenterActivity.this);
                    APIConfig.getDataIntoView(new Runnable() {
                        @Override
                        public void run() {
                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_BUY_NOW)
                                    , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                            .addParam("type", String.valueOf(Constant.GoodType.SINGLE))
                                            .addParam("id", String.valueOf(id))
                                            .addParam("num", "1")
                                            .addParam("productId", productId)
                                            .getParams());
                            BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                            MallApplication.showProgressDialog(false, GiftCenterActivity.this);
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                UIUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(GiftCenterActivity.this, CartActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                                    }
                                });
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });

                }
            });
            good_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GiftCenterActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, "" + id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }

        private void setData(GoodShow gs) {
            id = gs.getId();
            productId = gs.getProductId();
            GlideUtil.GlideInstance(GiftCenterActivity.this, gs.getImgUrl().get(0))
                    .apply(new RequestOptions().placeholder(R.drawable.load_err_empty))
                    .into(good_img);
            good_name.setText(gs.getName());
            good_intro.setText(gs.getDescription());
            good_price.setText(PriceFixUtil.format(gs.getPrice()));
        }
    }


    private class GiftVH extends RecyclerView.ViewHolder {

        private final ImageView imageView;
//        private final AutoScrollViewPager vp;
//        private CirclePageIndicator vpi;

        GiftVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
//            vp = itemView.findViewById(R.id.gift_banner);
//            vpi = itemView.findViewById(R.id.gift_banner_indicator);


//            ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.width = RuntimeConfig.SCREEN_WIDTH;
//            vp.setLayoutParams(layoutParams);
        }

        public void setData(List<WebBanner> banners) {
            //准备轮播
//            BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
//            vp.setCycle(true);
//            vp.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
//            vp.setAdapter(adapter);
//            vpi.setSnap(true);
//            vpi.setViewPager(vp, 1);
//            vp.startAutoScroll();
            GlideUtil.GlideWithPlaceHolder(GiftCenterActivity.this, imgUrl.get(0).getImgUrl()).into(imageView);
        }
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private final Paint paint;

        SpaceItemDecoration(int space) {
            this.space = space;
            paint = new Paint();
            paint.setColor(getResources().getColor(R.color.titleGray));
            paint.setAntiAlias(true);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            if (parent.getChildLayoutPosition(view) == 0) {

            } else {
                outRect.left = space;
                outRect.bottom = space;
                if (parent.getChildLayoutPosition(view) % 2 == 1) {
                    outRect.left = 0;
                }
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            //ondraw 方法是在底层；ondrawover方法是前景层；
            for (int i = 0, len = parent.getLayoutManager().getChildCount(); i < len; i++) {
                final View child = parent.getChildAt(i);


                float left = child.getLeft() - space;
                float top = child.getTop() - space;
                float right = child.getRight() + space;
                float bottom = child.getBottom() + space;

                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
