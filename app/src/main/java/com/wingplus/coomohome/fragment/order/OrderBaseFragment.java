package com.wingplus.coomohome.fragment.order;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.CartActivity;
import com.wingplus.coomohome.activity.CommitActivity;
import com.wingplus.coomohome.activity.MineOrderActivity;
import com.wingplus.coomohome.activity.OrderDetailActivity;
import com.wingplus.coomohome.activity.PayActivity;
import com.wingplus.coomohome.component.MyRefreshLayout;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.view.LoadingPage.LoadResult;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.Order;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;

import java.util.List;

/**
 * 页面加载完成，即开始请求数据，无刷新机制
 *
 * @author leaffun.
 *         Create on 2017/10/9.
 */
public abstract class OrderBaseFragment extends Fragment {

    public LoadingPage loadingPage;
    public Context context;
    protected MyRefreshLayout tr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        tr = (MyRefreshLayout) inflater.inflate(R.layout.framelayout, container, false);
        FrameLayout frameLayout = tr.findViewById(R.id.frameLayout);

        if (loadingPage == null) {
            loadingPage = new LoadingPage(getActivity()) {
                @Override
                public LoadResult load() {
                    return OrderBaseFragment.this.load(); // 将加载数据方法开放给子类
                }

                @Override
                protected void refreshUIInHere() {
                    OrderBaseFragment.this.refreshUIInHere();
                }

                @Override
                public View createSuccessLoad() {
                    return OrderBaseFragment.this.createSuccessLoad();
                }

                @Override
                public void setEmptyImage(ImageView img) {
                    GlideUtil.GlideInstance(context, R.drawable.icon_noorder).into(img);
                }
            };
        } else {
            ViewUtils.removeParent(loadingPage);
        }

        frameLayout.addView(loadingPage);
        tr.forbidden();
        tr.setEnableRefresh(true);
        tr.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                loadData();
            }

            @Override
            public void onFinishRefresh() {
                finishRefresh();
            }
        });

        // showState(); 请求数据，确认展示的状态，每一次预加载此页面，都会走此，但是切换viewpager时做出了响应，此处就应避免重复响应
        // showPage(); 根据state初始化界面，请求数据的时候已经在线程外初始化了一次
        return tr;
    }

    protected abstract void finishRefresh();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        LogUtil.i("order test", "onResume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        LogUtil.i("order test", "onViewStateRestored");
    }

    /**
     * 请求数据
     */
    public void loadData() {
        if (loadingPage != null) {
            loadingPage.showState();
        }
    }

    /**
     * 请求数据，返回枚举结果
     *
     * @return
     */
    public abstract LoadResult load();

    /**
     * 用数据来渲染布局
     *
     * @return
     */
    public abstract View createSuccessLoad();

    /**
     * 请求到数据后在此方法内刷新
     */
    public abstract void refreshUIInHere();

    /**
     * 订单列表适配器
     */
    class OrderAdapter extends RecyclerView.Adapter<OrderVH> {
        private List<Order> orders;

        public OrderAdapter(List<Order> orderList) {
            this.orders = orderList;
        }

        public void setData(List<Order> orders) {
            this.orders = orders;
        }

        @Override
        public OrderVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OrderVH(LayoutInflater.from(context).inflate(R.layout.item_fragment_order_all, parent, false));
        }

        @Override
        public void onBindViewHolder(OrderVH holder, int position) {
            holder.setData(orders.get(position));
        }

        @Override
        public int getItemCount() {
            return orders == null ? 0 : orders.size();
        }
    }

    class OrderVH extends RecyclerView.ViewHolder {
        private final TextView orderCode;
        private final TextView orderStatus;
        private final TextView orderDues;
        private final LinearLayout goodContent;
        private final TextView toPay;
        private final TextView toBuy;
        private final TextView toSure;
        private final TextView toCommit;

        OrderVH(View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.order_code);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDues = itemView.findViewById(R.id.order_dues);
            goodContent = itemView.findViewById(R.id.good_content);

            toPay = itemView.findViewById(R.id.to_pay);
            toBuy = itemView.findViewById(R.id.to_buy);
            toSure = itemView.findViewById(R.id.to_sure);
            toCommit = itemView.findViewById(R.id.to_commit);
        }

        public void setData(final Order order) {
            orderCode.setText(order.getCode());
//            orderCode.setLongClickable(true);
//            orderCode.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//                    ClipData mClipData = ClipData.newPlainText("Label", order.getCode());
//                    cmb.setPrimaryClip(mClipData);
//                    ToastUtil.toast("已复制:"+order.getCode());
//                    return false;
//                }
//            });
            orderStatus.setText(needCommit()
                    && order.getState() >= Constant.OrderStatus.ORDER_STATUS_INT_DONE
                    && order.getState() != Constant.OrderStatus.ORDER_STATUS_INT_CANCEL
                    && order.getIsCommented() == Constant.CommentType.NO_COMMENTED
                    ? Constant.OrderStatus.ORDER_STATUS_WAITING_COMMIT : StringUtil.getStrByOrderStatus(order.getState()));
            if (order.getState() == Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY
                    || order.getState() == Constant.OrderStatus.ORDER_STATUS_INT_CANCEL) {
                orderDues.setText(String.valueOf(order.getNeedPayAmout()));
            } else {
                orderDues.setText(String.valueOf(order.getPayAmount()));
            }
            orderStatus.setTextColor(UIUtils.getColor(R.color.color_order_status));

            goodContent.removeAllViews();
            int orderGoodsNum = 0;
            for (int i = 0; i < order.getGoods().size(); i++) {
                GoodShow good = order.getGoods().get(i);
                LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_fragment_order_good, goodContent, false);
                ImageView good_img = ll.findViewById(R.id.good_img);
                TextView good_name = ll.findViewById(R.id.good_title);
                TextView good_intro = ll.findViewById(R.id.good_intro);
                SpannableTextView good_price = ll.findViewById(R.id.good_price);
                SpannableTextView nums = ll.findViewById(R.id.nums);

                GlideUtil.GlideInstance(context, good.getImgUrl().get(0))
                        .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(context, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                        .into(good_img);
                good_name.setText(good.getName());
                good_intro.setText(good.getSpec());
                good_price.setText(PriceFixUtil.format(good.getPrice()));
                nums.setText(String.valueOf(good.getNum()));

                orderGoodsNum += good.getNum();
                goodContent.addView(ll);
            }
            final int orderGoodsNumAll = orderGoodsNum;



            String from = getActivity().getIntent().getStringExtra("from");
            if("客服".equals(from)) {
                toPay.setVisibility(View.VISIBLE);
                toBuy.setVisibility(View.GONE);
                toSure.setVisibility(View.GONE);
                toCommit.setVisibility(View.GONE);


                toPay.setText("发送");
                toPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String orderPrice = "";
                        if (order.getState() == Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY
                                || order.getState() == Constant.OrderStatus.ORDER_STATUS_INT_CANCEL) {
                            orderPrice = order.getNeedPayAmout()+"";
                        } else {
                            orderPrice = order.getNeedPayAmout()+"";
                        }

                        String orderImg = "";
                        try {
                            orderImg = order.getGoods().get(0).getImgUrl().get(0);
                        }catch (Exception e){

                        }

                        Intent intent = new Intent();
                        intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, "订单编号：" + order.getCode());
                        intent.putExtra("订单商品数", "订单商品数：" + orderGoodsNumAll);
                        intent.putExtra("订单时间", "");
                        intent.putExtra("订单金额", "订单金额："+getActivity().getResources().getString(R.string.currency_symbol)+" "+orderPrice);
                        intent.putExtra("订单图片", orderImg);

                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                });
                return;
            }

            goodContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, "" + order.getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            switch (order.getState()) {
                case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY:
                    toPay.setVisibility(View.VISIBLE);
                    toBuy.setVisibility(View.GONE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_SEND:
                    toPay.setVisibility(View.GONE);
                    toBuy.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_ALREADY_SEND:
                    toPay.setVisibility(View.GONE);
                    toBuy.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.VISIBLE);
                    toCommit.setVisibility(View.GONE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_DONE:
                case Constant.OrderStatus.ORDER_STATUS_INT_IN_AFTER_SALE:
                    toPay.setVisibility(View.GONE);
                    toBuy.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(needCommit() && order.getIsCommented() == Constant.CommentType.NO_COMMENTED ? View.VISIBLE : View.GONE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_CANCEL:
                    orderStatus.setTextColor(UIUtils.getColor(R.color.titleBlack));
                default:
                    toPay.setVisibility(View.GONE);
                    toBuy.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    break;
            }

            toPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, "" + order.getId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            toBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    APIConfig.getDataIntoView(new Runnable() {
                        @Override
                        public void run() {
                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_CART_BUY_AGAIN)
                                    , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                            .addParam("orderId", String.valueOf(order.getId()))
                                            .getParams());
                            BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
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

            toCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommitActivity.class);
                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, "" + order.getId());
                    startActivity(intent);
                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, order.getId());
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            toSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    APIConfig.getDataIntoView(new Runnable() {
                        @Override
                        public void run() {
                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_ROGCONFIRM),
                                    new ParamsBuilder()
                                            .addParam("token", RuntimeConfig.user.getToken())
                                            .addParam("id", "" + order.getId())//订单id
                                            .getParams());
                            BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                ToastUtil.toast("收货成功");
                                ((MineOrderActivity) getActivity()).sureGet();
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });
                }
            });

        }

    }

    /**
     * 由全部、已收货、待评价页面更新数据
     */
    public void sureGet() {

    }

    /**
     * 由待评价页面处理页面展示
     */
    protected boolean needCommit() {
        return false;
    }
}
