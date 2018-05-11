package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.application.MallApplication;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.BadgeUtil;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.News;
import com.wingplus.coomohome.web.entity.AfterSaleOrder;
import com.wingplus.coomohome.web.entity.Msg;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.AfterSaleResult;
import com.wingplus.coomohome.web.result.MsgResult;

import java.util.List;

/**
 * 我的消息
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class NewsActivity extends BaseActivity implements View.OnClickListener {

    private MyRefreshLayout tr;
    private RecyclerView rv;
    private NewsAdapter adapter;
    private TextView empty;

    private List<Msg> msgs;

    private int page = Constant.Page.START_PAGE;
    private int totalPageCount = 0;
    private boolean goDetail = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
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
        empty = findViewById(R.id.empty);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new NewsAdapter();
        rv.setAdapter(adapter);
    }

    private void initData() {
        page = Constant.Page.START_PAGE;

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_NEWS_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final MsgResult msgResult = GsonUtil.fromJson(rs, MsgResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishRefreshing();
                        if (msgResult != null && msgResult.getResult() == APIConfig.CODE_SUCCESS) {
                            List<Msg> data = msgResult.getData().getResult();
                            if (data != null && data.size() > 0) {
                                empty.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
                                msgs = data;
                                page = msgResult.getData().getCurrentPageNo();
                                totalPageCount = msgResult.getData().getTotalPageCount();
                                adapter.notifyDataSetChanged();
                                goDetailByPush();
                            } else {
                                empty.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }
                        } else {
                            ToastUtil.toastByCode(msgResult);
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
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_NEWS_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("page", String.valueOf(page + 1))
                                .addParam("pageSize", Constant.Page.COMMON_PAGE_SIZE)
                                .getParams());
                final MsgResult msgResult = GsonUtil.fromJson(rs, MsgResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tr.finishLoadmore();
                        if (msgResult != null && msgResult.getResult() == APIConfig.CODE_SUCCESS) {
                            List<Msg> data = msgResult.getData().getResult();
                            if (data != null && data.size() > 0) {
                                msgs.addAll(data);
                                page = msgResult.getData().getCurrentPageNo();
                                totalPageCount = msgResult.getData().getTotalPageCount();
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.toastByCode(msgResult);
                        }
                    }
                });
            }
        });
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsVH> {

        @Override
        public NewsVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NewsVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_news, parent, false));
        }

        @Override
        public void onBindViewHolder(NewsVH holder, int position) {
            holder.setData(msgs.get(position));
        }

        @Override
        public int getItemCount() {
            return msgs == null ? 0 : msgs.size();
        }

    }

    class NewsVH extends RecyclerView.ViewHolder {

        private final TextView time;

        private final TextView title;
        private final ImageView unRead;
        private final TextView des;
        private final TextView horizontal;
        private final ImageView img;
        private final CardView cv;

        NewsVH(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            unRead = itemView.findViewById(R.id.unRead);
            des = itemView.findViewById(R.id.des);
            horizontal = itemView.findViewById(R.id.horizontal);
            img = itemView.findViewById(R.id.img);
            cv = itemView.findViewById(R.id.cv);
        }

        public void setData(final Msg n) {
            if (n.getMsgType() == Constant.NewsType.ACTIVITY || n.getMsgType() == Constant.NewsType.ANNOUNCEMENT
                    || n.getMsgType() == Constant.NewsType.CATEGORY || n.getMsgType() == Constant.NewsType.GOOD_DETAIL) {
                //有图片就展示图片，有文字就展示文字
                des.setVisibility((n.getMsgContent() != null && n.getMsgContent().length() > 0) ? View.VISIBLE : View.GONE);
                if (n.getMsgImg() != null && n.getMsgImg().length() > 0) {
                    horizontal.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    GlideUtil.GlideWithPlaceHolder(getApplicationContext(), n.getMsgImg()).into(img);
                } else {
                    horizontal.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                }
            } else {
                //订单消息，售后消息没有图片
                des.setVisibility(View.VISIBLE);
                horizontal.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
            }

            if(n.getMsgType() == Constant.NewsType.ANNOUNCEMENT){
                des.setMaxLines(3);
            }

            des.setText(n.getMsgContent());
            unRead.setVisibility(n.getIsRead() == 0 ? View.VISIBLE : View.GONE);
            time.setText(n.getSendTime());
            title.setText(n.getMsgTitle());
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleMsg(n);
                }
            });
        }

    }

    /**
     * 检查是否通过推送进入本页面
     * 如果是就处理指定消息
     */
    private void goDetailByPush() {
        String msgId = getIntent().getStringExtra("msgId");
        if (msgId != null && goDetail) {
            goDetail = false;
            if (!"".equals(msgId) && msgs != null && msgs.size() > 0) {
                Msg temp = null;
                for (Msg msg : msgs) {
                    LogUtil.e("msg", msg.getMsgId() + "");
                    if (String.valueOf(msg.getMsgBgId()).equals(msgId)) {
                        temp = msg;
                        break;
                    }
                }
                if (temp != null) {
//                    ToastUtil.toast("temp" + temp.getMsgId());
                    handleMsg(temp);
                } else {
//                    ToastUtil.toast("temp null");
                }
            } else {
//                ToastUtil.toast("msgs null");
            }
        } else {
//            ToastUtil.toast("godetail " + goDetail);
        }
    }

    /**
     * 跳转消息详情。
     * @param n
     */
    private void handleMsg(final Msg n) {
        if(n.getIsRead() == 0) {
            setIsRead(n.getMsgId());
        }
        Intent intent;
        switch (n.getMsgType()) {
            case Constant.NewsType.ANNOUNCEMENT:
                intent = new Intent(getApplicationContext(), RegularActivity.class);
                intent.putExtra(Constant.Key.KEY_REGULAR_TYPE, Constant.RegularActivityType.REGULAR_ACTIVITY_TYPE_ANNOUNCEMENT);
                intent.putExtra(Constant.Key.KEY_MSG_CONTENT, n.getMsgContent());
                intent.putExtra(Constant.Key.KEY_MSG_TITLE, n.getMsgTitle());
                intent.putExtra(Constant.Key.KEY_MSG_IMG, n.getMsgImg());
                startActivity(intent);
                break;
            case Constant.NewsType.GOOD_DETAIL:
                intent = new Intent(getApplicationContext(), GoodsDetailActivity.class);
                intent.putExtra(Constant.Key.KEY_GOOD_ID_TYPE, Constant.GoodDetailIdType.SN);
                intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, n.getMsgParams());
                startActivity(intent);
                break;
            case Constant.NewsType.ACTIVITY:
                intent = new Intent(getApplicationContext(), PromotionActivity.class);
                intent.putExtra(Constant.Key.KEY_ACTIVITY_NAME, n.getMsgParams());
                startActivity(intent);
                break;
            case Constant.NewsType.ORDER:
                intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                intent.putExtra(Constant.Key.KEY_ORDER_ID_TYPE, "sn");
                intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, n.getMsgParams());
                startActivity(intent);
                break;
            case Constant.NewsType.CATEGORY:
                intent = new Intent(getApplicationContext(), CategoryActivity.class);
                intent.putExtra(Constant.Key.KEY_CATEGORY_TYPE, n.getMsgParams());
                startActivity(intent);
                break;
            case Constant.NewsType.COUPON:
                intent = new Intent(getApplicationContext(), CouponActivity.class);
                startActivity(intent);
                break;
            case Constant.NewsType.AFTER_SALE:
                MallApplication.showProgressDialog(true, NewsActivity.this);
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_INFO_BY_SN)
                                , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("sn", n.getMsgParams())
                                        .getParams());
                        MallApplication.showProgressDialog(false, NewsActivity.this);
                        final AfterSaleResult result = GsonUtil.parseJson(rs, AfterSaleResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            UIUtils.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent;
                                    AfterSaleOrder aso = result.getData();
                                    switch (aso.getState()) {
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_CANCEL:
                                            //前往取消
                                            intent = new Intent(NewsActivity.this, ReturnCancelActivity.class);
                                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID, String.valueOf(aso.getId()));
                                            break;
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_SERVICE:
//                                                    case Constant.OrderStatus.ORDER_AF_STATUS_INT_REJECT:
                                            //前往等待受理
                                            intent = new Intent(NewsActivity.this, ReturnCustomServiceActivity.class);
                                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID, String.valueOf(aso.getId()));
                                            break;
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_LOGISTIC:
                                            //前往填写物流信息
                                            intent = new Intent(NewsActivity.this, ReturnLogisticsActivity.class);
                                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID, String.valueOf(aso.getId()));
                                            break;
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_DONE_RETURN:
                                            //前往退款完成，展示信息
                                            intent = new Intent(NewsActivity.this, ReturnMoneyActivity.class);
                                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID, String.valueOf(aso.getId()));
                                            break;
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_GOODS:
                                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_RETURN:
                                            //前往收货确认，展示信息
                                            intent = new Intent(NewsActivity.this, ReturnSureGoodActivity.class);
                                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID, String.valueOf(aso.getId()));
                                            break;
                                        default:
                                            return;
                                    }
                                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
                break;
            default:
                break;
        }
        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
    }

    private void setIsRead(final long msgId) {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_SET_MSG_READ)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("msgId", String.valueOf(msgId))
                                .getParams());

            }
        });
        RuntimeConfig.setPushNumber(MallApplication.getContext(), -1);
    }
}
