package com.wingplus.coomohome.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wingplus.coomohome.Listener.WebBannerListener;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CartActivity;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.activity.MainActivity;
import com.wingplus.coomohome.activity.PromotionActivity;
import com.wingplus.coomohome.activity.RecommendActivity;
import com.wingplus.coomohome.activity.RegularActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.AutoScrollViewPager;
import com.wingplus.coomohome.component.CirclePageIndicator;
import com.wingplus.coomohome.component.ResizableImageView;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
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
 * 活动页Fragment
 *
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class PromotionFragment extends BaseFragment {

    private boolean dataDownSuccess = false;

    private LayoutInflater myInflater;
    private View rootView;
    private RecyclerView recycler;
    private Context context;
    private PromotionAdapter adapter;

    private List<GoodShow> list;
    private List<WebBanner> promotionImgUrl;
    private long activityId;
    private TextView promotion_title;


    public PromotionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflater = inflater;
        context = getContext();
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_main_surprise, container, false);
        }
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataDownSuccess) {
            initData();
        }
    }

    private void initView() {
        ImageView promotion_back = rootView.findViewById(R.id.promotion_back);
        promotion_title = rootView.findViewById(R.id.tool_text);
        recycler = rootView.findViewById(R.id.promotion_goods);

        promotion_back.setVisibility(getActivity() != null
                && (getActivity() instanceof PromotionActivity || getActivity() instanceof RecommendActivity)
                ? View.VISIBLE : View.GONE);
        promotion_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        adapter = new PromotionAdapter();
        recycler.setAdapter(adapter);
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String name;
                if (getActivity() instanceof RecommendActivity) {
                    name = "买手精选";

                } else {
                    //默认是：专题活动
                    String activityName = getActivity().getIntent().getStringExtra(Constant.Key.KEY_ACTIVITY_NAME);
                    name = (activityName == null || activityName.isEmpty() ? "专题活动" : activityName);
                }

                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ACTIVITY_INFO),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("activityName", name)
                                .getParams());
                final ActivityInfoResult result = GsonUtil.fromJson(rs, ActivityInfoResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            dataDownSuccess = true;
                            promotion_title.setText(result.getData().getName());
                            if (getActivity() instanceof MainActivity && RuntimeConfig.tabGroup != null && MallApplication.hasPromotion) {
                                RuntimeConfig.tabGroup.updateLabel(2, "买手精选".equals(result.getData().getName()) ? "买手精选" : "活动");
                            }
                            list = result.getData().getGoods();
                            activityId = result.getData().getId();
                            promotionImgUrl = result.getData().getBanner();
                            prepareBannerCycle(promotionImgUrl);
                            adapter.notifyDataSetChanged();
                        } else {
                            //没有活动时不提示错误信息
//                            ToastUtil.toastByCode(result);
                        }
                    }
                });

            }
        });


    }


    @Override
    public void setStatusBarColor(Activity activity) {

    }

    private class PromotionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ImgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_promotion_img, parent, false));
            } else if (viewType == 0) {
                return new BannerVH(LayoutInflater.from(context).inflate(R.layout.item_home_recyc_banner, parent, false));
            } else {
                return new GoodViewHolder(LayoutInflater.from(context).inflate(R.layout.item_promotion_good, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GoodViewHolder) {
                GoodViewHolder gVH = (GoodViewHolder) holder;
                GoodShow goodShow = list.get(position - 1);
                GlideUtil.GlideInstance(context, goodShow.getImgUrl().get(0))
                        .apply(new RequestOptions().placeholder(R.drawable.load_err_empty))
                        .into(gVH.good_img);
                gVH.good_name.setText(goodShow.getName());
                gVH.good_intro.setText(goodShow.getDescription());
                gVH.good_price.setText(PriceFixUtil.format(goodShow.getPrice()));
                gVH.setData(goodShow);
            } else if (holder instanceof ImgViewHolder) {
                ((ImgViewHolder) holder).setData();
            } else if (holder instanceof BannerVH) {
                ((BannerVH) holder).setData(promotionImgUrl);
            }
        }

        @Override
        public int getItemCount() {
            return (list == null ? 0 : list.size()) + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                String title = promotion_title.getText().toString().trim();
                if ("买手精选".equals(title)) {
                    return 0;
                } else {
                    //活动
                    return 1;
                }
            }
            return 2;
        }
    }

    private class BannerVH extends RecyclerView.ViewHolder {
        private AutoScrollViewPager home_banner;
        private CirclePageIndicator home_banner_indicator;

        public BannerVH(View itemView) {
            super(itemView);
            home_banner = itemView.findViewById(R.id.home_banner);
            home_banner_indicator = itemView.findViewById(R.id.home_banner_indicator);

            ViewGroup.LayoutParams layoutParams = home_banner.getLayoutParams();
            layoutParams.width = RuntimeConfig.SCREEN_WIDTH;
            layoutParams.height = layoutParams.width * 5 / 8;
            home_banner.setLayoutParams(layoutParams);
        }

        public void setData(List<WebBanner> banners) {
            //准备轮播
            BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
            home_banner.setCycle(true);
            home_banner.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
            home_banner.setAdapter(adapter);
            home_banner_indicator.setSnap(true);
            home_banner_indicator.setViewPager(home_banner, 1);
            home_banner.startAutoScroll();
        }
    }

    private class ImgViewHolder extends RecyclerView.ViewHolder {
        private ResizableImageView promotionImg;
        private TextView regular;

        ImgViewHolder(View itemView) {
            super(itemView);
            promotionImg = itemView.findViewById(R.id.promotion_img);
            regular = itemView.findViewById(R.id.promotion_regular);
            regular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RegularActivity.class);
                    intent.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_ACTIVITY_REGULAR);
                    intent.putExtra(Constant.Key.KEY_ACTIVITY_ID, activityId);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }

        public void setData() {
            if(promotionImgUrl != null && promotionImgUrl.size()>0){
                GlideUtil.GlideWithPlaceHolder(context, promotionImgUrl.get(0).getImgUrl()).into(promotionImg);
            }
            String title = promotion_title.getText().toString().trim();
            if ("买手精选".equals(title) || "".equals(title)) {
                regular.setVisibility(View.GONE);
            }else{
                regular.setVisibility(View.VISIBLE);
            }
        }
    }

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
                        Intent login = new Intent(getContext(), LoginActivity.class);
                        startActivity(login);
                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, getActivity());
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
                            MallApplication.showProgressDialog(false, getActivity());
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                UIUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(context, CartActivity.class);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
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
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, "" + id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }

        private void setData(GoodShow gs) {
            id = gs.getId();
            productId = gs.getProductId();
        }
    }

    private class BannerPagerAdapter extends PagerAdapter {

        private List<WebBanner> webBanners = null;


        public BannerPagerAdapter(List<WebBanner> webBanners) {
            this.webBanners = webBanners;
        }

        @Override
        public int getCount() {
            return webBanners == null ? 0 : webBanners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = myInflater.inflate(R.layout.item_home_banner, null);
            ImageView image = view.findViewById(R.id.img);
            Glide.with(context)
                    .load(webBanners.get(position).getImgUrl())
                    .apply(new RequestOptions().dontAnimate()
                            .skipMemoryCache(false)).into(image);
            image.setOnClickListener(WebBannerListener.getListener(getActivity(), webBanners.get(position).getType(), webBanners.get(position).getParams()));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private void prepareBannerCycle(List<WebBanner> bannerData) {
        //leaffun: autoScrollViewPager开启无限循环模式，需要前后增加两个数据。
        if (bannerData == null || bannerData.size() == 0) {
            return;
        }
        bannerData.add(bannerData.get(0));
        bannerData.add(0, bannerData.get(bannerData.size() - 2));
    }
}
