package com.wingplus.coomohome.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.Listener.WebBannerListener;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CategoryActivity;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.LocationActivity;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.activity.RecommendActivity;
import com.wingplus.coomohome.activity.ScanActivity;
import com.wingplus.coomohome.activity.SearchActivity;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.AutoScrollViewPager;
import com.wingplus.coomohome.component.CirclePageIndicator;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.component.ResizableImageView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.AndroidFileUtil;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.TimeUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.HomeFloor;
import com.wingplus.coomohome.web.entity.WebBanner;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.HomeResult;
import com.wingplus.coomohome.widget.ijk.IjkVideoView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * 首页
 *
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class HomeFragment extends BaseFragment {
    //全局
    private Context context = null;
    private int topParentAlpha = 0;
    private Map<Integer, Bitmap> mImageCache = new HashMap<>();
    private int playState = 0;
    private boolean dataDownSuccess = false;

    private List<WebBanner> bannerData;
    private String videoUrl = "";
    private List<HomeFloor> floorData;

    //视图
    private LayoutInflater myInflater;
    private View rootView;
    private ImageView scan;
    private ImageView location;
    private CardView search;

    private LinearLayout topParent;
    private RecyclerView homeRecycler;

    private VideoVH myVideoVH;
    private MyRefreshLayout tr;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        myInflater = inflater;
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_main_home, container, false);
        }
        initView();
        initEvent();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataDownSuccess) {
            initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myVideoVH != null && myVideoVH.ijkVideoView.isPlaying()) {
            myVideoVH.ijkVideoView.pause();
            playState = -1;
            myVideoVH.setInfoUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        topParentAlpha = topParent.getBackground().mutate().getAlpha();
    }

    @Override
    public void setStatusBarColor(Activity activity) {

    }

    public void initView() {
        topParent = rootView.findViewById(R.id.top_parent);
        location = rootView.findViewById(R.id.location);
        search = rootView.findViewById(R.id.search);
        scan = rootView.findViewById(R.id.scan);
        tr = rootView.findViewById(R.id.tr);

        homeRecycler = rootView.findViewById(R.id.home);
        LinearLayoutManager homeManager = new LinearLayoutManager(context);
        homeManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecycler.setLayoutManager(homeManager);

        //初始化透明度
        topParent.getBackground().mutate().setAlpha(topParentAlpha);

        tr.setAutoLoadMore(false);
        tr.setEnableLoadmore(false);
        tr.setEnableOverScroll(true);
        tr.setMaxHeadHeight(120);
        tr.setHeaderHeight(80);
    }

    public void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.location:
                        Intent intent1 = new Intent(context, LocationActivity.class);
                        startActivity(intent1);
                        getActivity().overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                        break;
                    case R.id.search:
                        Intent intent5 = new Intent(getActivity(), SearchActivity.class);
                        startActivity(intent5);
                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        break;
                    case R.id.scan:
                        Intent intent2 = new Intent(context, ScanActivity.class);
                        startActivity(intent2);
                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        break;
                    default:
                        break;
                }
            }
        };
        location.setOnClickListener(onClickListener);
        search.setOnClickListener(onClickListener);
        scan.setOnClickListener(onClickListener);

        homeRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    int top = layoutManager.findViewByPosition(firstVisibleItemPosition).getTop();
                    setAlpha(-top);
                } else if (firstVisibleItemPosition >= 1) {
                    setAlpha(200);
                }

                if (firstVisibleItemPosition >= 2 && myVideoVH != null && myVideoVH.ijkVideoView.isPlaying()) {
                    myVideoVH.ijkVideoView.pause();
                    playState = -1;
                    myVideoVH.setInfoUI();
                    myVideoVH.setPreView(myVideoVH.ijkVideoView.getCurrentPosition(), videoUrl);
                }

            }
        });

        tr.setOnRefreshListener(new RefreshListenerAdapter() {

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                initData();
            }
        });
    }

    public void initData() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_HOME_INFO),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("address", RuntimeConfig.USER_ADDRESS)
                                .getParams());
                final HomeResult result = GsonUtil.fromJson(rs, HomeResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            dataDownSuccess = true;
                            bannerData = result.getData().getBanner();
                            prepareBannerCycle(bannerData);
                            videoUrl = result.getData().getVideo().getVideoUrl();
                            if (videoUrl == null) {
                                videoUrl = "";
                            }
                            floorData = result.getData().getFloor();
                            OrderUtil.orderHomeFloor(floorData);

                            homeRecycler.setAdapter(new HomeAdapter());
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });

            }
        });
    }

    /**
     * -----------------------顶栏渐变----------------
     */
    public void setAlpha(int dy) {
        int h = 200;//最大渐变距离
        int minDis = 25;//超过最低距离才开始渐变

        if (dy < minDis) {
            topParent.getBackground().mutate().setAlpha(0);
            GlideUtil.GlideInstance(context, R.drawable.navbar_icon_location).into(location);
            GlideUtil.GlideInstance(context, R.drawable.navbar_icon_scan).into(scan);
            return;
        }

        if (dy <= h) {
            topParentAlpha = dy * 255 / h;
            topParent.getBackground().mutate().setAlpha(topParentAlpha);
            if (topParentAlpha > 150) {
                GlideUtil.GlideInstance(context, R.drawable.navbar_icon_location_up).into(location);
                GlideUtil.GlideInstance(context, R.drawable.navbar_icon_scan_up).into(scan);
                search.setCardBackgroundColor(getResources().getColor(R.color.gray_top));
            } else {
                GlideUtil.GlideInstance(context, R.drawable.navbar_icon_location).into(location);
                GlideUtil.GlideInstance(context, R.drawable.navbar_icon_scan).into(scan);
                search.setCardBackgroundColor(Color.WHITE);
            }
            search.setRadius(getResources().getDimension(R.dimen.fragment_home_card));
        } else {
            topParent.getBackground().mutate().setAlpha(255);
        }
    }


    /*
    ====================================================
    ====================================================
    ================== HomeRecycler ====================
    ====================================================
    ====================================================
     */

    private class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new BannerVH(LayoutInflater.from(context).inflate(R.layout.item_home_recyc_banner, parent, false));
            } else if (viewType == 1) {
                return new CateVH(LayoutInflater.from(context).inflate(R.layout.item_home_recyc_cate, parent, false));
            } else if (videoUrl.length() > 0 && viewType == 2) {
                return new VideoVH(LayoutInflater.from(context).inflate(R.layout.item_home_recyc_video, parent, false));
            } else {
                return new FloorVH(LayoutInflater.from(context).inflate(R.layout.item_home_recyc_floor, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof BannerVH) {
                ((BannerVH) holder).setData(bannerData);
            } else if (holder instanceof VideoVH) {
                ((VideoVH) holder).setData();
            } else if (holder instanceof FloorVH) {
                ((FloorVH) holder).setData(position - (2 + (videoUrl.length() > 0 ? 1 : 0)));
            }
        }

        @Override
        public int getItemCount() {
            return 2 + (videoUrl.length() > 0 ? 1 : 0) + (floorData == null ? 0 : floorData.size());
        }

        @Override
        public int getItemViewType(int position) {
            return position < (2 + (videoUrl.length() > 0 ? 1 : 0)) ? position : 4;
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

    private class CateVH extends RecyclerView.ViewHolder {
        private LinearLayout cate_recommend;
        private LinearLayout cate_kitchen;
        private LinearLayout cate_life;
        private LinearLayout cate_textile;
        private LinearLayout cate_bathroom;

        public CateVH(View itemView) {
            super(itemView);
            cate_recommend = itemView.findViewById(R.id.category_recommend);
            cate_kitchen = itemView.findViewById(R.id.category_kitchen);
            cate_life = itemView.findViewById(R.id.category_life);
            cate_textile = itemView.findViewById(R.id.category_textile);
            cate_bathroom = itemView.findViewById(R.id.category_bathroom);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CategoryActivity.class);
                    switch (view.getId()) {
                        case R.id.category_recommend:
                            intent = new Intent(context, RecommendActivity.class);
                            break;
                        case R.id.category_kitchen:
                            intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, Constant.MainCateType.CATEGORY_STR_KITCHEN);
                            break;
                        case R.id.category_life:
                            intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, Constant.MainCateType.CATEGORY_STR_LIFE);
                            break;
                        case R.id.category_textile:
                            intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, Constant.MainCateType.CATEGORY_STR_TEXTILE);
                            break;
                        case R.id.category_bathroom:
                            intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, Constant.MainCateType.CATEGORY_STR_BATHROOM);
                            break;
                        default:
                            break;
                    }
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            };
            cate_recommend.setOnClickListener(listener);
            cate_kitchen.setOnClickListener(listener);
            cate_life.setOnClickListener(listener);
            cate_textile.setOnClickListener(listener);
            cate_bathroom.setOnClickListener(listener);
        }
    }

    /**
     * 视频 Holder
     */
    private class VideoVH extends RecyclerView.ViewHolder {
        private CardView videoParent;
        private ImageView preView;
        private RelativeLayout videoCenter;
        private IjkVideoView ijkVideoView;
        private ImageView play;
        private LinearLayout seekParent;
        private TextView progress;
        private SeekBar seekBar;
        private TextView totalProgress;
        private SeekBarThread seekBarThread;

        VideoVH(View itemView) {
            super(itemView);
            videoParent = itemView.findViewById(R.id.video_parent);
            ijkVideoView = itemView.findViewById(R.id.video);
            preView = itemView.findViewById(R.id.pre_view);
            videoCenter = itemView.findViewById(R.id.center_video);
            play = itemView.findViewById(R.id.play);
            progress = itemView.findViewById(R.id.progress);
            seekParent = itemView.findViewById(R.id.seek_parent);
            totalProgress = itemView.findViewById(R.id.total_progress);
            seekBar = itemView.findViewById(R.id.video_bar);

            seekBarThread = new SeekBarThread(seekBar, ijkVideoView, progress, totalProgress);
            initPlayerEvent();

            ViewGroup.LayoutParams layoutParams = videoParent.getLayoutParams();
            layoutParams.width = RuntimeConfig.SCREEN_WIDTH - DisplayUtil.dip2px(getContext(), 16 * 2);
            layoutParams.height = layoutParams.width * 9 / 16;
            videoParent.setLayoutParams(layoutParams);
        }

        private void setData() {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            setVideoResource(ijkVideoView, videoUrl);
            setPreView(ijkVideoView.getCurrentPosition() == 0 ? 1 : ijkVideoView.getCurrentPosition(), videoUrl);
            if (!seekBarThread.isAlive()) seekBarThread.start();//最好是vh出现就开启，消失就关闭。
        }

        private void initPlayerEvent() {
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ijkVideoView.isPlaying()) {
                        ijkVideoView.pause();
                        playState = -1;
                        setInfoUI();
                    } else {
                        ijkVideoView.start();
                        playState = -2;
                        setClearUI();
                    }
                }
            });
            videoCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playState >= 0) return;
                    if (seekParent.getVisibility() == View.GONE) {
                        setInfoUI();
                    } else {
                        setClearUI();
                    }
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    ijkVideoView.seekTo(seekBar.getProgress());
                }
            });
            ijkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                    if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        hidePreView();
                    }
                    if (iMediaPlayer.getVideoHeight() != 0) {
                        int videoHeight = iMediaPlayer.getVideoHeight();
                        int videoWidth = iMediaPlayer.getVideoWidth();
                        ViewGroup.LayoutParams layoutParams = videoParent.getLayoutParams();
                        layoutParams.height = videoParent.getMeasuredWidth() * videoHeight / videoWidth;
                        videoParent.setLayoutParams(layoutParams);
                        LogUtil.e("Ijk", "h=" + videoHeight + "w=" + videoWidth + " vw=" + layoutParams.width + " vh=" + layoutParams.height);
                    }
                    return true;
                }
            });
            ijkVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    //用户点击再播放
                    seekBar.setMax(ijkVideoView.getDuration());
                    seekBar.setProgress(0);
                    totalProgress.setText(TimeUtil.mesToRx(ijkVideoView.getDuration()));
                }
            });
            ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    playState = 1;
                    play.setVisibility(View.VISIBLE);
                    GlideUtil.GlideInstance(context, R.drawable.home_icon_replay).into(play);
                    seekParent.setVisibility(View.GONE);
                }
            });
            ijkVideoView.setOnSurfaceUpdatedListener(new IjkVideoView.OnSurfaceUpdated() {
                @Override
                public void dosomethingOnSurfaceUpdated() {
                    hidePreView();
                }

                @Override
                public void dosomethingOnSurfaceCreated() {
                    if (myVideoVH != null) {
                        setPreView(myVideoVH.ijkVideoView.getCurrentPosition(), videoUrl);
                    }

                }

                @Override
                public void dosomethingOnSurfaceDestoryed() {

                }
            });

            pauseWhenInvisible();
        }

        private void setClearUI() {
            play.setVisibility(View.GONE);
            seekParent.setVisibility(View.GONE);
        }

        private void setInfoUI() {
            clearVideoImgCache();
            mImageCache.put(ijkVideoView.getCurrentPosition(), ijkVideoView.getBitmapNow());
            seekParent.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            if (playState == -2) {
                play.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_suspend));
            } else {
                play.setImageDrawable(getResources().getDrawable(R.drawable.home_icon_play));
            }
        }

        private void setPreView(int mecs, String url) {
            new PreViewAsyncTask(mecs, url, ijkVideoView, preView).execute();
        }

        private void hidePreView() {
            for (int a = 0; ; a++) {
                if (ijkVideoView.isPlaying()) {
                    preView.setAlpha(0.0f);
                    return;
                } else if (a > 10000) {
                    return;
                }
            }
        }

        private void pauseWhenInvisible() {
            myVideoVH = this;
            videoParent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    if (ijkVideoView.isPlaying()) {//切换页签时焦点变化，换页面时焦点不变
                        ijkVideoView.pause();
                        playState = -1;
                        setInfoUI();
                    }
                }
            });
        }
    }

    private class FloorVH extends RecyclerView.ViewHolder {
        private LinearLayout title_content;
        private ResizableImageView floor_img;
        private RecyclerView floor;

        public FloorVH(View itemView) {
            super(itemView);
            title_content = itemView.findViewById(R.id.title_content);
            floor_img = itemView.findViewById(R.id.floor_img);
            floor = itemView.findViewById(R.id.floor);

        }

        private void setData(final int pos) {
            if (pos == 0) {
                title_content.setVisibility(View.VISIBLE);
            } else {
                title_content.setVisibility(View.GONE);
            }

            floor_img.setOnClickListener(WebBannerListener.getListener(getActivity(), floorData.get(pos).getTopic().getType(), floorData.get(pos).getTopic().getParams()));

            GlideUtil.GlideWithPlaceHolder(context, floorData.get(pos).getTopic().getImgUrl()).into(floor_img);
            initFloorRecycler(floor);
            FloorAdapter floorAdapter = new FloorAdapter(pos, floorData.get(pos).getGoods());
            floor.setAdapter(floorAdapter);
        }
    }


    /**
     * -----------------------顶部banner----------------
     */
    private void prepareBannerCycle(List<WebBanner> bannerData) {
        //leaffun: autoScrollViewPager开启无限循环模式，需要前后增加两个数据。
        if (bannerData == null || bannerData.size() == 0) {
            return;
        }
        bannerData.add(bannerData.get(0));
        bannerData.add(0, bannerData.get(bannerData.size() - 2));
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

    /**
     * ------------------------视频控制-------------------
     */

    private class PreViewAsyncTask extends android.os.AsyncTask<String, Void, Bitmap> {
        int mecs;
        String url;
        IjkVideoView videoView;
        ImageView preView;

        /**
         * @param mecs 帧位置
         * @param url  视频路径
         */
        public PreViewAsyncTask(int mecs, String url, IjkVideoView videoView, ImageView view) {
            this.mecs = mecs;
            this.url = url;
            this.videoView = videoView;
            this.preView = view;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap b = mImageCache.get(mecs);
            if (b != null) {
                return b;
            } else {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                try {
                    String videoPath = AndroidFileUtil.getVideoPath(url, context);
                    if (videoPath.isEmpty()) {
                        b = null;
                    } else {
                        mmr.setDataSource(videoPath);
                    }
                    b = mmr.getFrameAtTime((mecs) * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
                    clearVideoImgCache();

                    if (b != null) {
                        mImageCache.put(mecs, b);
                    } else {
                        b = mImageCache.get(0);
                    }
                } catch (Exception e) {
                    LogUtil.e("HomeFragment", "获取图片封面异常，异常信息如下：");
                    e.printStackTrace();
                } finally {
                    mmr.release();
                }
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!videoView.isPlaying()) {
                if (bitmap == null) {
                    bitmap = videoView.getBitmapNow();
                }
                if (bitmap == null) {
                    LogUtil.e("settingPreView", "没有取到有效的bitmap");
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.load_err_empty);
                }
                preView.setImageBitmap(bitmap);
                preView.setAlpha(1.0f);
                LogUtil.i("视频封面", mImageCache.size() + "张");
            }
        }
    }

    private void clearVideoImgCache() {
        Set<Map.Entry<Integer, Bitmap>> entries = mImageCache.entrySet();
        Bitmap bs = null;
        if (entries.size() > 3) {
            for (Map.Entry<Integer, Bitmap> entry : entries) {
                if (entry.getKey() != 0) {
                    bs = mImageCache.get(entry.getKey());
                }
            }
            mImageCache.clear();
            mImageCache.put(0, bs);
            LogUtil.i("视频封面", "缓存清理完成");
        }
    }

    private class SeekBarThread extends Thread {
        private SeekBar bar;
        private IjkVideoView video;
        private TextView pro;
        private TextView totalPro;

        public SeekBarThread(SeekBar seekBar, IjkVideoView ijkVideoView, TextView progress, TextView totalPregress) {
            bar = seekBar;
            video = ijkVideoView;
            pro = progress;
            totalPro = totalPregress;
        }

        @Override
        public void run() {
            for (; ; ) {
                //执行耗时操作
                try {
                    Thread.sleep(1000);
                    if (!video.isPlaying()) continue;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar.setProgress(video.getCurrentPosition());
                            pro.setText(TimeUtil.mesToRx(video.getCurrentPosition()));
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setVideoResource(IjkVideoView videoView, String url) {
        String videoPath = AndroidFileUtil.getVideoPath(url, context);
        if (videoPath.isEmpty()) {
            HttpProxyCacheServer proxy = MallApplication.getProxy(context);

            String proxyUrl = proxy.getProxyUrl(url);
            LogUtil.i("HomeFragment proxyUrl : ", proxyUrl);
            videoView.setVideoPath(proxyUrl);
        } else {
            videoView.setVideoPath(videoPath);
        }
        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
    }


    /**
     * -----------------------楼层商品 Adapter----------------
     */
    private class FloorAdapter extends RecyclerView.Adapter<FloorViewHolder> {
        private int type;
        private List<GoodShow> goods;

        public FloorAdapter(int type, List<GoodShow> goods) {
            this.type = type;
            this.goods = goods;
        }

        @Override
        public FloorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FloorViewHolder(LinearLayout.inflate(context, R.layout.item_home_recyc_floor_item, null));
        }

        @Override
        public void onBindViewHolder(FloorViewHolder holder, int position) {
            final GoodShow goodShow = goods.get(position);
            GlideUtil.GlideWithPlaceHolder(context, goodShow.getImgUrl().get(0)).into(holder.good_img);
            holder.good_title.setText(goodShow.getName());
            holder.good_intro.setText(goodShow.getDescription());
            holder.good_price.setText(String.valueOf(goodShow.getPrice()));
            if (PriceFixUtil.checkNeedScribing(goodShow.getPrice(), goodShow.getOriginalPrice())) {
                holder.good_old_price.setVisibility(View.VISIBLE);
                holder.good_old_price.setText(PriceFixUtil.format(goodShow.getOriginalPrice()));
            } else {
                holder.good_old_price.setVisibility(View.INVISIBLE);
            }
            if (goodShow.getTag() != null && goodShow.getTag().size() > 0) {
                holder.good_label.setVisibility(View.VISIBLE);
                holder.good_label.setText(goodShow.getTag().get(0));
            } else {
                holder.good_label.setVisibility(View.INVISIBLE);
            }

            if (type != 0 && goodShow.getIsNew() == 1) {
                holder.new_good.setVisibility(View.VISIBLE);
            } else {
                holder.new_good.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(goodShow.getId()));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
            holder.good_add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!RuntimeConfig.userValid()) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, getActivity());
                    APIUtil.addCart(Constant.GoodType.SINGLE, goodShow.getId(), goodShow.getProductId(), 1, new APIUtil.CallBack<BaseResult>() {
                        @Override
                        public void handleResult(BaseResult result) {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("添加购物车成功");
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                            MallApplication.showProgressDialog(false, getActivity());
                        }
                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return goods == null ? 0 : goods.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }


    }

    /**
     * 楼层商品 ViewHolder
     */
    class FloorViewHolder extends RecyclerView.ViewHolder {
        private TextView new_good;
        private ImageView good_img;
        private TextView good_title;
        private TextView good_intro;
        private TextView good_price;
        private TextView good_old_price;
        private TextView good_label;
        private RelativeLayout good_add_cart;

        FloorViewHolder(View itemView) {
            super(itemView);
            new_good = itemView.findViewById(R.id.new_good);
            good_img = itemView.findViewById(R.id.good_img);
            good_title = itemView.findViewById(R.id.good_title);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_old_price = itemView.findViewById(R.id.good_old_price);
            good_label = itemView.findViewById(R.id.good_label);
            good_add_cart = itemView.findViewById(R.id.good_add_cart);
        }
    }

    private void initFloorRecycler(RecyclerView recyclerView) {
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
    }
}
