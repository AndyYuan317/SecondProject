package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.MyRefreshLayout;
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
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CollectListResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 我的收藏
 *
 * @author leaffun.
 *         Create on 2017/10/10.
 */
public class CollectActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv;
    private ImageView iv;
    private boolean editing = false;
    private TextView edit;
    private CollectAdapter adapter;
    private TextView deleteCount;
    private MyRefreshLayout tr;

    private int page = 1;
    private int totalPageCount = 0;
    private List<GoodShow> collections;

    /**
     * 去重的收藏。
     */
    private Set<String> ids = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.edit:
                if (editing) {
                    //编辑完成
                    editing = false;
                    edit.setText("编辑");
                    deleteCount.setVisibility(View.GONE);
                    deleteCount.setText("删除(0)");
                    ids.clear();
                    if (collections != null && collections.size() > 0) {
                        for (GoodShow goodShow : collections) {
                            goodShow.setChose(false);
                        }

                    }
                    tr.setEnableRefresh(true);
                } else {
                    //进入编辑
                    editing = true;
                    edit.setText("完成");
                    deleteCount.setVisibility(View.VISIBLE);
                    tr.setEnableRefresh(false);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.count:
                if (ids == null || ids.size() <= 0) {
                    return;
                }
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COLLECTION_DELETE)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("id", StringUtil.bindStr(ids, ","))
                                        .getParams());
                        final StringDataResult stringDataResult = GsonUtil.fromJson(rs, StringDataResult.class);
                        UIUtils.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (stringDataResult != null && stringDataResult.getResult() == APIConfig.CODE_SUCCESS) {
                                    ToastUtil.toastByCode(stringDataResult);
                                    editing = false;
                                    edit.setText("编辑");
                                    deleteCount.setVisibility(View.GONE);
                                    deleteCount.setText("删除(0)");
                                    ids.clear();
                                    if (collections != null && collections.size() > 0) {
                                        for (GoodShow goodShow : collections) {
                                            goodShow.setChose(false);
                                        }
                                    }
                                    tr.setEnableRefresh(true);
                                    tr.startRefresh();
                                } else {
                                    ToastUtil.toastByCode(stringDataResult);
                                }
                            }
                        });
                    }
                });
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
        edit = findViewById(R.id.edit);
        deleteCount = findViewById(R.id.count);
        tr = findViewById(R.id.tr);
        rv = findViewById(R.id.rv);
        iv = findViewById(R.id.iv);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //该元素占几个格子
                if (adapter.getItemViewType(position) == 0) {
                    return 2;
                }
                return 1;
            }
        });
        rv.setLayoutManager(manager);
        adapter = new CollectAdapter();
        rv.setAdapter(adapter);
        rv.addItemDecoration(new SpaceItemDecoration(1));

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

        deleteCount.setOnClickListener(this);
    }

    private void initData() {
        page = Constant.Page.START_PAGE;
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COLLECTION_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final CollectListResult collectListResult = GsonUtil.fromJson(rs, CollectListResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (collectListResult != null && collectListResult.getResult() == APIConfig.CODE_SUCCESS) {
                            page = collectListResult.getData().getCurrentPageNo();
                            totalPageCount = collectListResult.getData().getTotalPageCount();
                            collections = collectListResult.getData().getResult();
                            adapter.notifyDataSetChanged();
                            if (collections.size() == 0) {
                                iv.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.INVISIBLE);
                            } else {
                                iv.setVisibility(View.INVISIBLE);
                                rv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ToastUtil.toastByCode(collectListResult);
                        }
                    }
                });
            }
        });
    }

    private void getNextPageData() {
        if (page >= totalPageCount) {
            tr.finishLoadmore();
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COLLECTION_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final CollectListResult collectListResult = GsonUtil.fromJson(rs, CollectListResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (collectListResult != null && collectListResult.getResult() == APIConfig.CODE_SUCCESS) {
                            page = collectListResult.getData().getCurrentPageNo();
                            totalPageCount = collectListResult.getData().getTotalPageCount();
                            collections.addAll(collectListResult.getData().getResult());
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.toastByCode(collectListResult);
                        }
                    }
                });
            }
        });
    }

    /**
     * ----------------商品适配器------------------------
     */
    private class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                TextView textView = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(20));
                textView.setLayoutParams(params);
                textView.setBackgroundColor(UIUtils.getColor(R.color.gray_top));
                return new BlankVH(textView);
            }
            return new GoodViewHolder(LayoutInflater.from(CollectActivity.this).inflate(R.layout.item_category_grid_good, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {


            } else {
                GoodViewHolder goodVH = (GoodViewHolder) holder;
                goodVH.setData(position - 1);
            }
        }

        @Override
        public int getItemCount() {
            return collections != null && collections.size() > 0 ? (1 + collections.size()) : 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }
    }

    private class GoodViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout good;
        private SquareImage good_img;
        private TextView good_title;
        private TextView good_intro;
        private SpannableTextView good_price;
        private StrikeTextView good_old_price;
        private TextView good_label;
        private ImageView add_cart;
        private CheckBox good_check;

        private long sku;

        public GoodViewHolder(View itemView) {
            super(itemView);
            good = itemView.findViewById(R.id.good_1);
            good_img = itemView.findViewById(R.id.good_img);
            good_title = itemView.findViewById(R.id.good_title);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            good_old_price = itemView.findViewById(R.id.good_old_price);
            good_label = itemView.findViewById(R.id.good_label);
            add_cart = itemView.findViewById(R.id.good_add_cart);
            good_check = itemView.findViewById(R.id.good_check);

            add_cart.setVisibility(View.GONE);
            good_check.setClickable(false);
        }

        public void setData(final int pos) {
            GoodShow goodShow = collections.get(pos);
            GlideUtil.GlideWithPlaceHolder(CollectActivity.this, goodShow.getImgUrl().get(0)).into(good_img);
            good_title.setText(goodShow.getName());
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
                good_label.setText(String.valueOf(goodShow.getTag().get(0)));
            } else {
                good_label.setVisibility(View.INVISIBLE);
            }
            sku = goodShow.getId();
            good_check.setChecked(goodShow.isChose());
            good_check.setVisibility(editing ? View.VISIBLE : View.INVISIBLE);

            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editing) {
                        boolean checked = !good_check.isChecked();
                        good_check.setChecked(checked);
                        collections.get(pos).setChose(checked);
                        if (checked) {
                            ids.add(String.valueOf(sku));
                        } else {
                            ids.remove(String.valueOf(sku));
                        }
                        deleteCount.setText("删除(" + ids.size() + ")");
                    } else {
                        Intent intent = new Intent(CollectActivity.this, GoodsDetailActivity.class);
                        intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(sku));
                        startActivity(intent);
                        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                    }
                }
            });
        }
    }

    private class BlankVH extends RecyclerView.ViewHolder {
        public BlankVH(View itemView) {
            super(itemView);
        }
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

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
