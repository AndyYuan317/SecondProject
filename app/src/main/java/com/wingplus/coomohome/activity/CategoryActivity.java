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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.APIUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.PageContent;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.CateResult;
import com.wingplus.coomohome.web.result.SearchGoodsResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class CategoryActivity extends BaseActivity implements View.OnClickListener {


    /**
     * 所有一级分类
     */
    public static String[] cates = {
            Constant.MainCateType.CATEGORY_STR_KITCHEN
            , Constant.MainCateType.CATEGORY_STR_LIFE
            , Constant.MainCateType.CATEGORY_STR_TEXTILE
            , Constant.MainCateType.CATEGORY_STR_BATHROOM
    };

    {
        try {
            if (RuntimeConfig.mainCate == null || RuntimeConfig.mainCate.size() <= 0) {
                finish();
            }
            int size = RuntimeConfig.mainCate.size();
            if (RuntimeConfig.mainCate != null && size > 0) {
                cates = new String[size];
                for (int i = 0; i < size; i++) {
                    cates[i] = RuntimeConfig.mainCate.get(i).getName();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 选中的一级分类
     */
    private String rootCateName;

    /**
     * 【分类和排序】选项栏是否打开
     */
    private boolean isShowing = false;

    /**
     * 是否列表式
     */
    private boolean list = true;

    /**
     * 第一个表示当前打开的状态
     */
    private String[] tabs = {"all_closed", "cate", "sort", "filter"};

    /**
     * 所有的排序选项(为了方便切换，排序也用分类entity存储)
     */
    private List<Category> chosenSort = new ArrayList<>();
    private EditText lowPrice;
    private EditText highPrice;
    private EditText place;

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
     * 选中的排序
     */
    private Category mSort = chosenSort.get(0);

    /**
     * 存储相应的二级分类
     */
    private List<Category> secondCate = new ArrayList<>();

    /**
     * 选中的分类
     */
    private Category mSecondCate = new Category();

    {
        mSecondCate.setId("0");
        mSecondCate.setName("全部分类");
    }

    /**
     * 展开的可选项（二级分类，或排序选项）
     */
    private List<Category> mTabData = new ArrayList<>();

    /**
     * 查询到的商品结果
     */
    private List<GoodShow> goods = new ArrayList<>();

    /**
     * 当前页码
     */
    private int page = Constant.Page.START_PAGE;

    /**
     * 总页码
     */
    private int total = 0;

    private View popup;
    private PopupWindow searchPop;
    private RelativeLayout chose_back;
    private RecyclerView choseRecycler;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private ChoseAdapter choseAdapter;
    private TextView cate_chosen;
    private TextView sort_chosen;
    private RecyclerView recycler;
    private ImageView list_or_grid;
    private SRGAdapter adapter;
    private RecyclerView cate_content;
    private RelativeLayout cate_back;
    private CateAdapter cateAdapter;
    private TextView rootCate;
    private ViewTreeObserver.OnGlobalLayoutListener cateListener;
    private TwinklingRefreshLayout tr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initView();
        initEvent();
        getSecondCate();
    }

    private void initData() {
        page = Constant.Page.START_PAGE;
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("catName", rootCateName)
                                .addParam("subCatId", mSecondCate.getId())
                                .addParam("place", place.getText().toString().trim())
                                .addParam("lowPrice", lowPrice.getText().toString().trim())
                                .addParam("highPrice", highPrice.getText().toString().trim())
                                .addParam("order", mSort.getId())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final SearchGoodsResult result = GsonUtil.fromJson(rs, SearchGoodsResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            PageContent<GoodShow> data = result.getData();
                            page = data.getCurrentPageNo();
                            total = data.getTotalPageCount();
                            goods = data.getResult();
                            adapter.notifyDataSetChanged();
                            recycler.smoothScrollToPosition(0);
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
            }
        });
    }

    private void getNextPageData() {
        LogUtil.e("nextpage", "nextpage");
        if (page >= total) {
            tr.finishLoadmore();
            return;
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_LIST)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("catName", rootCateName)
                                .addParam("subCatId", mSecondCate.getId())
                                .addParam("place", "")
                                .addParam("lowPrice", "")
                                .addParam("highPrice", "")
                                .addParam("order", mSort.getId())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final SearchGoodsResult result = GsonUtil.fromJson(rs, SearchGoodsResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            PageContent<GoodShow> data = result.getData();
                            page = data.getCurrentPageNo();
                            total = data.getTotalPageCount();
                            goods.addAll(data.getResult());
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
            }
        });
    }


    private void getSecondCate() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_GOODS_CATEGORY),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("catName", rootCateName)
                                .getParams());
                CateResult secondCateResult = GsonUtil.fromJson(rs, CateResult.class);
                if (secondCateResult != null && secondCateResult.getResult() == APIConfig.CODE_SUCCESS) {
                    secondCate = secondCateResult.getData();
                    final Category category = new Category();
                    category.setId("0");
                    category.setName("全部分类");
                    secondCate.add(0, category);
                    mSecondCate = category;
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            cate_chosen.setText(category.getName());
                            initData();
                        }
                    });
                } else {
                    ToastUtil.toastByCode(secondCateResult);
                }
            }
        });
    }


    private void initView() {
        //一级分类名称设置
        rootCate = findViewById(R.id.tool_text);
        Intent intent = getIntent();//todo 一律接收一级分类名称
        rootCateName = intent.getStringExtra(Constant.Key.KEY_CATEGORY_TYPE);
        rootCate.setText(rootCateName);

        //列表样式
        list_or_grid = findViewById(R.id.list_or_grid);

        //商品列表
        recycler = findViewById(R.id.search_result_goods);
        setGoodsLayoutManager();
        adapter = new SRGAdapter();
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(new SpaceItemDecoration(1));

        tr = findViewById(R.id.tr);

        //筛选选项列表
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
        cate_chosen.setText(mSecondCate.getName());
        sort_chosen.setText(mSort.getName());
        //侧滑筛选条件栏
        initSearch();

        //一级分类选项列表
        cate_content = findViewById(R.id.cate_content);
        cate_back = findViewById(R.id.cate_back);
        LinearLayoutManager cateManager = new LinearLayoutManager(this);
        cateManager.setOrientation(LinearLayoutManager.VERTICAL);
        cate_content.setLayoutManager(cateManager);
        cateAdapter = new CateAdapter();
        cate_content.setAdapter(cateAdapter);
    }


    private void initEvent() {
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                closeChoseWhenInit();
            }
        };
        chose_back.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        cateListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                closeCateWhenInit();
            }
        };
        cate_back.getViewTreeObserver().addOnGlobalLayoutListener(cateListener);

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cate_back:
                toggleCate();
                break;
            case R.id.tool_text:
                toggleCate();
                break;
            case R.id.list_or_grid:
                int vp = 0;
                if (recycler.getLayoutManager() != null) {
                    vp = ((LinearLayoutManager) recycler.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
                    // 获取当前第一个可见Item的position
                }

                list = !list;//反转到想要变更的布局
                setGoodsLayoutManager();
                recycler.scrollToPosition(vp);
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
                //todo dismiss要监听，将tab移动到all_closed
                break;
            case R.id.filter_content:
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


    /**
     * ----------------商品适配器------------------------
     */
    private class SRGAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new GoodViewHolder(LayoutInflater.from(CategoryActivity.this).inflate(
                    viewType == 0 ? R.layout.item_category_list_good : R.layout.item_category_grid_good, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GoodViewHolder goodVH = (GoodViewHolder) holder;
            goodVH.setData(position);
        }

        @Override
        public int getItemCount() {
            return goods == null ? 0 : goods.size();
        }

        @Override
        public int getItemViewType(int position) {
            return list ? 0 : 1;
        }
    }

    private class GoodViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout good;
        private SquareImage good_img;
        private TextView good_intro;
        private TextView good_title;
        private SpannableTextView good_price;
        private StrikeTextView good_old_price;
        private TextView good_label;
        private TextView good_place;
        private ImageView add_cart;

        GoodViewHolder(View itemView) {
            super(itemView);
            good = itemView.findViewById(R.id.good_1);
            good_title = itemView.findViewById(R.id.good_title);
            good_img = itemView.findViewById(R.id.good_img);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_old_price = itemView.findViewById(R.id.good_old_price);
            good_label = itemView.findViewById(R.id.good_label);
            good_place = itemView.findViewById(R.id.place);
            add_cart = itemView.findViewById(R.id.good_add_cart);
        }

        public void setData(int position) {
            final GoodShow goodShow = goods.get(position);
            good_title.setText(goodShow.getName());
            GlideUtil.GlideWithPlaceHolder(CategoryActivity.this, goodShow.getImgUrl().get(0)).into(good_img);
            good_intro.setText(goodShow.getDescription());
            good_price.setText(PriceFixUtil.format(goodShow.getPrice()));
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
                    Intent intent = new Intent(CategoryActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(goodShow.getId()));
                    startActivity(intent);
                }
            });

            add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!RuntimeConfig.userValid()) {
                        Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        return;
                    }
                    MallApplication.showProgressDialog(true, CategoryActivity.this);
                    APIUtil.addCart(Constant.GoodType.SINGLE, goodShow.getId(), goodShow.getProductId(), 1, new APIUtil.CallBack<BaseResult>() {
                        @Override
                        public void handleResult(BaseResult result) {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("添加购物车成功");
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                            MallApplication.showProgressDialog(false, CategoryActivity.this);
                        }
                    });
                }
            });
        }
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private final Paint paint;

        SpaceItemDecoration(int space) {
            this.space = space;
            paint = new Paint();
            paint.setColor(UIUtils.getColor(R.color.titleGray));
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

            if (list) {
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
     * -----------------下拉选项适配器----------------------
     */
    private class ChoseAdapter extends RecyclerView.Adapter<ChoseViewHolder> {

        @Override
        public ChoseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChoseViewHolder(LayoutInflater.from(CategoryActivity.this).inflate(R.layout.item_search_result_chose, parent, false));
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
                    choseAdapter.notifyDataSetChanged();
                    initData();
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

        ChoseViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
        }
    }


    /**
     * ----------------下拉选项开关-----------------
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
    }

    //选项开关初始化
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
     * -----------------一级分类适配器----------------------
     */
    private class CateAdapter extends RecyclerView.Adapter<CateViewHolder> {

        @Override
        public CateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CateViewHolder(LayoutInflater.from(CategoryActivity.this).inflate(R.layout.item_search_result_chose, parent, false));
        }

        @Override
        public void onBindViewHolder(CateViewHolder holder, int position) {
            String cate = cates[position];
            holder.item.setText(cate);
            holder.item.setGravity(Gravity.CENTER);
            if (cate.equals(rootCateName)) {
                holder.item.setTextColor(UIUtils.getColor(R.color.colorPrimary));
            } else {
                holder.item.setTextColor(UIUtils.getColor(R.color.titleBlack));
            }
        }

        @Override
        public int getItemCount() {
            return cates.length;
        }
    }

    class CateViewHolder extends RecyclerView.ViewHolder {
        private TextView item;

        CateViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cate = item.getText().toString();
                    rootCateName = cate;
                    rootCate.setText(cate);
                    getSecondCate();
                    cateAdapter.notifyDataSetChanged();
                    toggleCate();
                }
            });
        }
    }


    /**
     * ---------一级分类开关-------------
     */
    private synchronized void toggleCate() {
        boolean wantOpen;
        ObjectAnimator anim;
        float h = cate_content.getMeasuredHeight();
        if (cate_back.getVisibility() == View.GONE) {
            wantOpen = true;
            if (!"all_closed".equals(getTab())) {
                if ("filter".equals(getTab())) {
                    searchPop.dismiss();
                } else {
                    openTab("all_closed");
                    toggleChose();
                }
            }
            anim = ObjectAnimator.ofFloat(cate_content, "translationY", -h, 0);
        } else {
            wantOpen = false;
            anim = ObjectAnimator.ofFloat(cate_content, "translationY", 0, -h);
        }
        final boolean finalOpen = wantOpen;
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (finalOpen) {
                    cate_back.getBackground().setAlpha(153);
                    cate_back.setVisibility(View.VISIBLE);
                } else {
                    cate_back.getBackground().setAlpha(0);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!finalOpen) {
                    cate_back.setVisibility(View.GONE);
                }
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
    }


    //一级分类初始化
    private void closeCateWhenInit() {
        ObjectAnimator anim;
        int h = cate_content.getMeasuredHeight();
        anim = ObjectAnimator.ofFloat(cate_content, "translationY", 0, -h);
        anim.setDuration(1);
        anim.start();
        cate_back.setVisibility(View.GONE);
        cate_back.getViewTreeObserver().removeOnGlobalLayoutListener(cateListener);
    }

    private void setGoodsLayoutManager() {
        if (list) {
            list_or_grid.setImageDrawable(getResources().getDrawable(R.drawable.icon_rowlist));
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler.setLayoutManager(manager);
        } else {

            list_or_grid.setImageDrawable(getResources().getDrawable(R.drawable.icon_columnlist));
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            recycler.setLayoutManager(manager);
        }
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


    private void initSearch() {
        popup = getLayoutInflater().inflate(R.layout.popup_search_result, null);
        searchPop = new PopupWindow(popup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, true);//获取焦点，事件不会穿透到下层的activity
        searchPop.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.bg_color_tran_grey));//必须设置背景才能点击外部消失
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

    //设置数据
    private void initDraw() {
        popup.findViewById(R.id.filter_back).setOnClickListener(CategoryActivity.this);
        popup.findViewById(R.id.filter_content).setOnClickListener(CategoryActivity.this);
        lowPrice = popup.findViewById(R.id.low_price);
        highPrice = popup.findViewById(R.id.high_price);
        place = popup.findViewById(R.id.place);
        popup.findViewById(R.id.reset).setOnClickListener(CategoryActivity.this);
        popup.findViewById(R.id.query).setOnClickListener(CategoryActivity.this);
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
