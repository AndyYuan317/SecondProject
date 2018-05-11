package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.WebBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 买手精选页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class RecommendActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private RecyclerView recycler;
    private List<GoodShow> list;
    private List<WebBanner> bannerData = new ArrayList<>();

//    private RecommendAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
//        initView();
//        initData();

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

//    private void initView() {
//        back = findViewById(R.id.back);
//        recycler = findViewById(R.id.recommend);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler.setLayoutManager(layoutManager);
//        adapter = new RecommendAdapter();
//        recycler.setAdapter(adapter);
//    }
//
//
//    private void initData() {
//        APIConfig.getDataIntoView(new Runnable() {
//            @Override
//            public void run() {
//                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ACTIVITY_INFO),
//                        new ParamsBuilder()
//                                .addParam("token", RuntimeConfig.user.getToken())
//                                .addParam("activityName", "买手精选")
//                                .getParams());//默认是：买手精选
//                final ActivityInfoResult result = GsonUtil.fromJson(rs, ActivityInfoResult.class);
//                UIUtils.runOnUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
//                            list = result.getData().getGoods();
//                            WebBanner banner = new WebBanner(result.getData().getBanner(), "index.xml");
//                            banner.setName("纯图");
//                            banner.setType(Constant.BannerType.PURE_IMAGE);
//                            bannerData.add(banner);
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            ToastUtil.toastByCode(result);
//                        }
//                    }
//                });
//
//            }
//        });
//    }
//
//    class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            if (viewType == 0) {
//                return new BannerVH(LayoutInflater.from(RecommendActivity.this).inflate(R.layout.item_recommend_img, parent, false));
//            }
//            return new GoodViewHolder(LayoutInflater.from(RecommendActivity.this).inflate(R.layout.item_promotion_good, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            if (holder instanceof GoodViewHolder) {
//                GoodViewHolder gVH = (GoodViewHolder) holder;
//                GoodShow goodIntro = list.get(position - 1);
//                GlideUtil.GlideInstance(RecommendActivity.this, goodIntro.getImgUrl().get(0))
//                        .apply(new RequestOptions().placeholder(R.drawable.load_err_empty))
//                        .into(gVH.good_img);
//                gVH.good_name.setText(goodIntro.getName());
//                gVH.good_intro.setText(goodIntro.getDescription());
//                gVH.good_price.setText(String.valueOf(goodIntro.getPrice() + ""));
//                gVH.setData(goodIntro.getId());
//            } else if (holder instanceof BannerVH) {
//                ((BannerVH) holder).setData(bannerData);
//            }
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return (list == null ? 0 : list.size()) + 1;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position == 0 ? 0 : 1;
//        }
//    }
//
//    class GoodViewHolder extends RecyclerView.ViewHolder {
//        private CardView good_content;
//        private ResizableImageView good_img;
//        private TextView good_name;
//        private TextView good_intro;
//        private SpannableTextView good_price;
//        private TextView good_buy_now;
//
//        private String id;
//
//        public GoodViewHolder(View itemView) {
//            super(itemView);
//
//            good_content = itemView.findViewById(R.id.good_content);
//            good_img = itemView.findViewById(R.id.good_img);
//            good_name = itemView.findViewById(R.id.good_name);
//            good_intro = itemView.findViewById(R.id.good_intro);
//            good_price = itemView.findViewById(R.id.good_price);
//            good_buy_now = itemView.findViewById(R.id.good_buy_now);
//
//            good_buy_now.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ToastUtil.toast(RecommendActivity.this, "立即购买");
//                    Intent intent = new Intent(RecommendActivity.this, MineOrderActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
//                }
//            });
//            good_content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(RecommendActivity.this, GoodsDetailActivity.class);
//                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, id);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
//                }
//            });
//        }
//
//        private void setData(long id){
//            this.id = String.valueOf(id);
//        }
//    }
//
//    class BannerVH extends RecyclerView.ViewHolder {
//        private AutoScrollViewPager recommend_banner;
//        private CirclePageIndicator recommend_banner_indicator;
//
//        public BannerVH(View itemView) {
//            super(itemView);
//            recommend_banner = itemView.findViewById(R.id.recommend_banner);
//            recommend_banner_indicator = itemView.findViewById(R.id.recommend_banner_indicator);
//        }
//
//        public void setData(List<WebBanner> banners) {
//            //准备轮播
//            BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
//            recommend_banner.setCycle(true);
//            recommend_banner.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
//            recommend_banner.setAdapter(adapter);
//            recommend_banner_indicator.setSnap(true);
//            recommend_banner_indicator.setViewPager(recommend_banner, 1);
//            recommend_banner.startAutoScroll();
//        }
//    }
//
//    /**
//     * -----------------------顶部banner----------------
//     */
//    private void prepareBannerCycle(List<WebBanner> bannerData) {
//        //leaffun: autoScrollViewPager开启无限循环模式，需要前后增加两个数据。
//        bannerData.add(bannerData.get(0));
//        bannerData.add(0, bannerData.get(bannerData.size() - 2));
//    }
//
//    private class BannerPagerAdapter extends PagerAdapter {
//
//        private List<WebBanner> webBanners = null;
//
//
//        public BannerPagerAdapter(List<WebBanner> webBanners) {
//            this.webBanners = webBanners;
//        }
//
//        @Override
//        public int getCount() {
//            return webBanners == null ? 0 : webBanners.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View view = LayoutInflater.from(RecommendActivity.this).inflate(R.layout.item_home_banner, null);
//            ImageView image = view.findViewById(R.id.img);
//
//            GlideUtil.GlideWithPlaceHolder(RecommendActivity.this, "http://upload-images.jianshu.io/upload_images/1693344-20f985250e555b6b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240")
//                    .into(image);
//            container.addView(view);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//    }
}
