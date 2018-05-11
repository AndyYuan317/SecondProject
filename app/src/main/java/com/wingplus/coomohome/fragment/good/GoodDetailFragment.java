package com.wingplus.coomohome.fragment.good;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.just.library.AgentWebView;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.PromotionActivity;
import com.wingplus.coomohome.adapter.LikeVH;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.component.StrikeTextView;
import com.wingplus.coomohome.component.ToggleViewPager;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.NetUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodActivityEntity;
import com.wingplus.coomohome.web.entity.GoodDetail;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.GoodSpec;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.GoodActivityResult;
import com.wingplus.coomohome.web.result.GoodDetailResult;
import com.wingplus.coomohome.web.result.GuessLikeResult;
import com.wingplus.coomohome.web.result.StringDataResult;
import com.wingplus.coomohome.widget.ijk.IjkVideoView;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.wingplus.coomohome.util.TestDataProduceUtil.url;

/**
 * 商品详情-详情
 *
 * @author leaffun.
 *         Create on 2017/9/14.
 */
public class GoodDetailFragment extends Fragment {

    private AgentWebView webView;
    private int[] combind = {R.drawable.load_err_empty, R.drawable.load_err_empty};
    private String NowattrStr = "请选择规格、数量";

    private Context context;
    private View rootView;
    private RecyclerView recycler;
    private MyAdapter adapter;

    /**
     * 本页数据
     */
    private List<String> bannerData;
    private GoodDetail goodDetail;
    private GoodSpec gs;
    private String html = "";
    private List<GoodActivityEntity> activities;

    private List<GoodShow> mLike;
    private double[] minAndMax;
    private boolean videoAdd = false;


    public static GoodDetailFragment newInstance() {

        return new GoodDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_good_detail, container, false);
        context = getContext();
        initView();
        initEvent();
        initData();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (!NetUtil.IsActivityNetWork(context)) {
                getActivity().finish();
            }
            if (webView != null) {
                webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (webView != null) {
                webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initView() {
        recycler = rootView.findViewById(R.id.all_content);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layout);
        adapter = new MyAdapter();
        recycler.setAdapter(adapter);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findLastVisibleItemPosition();
                if (videoAdd && position >= 4 && html.length() > 0) {
                    View view = layoutManager.findViewByPosition(4);
                    if (view == null) return;
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(4);
                    if (holder == null) return;
                    boolean b = holder instanceof ImgViewHolder;
                    final int height = view.getHeight();
                    boolean b1 = height > 100;//8
                    if (b && b1) {
                        APIConfig.getDataIntoView(new Runnable() {
                            @Override
                            public void run() {
                                UIUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        videoAdd = false;
                                        LogUtil.e("videoHeight", height + "");
                                        adapter.notifyDataSetChanged();
                                    }
                                }, 100);
                            }
                        });
                    }
                }
            }
        });
    }

    private void initEvent() {
        ((GoodsDetailActivity) getActivity()).setChangeSKUListener(new GoodsDetailActivity.ChangeSKUListener() {
            @Override
            public void changeSKU(GoodSpec goodSpec) {
                gs = goodSpec;
                if (gs != null) {
                    minAndMax = new double[]{-1, -1};
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void setAttr(String attrStr) {
                NowattrStr = attrStr;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                final String type;
                final String api;
                if (Constant.GoodDetailIdType.SN.equals(getActivity().getIntent().getStringExtra(Constant.Key.KEY_GOOD_ID_TYPE))) {
                    type = "sn";
                    api = APIConfig.API_GOODS_DETAIL_BY_SN;
                } else {
                    type = "id";//其他为空的地方都是id
                    api = APIConfig.API_GOODS_DETAIL;
                }

                String idOrSnTemp = getActivity().getIntent().getStringExtra(Constant.Key.KEY_GOOD_ID_OR_SN);
                if (idOrSnTemp == null || idOrSnTemp.length() <= 0) {
                    Uri uri = getActivity().getIntent().getData();
                    if (uri != null) {
                        idOrSnTemp = uri.getQueryParameter("goodsId");
                    }
                }
                final String idOrSn = idOrSnTemp;
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(api),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam(type, idOrSn)
                                .getParams());
                final GoodDetailResult goodDetailResult = GsonUtil.fromJson(rs, GoodDetailResult.class);
                if (goodDetailResult != null && goodDetailResult.getResult() == APIConfig.CODE_SUCCESS) {
                    goodDetail = goodDetailResult.getData();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (goodDetail != null && goodDetail.getImgUrl() != null) {
                                if (goodDetail.getImgUrl().size() > 1) {
                                    prepareBannerCycle(goodDetail.getImgUrl());
                                } else {
                                    bannerData = goodDetail.getImgUrl();
                                }

                                //使用spec,方便属性更改时刷新页面
                                for (GoodSpec spec : goodDetail.getSpecList()) {
                                    if (spec.getProductId().equals(goodDetail.getProductId())) {
                                        gs = spec;
                                        break;
                                    }
                                }
                                GoodsDetailActivity activity = (GoodsDetailActivity) getActivity();
                                if(activity != null){
                                    minAndMax = activity.initData(goodDetail);
                                }
                                if (gs != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                    if (goodDetail != null && goodDetail.getImgUrl() != null) {
                        getDescription(goodDetail.getId());
                        getPromotion(goodDetail.getId());
                        getPopularGoods();
                    }

                } else {
                    ToastUtil.toastByCode(goodDetailResult);
                }
            }
        });
    }

    //获取商品详情富文本
    private void getDescription(final long id) {
        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_DESCRIPTION),
                new ParamsBuilder()
                        .addParam("token", RuntimeConfig.user.getToken())
                        .addParam("id", String.valueOf(id))
                        .getParams());
        final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
            if (result.getData() != null && result.getData().length() > 0) {
                html = "<style> p{-webkit-margin-before:0px;-webkit-margin-after:0px;padding:0px;} body{margin:0px;} </style>" + result.getData();
                if (html.contains("<video") && html.contains("<script>")) {
                    //方案1：js注入重置宽高
                    html = html.replace("<script>var video = document.getElementsByTagName(\"video\")[0];video.width = document.body.clientWidth;video.height = document.body.clientWidth / 16 * 9;</script>"
                            , "<script>video.addEventListener('loadstart', function(){" +
                                    "video.width = " + RuntimeConfig.SCREEN_WIDTH + ";" +
                                    "video.height = " + RuntimeConfig.SCREEN_WIDTH / 16 * 9 + ";}" +
                                    "</script>");

                    //方案2：另增16:9海报占位
                    html = html.replace("<video", "<video poster=\"http://118.31.18.245:8080/coomohome/statics/attachment/ueditor/2017/12/15/17//12284284.png\"");

//                    //方案3：另增16:9父容器限制
//                    html = html.replace("<video", "<p style=\"display:block;width:'" + RuntimeConfig.SCREEN_WIDTH + "';height:'" + RuntimeConfig.SCREEN_WIDTH / 16 * 9 + "';\"><video");
//                    html = html.replace("</video>", "</video></p>");
//                    html = html.replace("preload=\"none\" width=\"100%\" height=\"auto\"", "preload=\"none\" width=\"100%\" height=\""+RuntimeConfig.SCREEN_WIDTH/16*9+"\"");
                }
            }
        }
    }

    //获取商品活动
    private void getPromotion(final long id) {
        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_ACTIVITY),
                new ParamsBuilder()
                        .addParam("token", RuntimeConfig.user.getToken())
                        .addParam("id", String.valueOf(id))
                        .getParams());
        final GoodActivityResult result = GsonUtil.fromJson(rs, GoodActivityResult.class);
        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
            if (result.getData().getActivity() != null) {
                activities = result.getData().getActivity();
            }
        }
    }

    private void getPopularGoods() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_POPULAR)
                        , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken()).getParams());
                GuessLikeResult result = GsonUtil.fromJson(rs, GuessLikeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    mLike = result.getData();
                } else {
                    ToastUtil.toastByCode(result);
                }
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void prepareBannerCycle(List<String> banner) {
        //leaffun: autoScrollViewPager开启无限循环模式，需要前后增加两个数据。
        if (banner != null && banner.size() > 0) {
            bannerData = new ArrayList<>();
            for (String s : banner) {
                bannerData.add(s);
            }
            bannerData.add(bannerData.get(0));
            bannerData.add(0, bannerData.get(bannerData.size() - 2));
        }
    }

    /**
     * -------------------------------------商品图片轮播适配器-----------------
     */
    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return bannerData == null ? 0 : bannerData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_banner, null);
            ImageView image = view.findViewById(R.id.img);
            GlideUtil.GlideWithPlaceHolder(context, bannerData.get(position))
                    .apply(new RequestOptions().dontAnimate().dontTransform())
                    .into(image);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * --------------------------页面主适配器----------------------
     */
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new BannerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_banner, parent, false));
            }
            if (viewType == 1) {
                return new IntroViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_intro, parent, false));
            }
            if (viewType == 2) {
                return new PromotionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_promotion, parent, false));
            }
            if (viewType == 1113) {
                return new CombinationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_combination, parent, false));
            }
            if (viewType == 1114) {
                return new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_video, parent, false));
            }
            if (viewType == 3) {
                return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_imgs_title, parent, false));
            }
            if (viewType == 4) {
                return new ImgViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_recyc_img, parent, false));
            }
            if (viewType >= 5) {
                return new LikeVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_cart_like, parent, false), getActivity(), "大家都在买");
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof BannerViewHolder) {
                final BannerViewHolder banner = (BannerViewHolder) holder;
                final BannerAdapter adapter = new BannerAdapter();
                banner.good_detail_banner.setAdapter(adapter);
                banner.good_detail_banner.setCanScroll(bannerData != null && bannerData.size() > 1);
                banner.indicator.setText("1/1");
                banner.good_detail_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        int p = 0;
                        if (position == 0) {
                            p = adapter.getCount() - 2;
                        } else if (position == adapter.getCount() - 1) {
                            p = 1;
                        } else {
                            p = position;
                        }
                        String text = p + "/" + (adapter.getCount() - 2);
                        banner.indicator.setText(text);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        switch (state) {
                            case ViewPager.SCROLL_STATE_IDLE:// 空闲状态，没有任何滚动正在进行（表明完成滚动）
                                final int pageCount = adapter.getCount();
                                if (banner.good_detail_banner.getCurrentItem() == pageCount - 1) {
                                    banner.good_detail_banner.setCurrentItem(1, false);
                                }
                                if (banner.good_detail_banner.getCurrentItem() == 0) {
                                    banner.good_detail_banner.setCurrentItem(pageCount - 2, false);
                                }

                                break;
                            case ViewPager.SCROLL_STATE_DRAGGING:// 正在拖动page状态
                                break;
                            case ViewPager.SCROLL_STATE_SETTLING:// 手指已离开屏幕，自动完成剩余的动画效果
                                break;
                        }
                    }
                });
                banner.good_detail_banner.setCurrentItem((bannerData != null && bannerData.size() > 1) ? 1 : 0);

            } else if (holder instanceof IntroViewHolder) {
                if (goodDetail == null || gs == null) {
                    return;
                }
                IntroViewHolder introVH = (IntroViewHolder) holder;
                introVH.goodName.setText(goodDetail.getName());
                introVH.description.setText(goodDetail.getDescription());


                if (minAndMax[0] != -1 && minAndMax[1] != -1) {
                    introVH.price.setPreString(getResources().getString(R.string.currency_symbol));
                    introVH.price.setText(PriceFixUtil.format(minAndMax[0]) + " ~ " + PriceFixUtil.format(minAndMax[1]));
                } else {
                    introVH.price.setPreString("");
                    introVH.price.setText(PriceFixUtil.format(gs.getPrice()));
                }


                if (PriceFixUtil.checkNeedScribing(gs.getPrice(), goodDetail.getOriginalPrice())) {
                    introVH.originalPrice.setVisibility(View.VISIBLE);
                    introVH.originalPrice.setText(PriceFixUtil.format(goodDetail.getOriginalPrice()));
                } else {
                    introVH.originalPrice.setVisibility(View.GONE);
                }
                introVH.place.setText(goodDetail.getPlace());
                introVH.pleaseChoseAttr.setText(NowattrStr.length() > 0 ? NowattrStr : StringUtil.bindStr(gs.getSpec()));
                introVH.chosen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GoodsDetailActivity) getActivity()).showPurchase();
                    }
                });

            } else if (holder instanceof PromotionViewHolder) {
                ((PromotionViewHolder) holder).setData();

            } else if (holder instanceof CombinationViewHolder) {
                setVisibility(false, holder.itemView);
                CombinationViewHolder combind = (CombinationViewHolder) holder;
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                combind.combination.setLayoutManager(layoutManager);
                combind.combination.setAdapter(new CombinationAdapter());

            } else if (holder instanceof VideoViewHolder) {
                final VideoViewHolder videoVH = (VideoViewHolder) holder;
                IjkMediaPlayer.loadLibrariesOnce(null);
                IjkMediaPlayer.native_profileBegin("libijkplayer.so");
                HttpProxyCacheServer proxy = MallApplication.getProxy(context);
                String proxyUrl = proxy.getProxyUrl(url);
                videoVH.good_detail_video.setVideoPath(proxyUrl);
                videoVH.good_detail_video.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                videoVH.good_detail_video.start();
                videoVH.good_detail_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (videoVH.good_detail_video.isPlaying()) {
                            videoVH.good_detail_video.pause();
                        } else {
                            videoVH.good_detail_video.start();
                        }
                    }
                });

            } else if (holder instanceof TitleViewHolder) {
//                holder.itemView.setVisibility(View.GONE);
                ((TitleViewHolder) holder).title.setText("商品介绍");

            } else if (holder instanceof ImgViewHolder) {
                ((ImgViewHolder) holder).setData();
            } else if (holder instanceof LikeVH) {
                ((LikeVH) holder).refreshLikeData(mLike);
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    //////////////-----------页面主适配器holder-----------------////////////////////////

    private class BannerViewHolder extends RecyclerView.ViewHolder {
        private ToggleViewPager good_detail_banner;
        private TextView indicator;

        public BannerViewHolder(View itemView) {
            super(itemView);
            good_detail_banner = itemView.findViewById(R.id.good_detail_banner);
            indicator = itemView.findViewById(R.id.indicator);
            good_detail_banner.setCanScroll(true);

        }
    }

    private class IntroViewHolder extends RecyclerView.ViewHolder {
        private final TextView goodName;
        private final TextView description;
        private final SpannableTextView price;
        private final TextView originalPrice;
        private final TextView place;
        private final TextView pleaseChoseAttr;
        private final LinearLayout chosen;

        IntroViewHolder(View itemView) {
            super(itemView);
            goodName = itemView.findViewById(R.id.good_detail_name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            place = itemView.findViewById(R.id.place);
            pleaseChoseAttr = itemView.findViewById(R.id.please_chose_attr);
            chosen = itemView.findViewById(R.id.chosen);
        }
    }

    private class PromotionViewHolder extends RecyclerView.ViewHolder {
        private final TextView specTag;
        private final TextView specTxt;
        private final TextView dailyTag;
        private final TextView dailyTxt;
        private final LinearLayout promotion;
        private final LinearLayout normalPromotion;

        PromotionViewHolder(View itemView) {
            super(itemView);
            promotion = itemView.findViewById(R.id.promotion);
            normalPromotion = itemView.findViewById(R.id.normal_promotion);
            specTag = itemView.findViewById(R.id.tag_spec);
            specTxt = itemView.findViewById(R.id.txt_spec);
            dailyTag = itemView.findViewById(R.id.tag_daily);
            dailyTxt = itemView.findViewById(R.id.txt_daily);
        }

        private void setData() {
            promotion.setVisibility(View.GONE);
            normalPromotion.setVisibility(View.GONE);
            if (activities != null && activities.size() > 0) {
                for (GoodActivityEntity entity : activities) {
                    if ("专题活动".equals(entity.getTag())) {
                        promotion.setVisibility(View.VISIBLE);
                        specTag.setText("专题活动");
                        specTxt.setText(entity.getName());
                        promotion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, PromotionActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                            }
                        });
                    } else {
                        normalPromotion.setVisibility(View.VISIBLE);
                        dailyTag.setText("普通活动");
                        dailyTxt.setText(entity.getName());
                    }
                }
            }


        }
    }

    private class CombinationViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView combination;

        CombinationViewHolder(View itemView) {
            super(itemView);
            combination = itemView.findViewById(R.id.combination);
        }


    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {
        private IjkVideoView good_detail_video;
        private ImageView good_detail_play;

        VideoViewHolder(View itemView) {
            super(itemView);
            good_detail_video = itemView.findViewById(R.id.good_detail_video);
            good_detail_play = itemView.findViewById(R.id.good_detail_play);
        }
    }

    private class ImgViewHolder extends RecyclerView.ViewHolder {

        ImgViewHolder(View itemView) {
            super(itemView);

            webView = itemView.findViewById(R.id.img);
//            AgentWeb agentWeb = AgentWeb.with(getActivity())//传入Activity
//                    .setAgentWebParent(ll, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
//                    .useDefaultIndicator()// 使用默认进度条
//                    .defaultProgressBarColor() // 使用默认进度条颜色
//                    .createAgentWeb()//
//                    .ready()
//                    .go(null);
//            img = agentWeb.getWebCreator().get();
        }

        public void setData() {
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setDefaultTextEncodingName("utf-8");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        TitleViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    /**
     * ------------------优惠组合适配器------------------------
     */
    private class CombinationAdapter extends RecyclerView.Adapter<CombindViewHolder> {

        @Override
        public CombindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new CombindViewHolder(LayoutInflater.from(context).inflate(R.layout.item_good_detail_combind, parent, false));
        }

        @Override
        public void onBindViewHolder(final CombindViewHolder holder, int position) {
            holder.combind_name.setText("组合优惠" + (position + 1));
            holder.combind_price_now.setText("3999");
            holder.combind_price_old.setText("5399");
            holder.combind_add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtil.toast(context, holder.combind_name.getText().toString() + "加入购物车");
                }
            });

            if (holder.combind_goods.getChildCount() == 0) {
                LinearLayout.LayoutParams skuParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 100), DisplayUtil.dip2px(context, 100));
                LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 16), DisplayUtil.dip2px(context, 16));
                addParams.setMargins(12, 12, 12, 12);
                int allBindSize = (position % 2 + 2);
                for (int i = 0; i < allBindSize; i++) {
                    //if 不是最后一张
                    ImageView sku = (ImageView) LayoutInflater.from(context).inflate(R.layout.item_good_detail_combind_img, null);
                    sku.setLayoutParams(skuParams);
                    GlideUtil.GlideWithPlaceHolder(context, combind[i % 2]).into(sku);
                    sku.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, GoodsDetailActivity.class);
                            startActivity(intent);
                        }
                    });

                    holder.combind_goods.addView(sku);
                    if (i != allBindSize - 1) {
                        ImageView add = (ImageView) LayoutInflater.from(context).inflate(R.layout.item_good_detail_combind_add, null);
                        add.setLayoutParams(addParams);
                        holder.combind_goods.addView(add);
                    }
                }
            }

        }

        @Override
        public int getItemCount() {

            return 5;
        }
    }

    class CombindViewHolder extends RecyclerView.ViewHolder {
        private TextView combind_name;
        private SpannableTextView combind_price_now;
        private StrikeTextView combind_price_old;
        private ImageView combind_add_cart;
        private LinearLayout combind_goods;

        CombindViewHolder(View itemView) {
            super(itemView);
            combind_name = itemView.findViewById(R.id.combind_name);
            combind_price_now = itemView.findViewById(R.id.combind_price_now);
            combind_price_old = itemView.findViewById(R.id.combind_price_old);
            combind_add_cart = itemView.findViewById(R.id.combind_add_cart);
            combind_goods = itemView.findViewById(R.id.combind_goods);
        }
    }

    public void setVisibility(boolean isVisible, View itemView) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }
}
