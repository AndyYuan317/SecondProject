package com.wingplus.coomohome.fragment.aftersale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.GoodsDetailActivity;
import com.wingplus.coomohome.activity.ReturnApplyActivity;
import com.wingplus.coomohome.activity.ReturnCancelActivity;
import com.wingplus.coomohome.activity.ReturnCustomServiceActivity;
import com.wingplus.coomohome.activity.ReturnLogisticsActivity;
import com.wingplus.coomohome.activity.ReturnMoneyActivity;
import com.wingplus.coomohome.activity.ReturnSureGoodActivity;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;
import com.wingplus.coomohome.view.LoadingPage;
import com.wingplus.coomohome.web.entity.AfterSaleOrder;
import com.wingplus.coomohome.web.entity.AfterSaleValid;

import java.util.List;

/**
 * @author leaffun.
 *         Create on 2017/10/16.
 */
public abstract class AfterSaleBaseFragment extends BaseFragment {


    public LoadingPage loadingPage;
    public Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        if (loadingPage == null) {
            loadingPage = new LoadingPage(getActivity()) {
                @Override
                public LoadResult load() {
                    return AfterSaleBaseFragment.this.load(); // 将加载数据方法开放给子类
                }

                @Override
                public View createSuccessLoad() {
                    return AfterSaleBaseFragment.this.createSuccessLoad();
                }

                @Override
                protected void refreshUIInHere() {
                    AfterSaleBaseFragment.this.refreshUIInHere();
                }

                @Override
                public void setEmptyImage(ImageView img) {
                    GlideUtil.GlideInstance(context, R.drawable.icon_noorder).into(img);
                }
            };
        } else {
            ViewUtils.removeParent(loadingPage);
        }

        // showState(); 请求数据，确认展示的状态，每一次预加载此页面，都会走此，但是切换viewpager时做出了响应，此处就应避免重复响应
        // showPage(); 根据state初始化界面，请求数据的时候已经在线程外初始化了一次
        return loadingPage;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 请求数据 并刷新页面
     */
    public void loadData() {
        if (loadingPage != null) {
            loadingPage.showState();
        }
    }


    /**
     * 请求到数据后在此方法内刷新
     */
    public abstract void refreshUIInHere();


    /**
     * 请求数据，返回枚举结果
     *
     * @return
     */
    public abstract LoadingPage.LoadResult load();

    /**
     * 用数据来渲染布局
     *
     * @return
     */
    public abstract View createSuccessLoad();


    class OrderAdapter extends RecyclerView.Adapter<OrderVH> {

        private int afterSaleType;
        private List asvs;

        public OrderAdapter(List orderList, int afterSaleType) {
            this.asvs = orderList;
            this.afterSaleType = afterSaleType;
        }

        public void setData(List asvs){
            this.asvs = asvs;
        }

        @Override
        public OrderVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OrderVH(LayoutInflater.from(context).inflate(R.layout.vh_fragment_after_sale_order, parent, false));
        }

        @Override
        public void onBindViewHolder(OrderVH holder, int position) {
            if (afterSaleType == Constant.AfterSaleType.AFTER_SALE_RETURN) {
                holder.setData((AfterSaleValid) asvs.get(position), afterSaleType);
            } else {
                holder.setData((AfterSaleOrder) asvs.get(position), afterSaleType);
            }
        }

        @Override
        public int getItemCount() {
            return asvs == null ? 0 : asvs.size();
        }
    }


    class OrderVH extends RecyclerView.ViewHolder {
        private final SpannableTextView orderCode;
        private final TextView orderStatus;
        private final LinearLayout goodContent;
        private final TextView totalDuesTxt;
        private final TextView orderDues;
        private final TextView toReturn;

        OrderVH(View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.order_code);
            orderStatus = itemView.findViewById(R.id.order_status);
            totalDuesTxt = itemView.findViewById(R.id.total_dues_txt);
            orderDues = itemView.findViewById(R.id.order_dues);
            goodContent = itemView.findViewById(R.id.good_content);

            toReturn = itemView.findViewById(R.id.to_return);
        }

        public void setData(final AfterSaleValid asv, int afterSaleType) {

            orderCode.setText(asv.getOrderSn());
            orderStatus.setText(StringUtil.getStrByOrderStatus(asv.getOrderStatus()));
            orderDues.setText(PriceFixUtil.format(asv.getPrice()));
            orderStatus.setTextColor(UIUtils.getColor(R.color.color_order_status));

            goodContent.removeAllViews();

            LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_fragment_order_good, goodContent, false);
            ImageView good_img = ll.findViewById(R.id.good_img);
            TextView good_name = ll.findViewById(R.id.good_title);
            TextView good_intro = ll.findViewById(R.id.good_intro);
            SpannableTextView good_price = ll.findViewById(R.id.good_price);
            SpannableTextView nums = ll.findViewById(R.id.nums);

            GlideUtil.GlideWithPlaceHolder(context, asv.getGoodsImg()).into(good_img);
            good_name.setText(asv.getName());
            good_intro.setText(asv.getSpec());
            good_price.setText(PriceFixUtil.format(asv.getPrice()));
            nums.setText(String.valueOf(asv.getNum()));

            goodContent.addView(ll);


            goodContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra(Constant.Key.KEY_GOOD_ID_OR_SN, String.valueOf(asv.getGoodsId()));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });


            toReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReturnApplyActivity.class);
                    intent.putExtra(Constant.Key.KEY_AFTER_SALE_GOOD_STR, asv);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            if (afterSaleType == Constant.AfterSaleType.AFTER_SALE_RECORD) {
                toReturn.setVisibility(View.INVISIBLE);
                totalDuesTxt.setVisibility(View.VISIBLE);
                orderDues.setVisibility(View.VISIBLE);
            } else {
                toReturn.setVisibility(View.VISIBLE);
                totalDuesTxt.setVisibility(View.INVISIBLE);
                orderDues.setVisibility(View.INVISIBLE);
            }
        }

        public void setData(final AfterSaleOrder aso, int afterSaleType) {
            orderCode.setPreString("售后单号：");
            orderCode.setText(aso.getCode());
            orderStatus.setText(StringUtil.getStrByAfterSaleOrderStatus(aso.getState()));
            orderDues.setText(PriceFixUtil.format(aso.getAmount()));
            if(aso.getState() == Constant.OrderStatus.ORDER_AF_STATUS_INT_CANCEL){
                orderStatus.setTextColor(UIUtils.getColor(R.color.titleBlack));
            }else{
                orderStatus.setTextColor(UIUtils.getColor(R.color.color_order_status));
            }

            goodContent.removeAllViews();

            LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_fragment_order_good, goodContent, false);
            ImageView good_img = ll.findViewById(R.id.good_img);
            TextView good_name = ll.findViewById(R.id.good_title);
            TextView good_intro = ll.findViewById(R.id.good_intro);
            SpannableTextView good_price = ll.findViewById(R.id.good_price);
            SpannableTextView nums = ll.findViewById(R.id.nums);

            GlideUtil.GlideWithPlaceHolder(context, aso.getGoodsPhoto()).into(good_img);
            good_name.setVisibility(View.GONE);
            good_intro.setVisibility(View.GONE);
            good_price.setVisibility(View.GONE);
            nums.setText("");

            goodContent.addView(ll);


            goodContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    switch (aso.getState()) {
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_CANCEL:
                            intent = new Intent(context, ReturnCancelActivity.class);
                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID,  String.valueOf(aso.getId()));
                            break;
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_SERVICE:
//                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_REJECT:
                            //前往等待受理
                            intent = new Intent(context, ReturnCustomServiceActivity.class);
                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID,  String.valueOf(aso.getId()));
                            break;
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_LOGISTIC:
                            //前往填写物流信息
                            intent = new Intent(context, ReturnLogisticsActivity.class);
                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID,  String.valueOf(aso.getId()));
                            break;
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_DONE_RETURN:
                            //前往退款完成，展示信息
                            intent = new Intent(context, ReturnMoneyActivity.class);
                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID,  String.valueOf(aso.getId()));
                            break;
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_GOODS:
                        case Constant.OrderStatus.ORDER_AF_STATUS_INT_WAITING_RETURN:
                            //前往收货确认，展示信息
                            intent = new Intent(context, ReturnSureGoodActivity.class);
                            intent.putExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID,  String.valueOf(aso.getId()));
                            break;
                        default:
                            return;
                    }
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            if (afterSaleType == Constant.AfterSaleType.AFTER_SALE_RECORD) {
                toReturn.setVisibility(View.INVISIBLE);
                totalDuesTxt.setVisibility(View.VISIBLE);
                orderDues.setVisibility(View.VISIBLE);
            } else {
                toReturn.setVisibility(View.VISIBLE);
                totalDuesTxt.setVisibility(View.INVISIBLE);
                orderDues.setVisibility(View.INVISIBLE);
            }
        }

    }


    /**
     * 商品 ViewHolder
     */
    class GoodVH extends RecyclerView.ViewHolder {
        private SpannableTextView orderCode;
        private TextView orderStatus;
        private LinearLayout goodContent;
        private SpannableTextView orderDues;
        private TextView toReturn;

        public GoodVH(View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.order_code);
            orderStatus = itemView.findViewById(R.id.order_status);
            goodContent = itemView.findViewById(R.id.good_content);
            orderDues = itemView.findViewById(R.id.order_dues);
            toReturn = itemView.findViewById(R.id.to_return);
        }

        public void setData() {

        }
    }

}
