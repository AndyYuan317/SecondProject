package com.wingplus.coomohome.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.component.SquareImage;
import com.wingplus.coomohome.component.StrikeTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.SearchGoodsResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果页
 *
 * @author leaffun.
 *         Create on 2017/9/20.
 */
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 页码从 1 开始
     */
    private int page = 1;
    private int totalCount = 0;
    private List<GoodShow> searchData = new ArrayList<>();


    /**
     * 【分类和排序】选项栏是否打开
     */
    private static boolean isShowing = false;

    private static String[] tabs = {"all_closed", "cate", "sort", "filter"};


    /**
     * 存储相应的二级分类
     */
    private List<Category> secondCate = new ArrayList<>();

    /**
     * 所有的排序选项(为了方便切换，排序也用分类entity存储)
     */
    private List<Category> chosenSort = new ArrayList<>();

    {
        Category category = new Category();
        category.setId("0");
        category.setName("全部分类");
        secondCate.add(category);
        secondCate.addAll(RuntimeConfig.mainCate);
    }

    {
        Category category0 = new Category();
        category0.setId("0");
        category0.setName("综合排序");

        Category category1 = new Category();
        category1.setId("1");
        category1.setName("评价排序");

        Category category2 = new Category();
        category2.setId("2");
        category2.setName("点击量排序");

        Category category3 = new Category();
        category3.setId("3");
        category3.setName("价格低到高");

        Category category4 = new Category();
        category4.setId("4");
        category4.setName("价格高到低");

        chosenSort.add(category0);
        chosenSort.add(category1);
        chosenSort.add(category2);
        chosenSort.add(category3);
        chosenSort.add(category4);
    }

    /**
     * 选中的分类
     */
    private Category mSecondCate = new Category();

    {
        mSecondCate.setId("0");
        mSecondCate.setName("全部分类");
    }

    /**
     * 选中的排序
     */
    private Category mSort = chosenSort.get(0);

    /**
     * 展开的可选项（二级分类，或排序选项）
     */
    private List<Category> mTabData = new ArrayList<>();


    private ImageView empty;
    private TextView search_edit;
    private RecyclerView recycler;
    private View popup;
    private EditText lowPrice;
    private EditText highPrice;
    private EditText place;
    private PopupWindow searchPop;
    private RelativeLayout chose_back;
    private RecyclerView choseRecycler;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private ChoseAdapter choseAdapter;
    private TextView cate_chosen;
    private TextView sort_chosen;
    private SRGAdapter adapter;
    private TwinklingRefreshLayout tr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        initView();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        empty = findViewById(R.id.empty);
        search_edit = findViewById(R.id.search_edit);
        tr = findViewById(R.id.tr);

        recycler = findViewById(R.id.search_result_goods);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new SpaceItemDecoration(1));

        chose_back = findViewById(R.id.chose_back);
        choseRecycler = findViewById(R.id.chose_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        choseRecycler.setLayoutManager(layoutManager);
        choseAdapter = new ChoseAdapter();
        mTabData = secondCate;
        choseRecycler.setAdapter(choseAdapter);

        cate_chosen = findViewById(R.id.cate_chosen);
        sort_chosen = findViewById(R.id.sort_chosen);

        adapter = new SRGAdapter();
        recycler.setAdapter(adapter);
        initAd();
    }


    private void initEvent() {
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                closeChoseWhenInit();
            }
        };
        chose_back.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                initData();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                getNextPageData();
            }
        });
    }

    private void initData() {
        search_edit.setText(RuntimeConfig.searchWord);

        page = Constant.Page.START_PAGE;

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SEARCH_GOODS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("keyWords", RuntimeConfig.searchWord)
                                .addParam("catName", mSecondCate.getName())
                                .addParam("place", place.getText().toString().trim())
                                .addParam("lowPrice", lowPrice.getText().toString().trim())
                                .addParam("highPrice", highPrice.getText().toString().trim())
                                .addParam("order", mSort.getId())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final SearchGoodsResult searchGoodsResult = GsonUtil.fromJson(rs, SearchGoodsResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (searchGoodsResult != null && searchGoodsResult.getResult() == APIConfig.CODE_SUCCESS) {
                            if (searchGoodsResult.getData() != null && searchGoodsResult.getData().getResult() != null && searchGoodsResult.getData().getResult().size() > 0) {
                                searchData = searchGoodsResult.getData().getResult();
                                page = searchGoodsResult.getData().getCurrentPageNo();
                                totalCount = searchGoodsResult.getData().getTotalCount();
                                OrderUtil.order(searchData);

                                empty.setVisibility(View.INVISIBLE);
                                recycler.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                                recycler.smoothScrollToPosition(0);
                            } else {
                                empty.setVisibility(View.VISIBLE);
                                recycler.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            ToastUtil.toastByCode(searchGoodsResult);
                            empty.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void getNextPageData() {
        if (page * Integer.parseInt(Constant.Page.COMMON_PAGE_SIZE) >= totalCount) {
            tr.finishLoadmore();
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SEARCH_GOODS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("keyWords", RuntimeConfig.searchWord)
                                .addParam("catName", mSecondCate.getName())
                                .addParam("place", place.getText().toString().trim())
                                .addParam("lowPrice", lowPrice.getText().toString().trim())
                                .addParam("highPrice", highPrice.getText().toString().trim())
                                .addParam("order", mSort.getId())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final SearchGoodsResult searchGoodsResult = GsonUtil.fromJson(rs, SearchGoodsResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (searchGoodsResult != null && searchGoodsResult.getResult() == APIConfig.CODE_SUCCESS) {
                            if (searchGoodsResult.getData() != null && searchGoodsResult.getData().getResult() != null && searchGoodsResult.getData().getResult().size() > 0) {
                                searchData.addAll(searchGoodsResult.getData().getResult());
                                page = searchGoodsResult.getData().getCurrentPageNo();
                                totalCount = searchGoodsResult.getData().getTotalCount();
                                OrderUtil.order(searchData);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.toastByCode(searchGoodsResult);
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.search:
                Intent searchInt = new Intent(SearchResultActivity.this, SearchActivity.class);
                searchInt.putExtra("from", "SearchResultActivity");
                startActivity(searchInt);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.cart:
                Intent intent = new Intent(SearchResultActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                break;
            case R.id.category_second:
                if (openTab("cate")) {
                    mTabData = secondCate;
                    choseAdapter.notifyDataSetChanged();
                    //打开
                    isShowing = false;
                    toggleChose();
                } else {
                    //关闭
                    openTab("all_closed");
                    isShowing = true;
                    toggleChose();
                }
                break;
            case R.id.sort:
                if (openTab("sort")) {
                    mTabData = chosenSort;
                    choseAdapter.notifyDataSetChanged();
                    //打开
                    isShowing = false;
                    toggleChose();
                } else {
                    //关闭
                    openTab("all_closed");
                    isShowing = true;
                    toggleChose();
                }
                break;
            case R.id.chose_back:
                openTab("all_closed");
                isShowing = true;
                toggleChose();
                break;
            case R.id.filter:
                if (openTab("filter")) {
                    if (isShowing) toggleChose();
                }
                showPopup(getWindow().getDecorView());
                break;
            case R.id.filter_back:
                searchPop.dismiss();
                break;
            case R.id.reset:
                lowPrice.setText("");
                highPrice.setText("");
                place.setText("");
                break;
            case R.id.query:
                initData();
                searchPop.dismiss();
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


    /**
     * ----------------搜索结果适配器------------------------
     */
    class SRGAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GoodViewHolder(LayoutInflater.from(SearchResultActivity.this).inflate(R.layout.item_category_grid_good, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GoodViewHolder goodVH = (GoodViewHolder) holder;
            goodVH.setData(searchData.get(position));
        }

        @Override
        public int getItemCount() {
            return searchData == null ? 0 : searchData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout good;
        private final SquareImage good_img;
        private final TextView good_title;
        private final TextView good_intro;
        private final SpannableTextView good_price;
        private final StrikeTextView good_old_price;
        private final TextView good_label;
        private TextView good_place;
        private final ImageView add_cart;

        public GoodViewHolder(View itemView) {
            super(itemView);
            good = itemView.findViewById(R.id.good_1);
//            endLine = itemView.findViewById(R.id.end_line);
            good_img = itemView.findViewById(R.id.good_img);
            good_title = itemView.findViewById(R.id.good_title);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_old_price = itemView.findViewById(R.id.good_old_price);
            good_label = itemView.findViewById(R.id.good_label);
            good_place = itemView.findViewById(R.id.place);
            add_cart = itemView.findViewById(R.id.good_add_cart);

        }

        public void setData(final GoodShow goodShow) {
            good_title.setText(goodShow.getName());
            GlideUtil.GlideWithPlaceHolder(getApplicationContext(), goodShow.getImgUrl().get(0)).into(good_img);
            good_intro.setText(goodShow.getDescription());
            good_price.setText(String.valueOf(goodShow.getPrice()));
            if (PriceFixUtil.checkNeedScribing(goodShow.getPrice(), goodShow.getOriginalPrice())) {
                good_old_price.setVisibility(View.VISIBLE);
                good_old_price.setText(PriceFixUtil.format(goodShow.getOriginalPrice()));
            } else {
                good_old_price.setVisibility(View.INVISIBLE);
            }
            if (goodShow.getTag() != null && goodShow.getTag().size() > 0) {
                good_label.setVisibility(View.VISIBLE);
                good_label.setText(goodShow.getTag().get(0));
            } else {
                good_label.setVisibility(View.INVISIBLE);
            }
            if (goodShow.getPlace() != null && goodShow.getPlace().length() > 0) {
                good_place.setVisibility(View.VISIBLE);
                good_place.setText(goodShow.getPlace());
            }else{
                good_place.setVisibility(View.GONE);
            }


            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchResultActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(goodShow.getId()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!RuntimeConfig.userValid()) {
                        Intent intent = new Intent(SearchResultActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, SearchResultActivity.this);
                    APIUtil.addCart(Constant.GoodType.SINGLE, goodShow.getId(), goodShow.getProductId(), 1, new APIUtil.CallBack<BaseResult>() {
                        @Override
                        public void handleResult(BaseResult result) {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("添加购物车成功");
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                            MallApplication.showProgressDialog(false, SearchResultActivity.this);
                        }
                    });
                }
            });
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private final Paint paint;

        public SpaceItemDecoration(int space) {
            this.space = space;
            paint = new Paint();
            paint.setColor(getResources().getColor(R.color.titleGray));
            paint.setAntiAlias(true);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            if (parent.getChildLayoutPosition(view) % 2 == 0) {
                outRect.left = 0;
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


//            int childCount = parent.getChildCount();
//            int left;
//            int right;
//
//
//
//
//            for (int i = 0; i < childCount - 1; i++) {
//                //底线
//                View view = parent.getChildAt(i);
//                left = view.getLeft();
//                right = view.getWidth();
//                float top = view.getBottom();
//                float bottom = view.getBottom() + space;
//                c.drawRect(left, top, right, bottom, paint);
//                LogUtil.e("底线",left+"-"+top+"-"+right+"-"+bottom);
//
//                //左线
//                if (parent.getChildLayoutPosition(view) % 2 != 0) {
//                    top = view.getTop();
//                    bottom = view.getBottom();
//                    left = view.getLeft() - space;
//                    right = view.getLeft();
//                    c.drawRect(left, top, right, bottom, paint);
//                    LogUtil.e("左线", left + "-" + top + "-" + right + "-" + bottom);
//                }
//            }
        }
    }


    /**
     * -----------------选项适配器----------------------
     */
    private class ChoseAdapter extends RecyclerView.Adapter<ChoseViewHolder> {

        @Override
        public ChoseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChoseViewHolder(LayoutInflater.from(SearchResultActivity.this).inflate(R.layout.item_search_result_chose, parent, false));
        }

        @Override
        public void onBindViewHolder(ChoseViewHolder holder, int position) {
            final int pos = position;
            holder.item.setText(mTabData.get(position).getName());
            if (mSecondCate.getName().equals(mTabData.get(position).getName()) || mSort.getName().equals(mTabData.get(position).getName())) {
                holder.item.setTextColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                holder.item.setTextColor(UIUtils.getColor(R.color.titleBlack));
            }
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getTab().equals("cate")) {
                        mSecondCate = secondCate.get(pos);
                        cate_chosen.setText(mSecondCate.getName());
                        cate_chosen.setTextColor(UIUtils.getColor(R.color.colorPrimary));
                    } else {
                        mSort = chosenSort.get(pos);
                        sort_chosen.setText(mSort.getName());
                        sort_chosen.setTextColor(UIUtils.getColor(R.color.colorPrimary));
                    }
                    initData();
                    choseAdapter.notifyDataSetChanged();
                    openTab("all_closed");
                    toggleChose();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTabData == null ? 0 : mTabData.size();
        }
    }

    class ChoseViewHolder extends RecyclerView.ViewHolder {
        private TextView item;

        public ChoseViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
        }
    }


    /**
     * ----------------选项开关-----------------
     */
    private synchronized void toggleChose() {
        ObjectAnimator anim;
        float h = choseRecycler.getMeasuredHeight();
        if (isShowing) {
            anim = ObjectAnimator.ofFloat(choseRecycler, "translationY", 0, -h);
        } else {
            anim = ObjectAnimator.ofFloat(choseRecycler, "translationY", -h, 0);
        }
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (isShowing) {
                    chose_back.getBackground().setAlpha(0);
                } else {
                    chose_back.getBackground().setAlpha(153);
                    chose_back.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isShowing) {
                    chose_back.setVisibility(View.GONE);
                }
                isShowing = !isShowing;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.setDuration(200);
        anim.start();


//
//
//        ValueAnimator animator;
//        if (isShowing) {
//            animator = ValueAnimator.ofInt(300, 0);
//        } else {
//            animator = ValueAnimator.ofInt(0, 300);
//        }
//
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                int h = (int) valueAnimator.getAnimatedValue();
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h);
//                choseRecycler.setLayoutParams(params);
//            }
//        });
//
//        animator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                if (!isShowing) chose_back.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                if (isShowing) chose_back.setVisibility(View.GONE);
//                isShowing = !isShowing;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        animator.setDuration(500);
//        animator.start();
    }

    private void closeChoseWhenInit() {
        ObjectAnimator anim;
        int h = choseRecycler.getMeasuredHeight();
        anim = ObjectAnimator.ofFloat(choseRecycler, "translationY", 0, -h);
        anim.setDuration(1);
        anim.start();
        chose_back.setVisibility(View.GONE);
        chose_back.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }


    /**
     * ------------------工具------------------
     */

    /**
     * 选择 tab
     *
     * @param tab
     * @return true表示tab未打开，接下来应该打开
     */
    private boolean openTab(String tab) {
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].equals(tab)) {
                if (i != 0) {
                    tab = tabs[0];
                    tabs[0] = tabs[i];
                    tabs[i] = tab;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前tab
     *
     * @return
     */
    private String getTab() {
        return tabs[0];
    }

    /**
     * 旋转动画
     *
     * @param imageView
     */
    private void rotate(ImageView imageView) {
        Animation animation = new RotateAnimation(0, 180);
        animation.setDuration(310);
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
        imageView.startAnimation(animation);//开始动画
    }


    //-------------------------------初始化 |下拉框|-----------------------------
    private void initAd() {
        popup = getLayoutInflater().inflate(R.layout.popup_search_result, null);
        searchPop = new PopupWindow(popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, true);//获取焦点，事件不会穿透到下层的activity
        searchPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_color_tran_grey));//必须设置背景才能点击外部消失
        searchPop.setOutsideTouchable(true);
        searchPop.setAnimationStyle(R.style.popupAnim);
//        searchPop.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//          });
        initDraw();
    }

    private void initDraw() {
        popup.findViewById(R.id.filter_back).setOnClickListener(SearchResultActivity.this);
        popup.findViewById(R.id.filter_content).setOnClickListener(SearchResultActivity.this);
        lowPrice = popup.findViewById(R.id.low_price);
        highPrice = popup.findViewById(R.id.high_price);
        place = popup.findViewById(R.id.place);
        popup.findViewById(R.id.reset).setOnClickListener(SearchResultActivity.this);
        popup.findViewById(R.id.query).setOnClickListener(SearchResultActivity.this);
        searchPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                openTab("all_closed");
                backgroundAlpha(1.0f);
            }
        });
    }

    private void showPopup(View v) {
        if (searchPop != null && searchPop.isShowing()) {
            searchPop.dismiss();
            return;
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);


        searchPop.showAtLocation(v, Gravity.END, 0, location[1] + v.getHeight());
        backgroundAlpha(0.5f);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
