package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.StringUtil;
import com.wingplus.coomohome.util.TimeUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.entity.OrderMake;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.OrderMakeResult;

/**
 * 订单详情
 *
 * @author leaffun.
 *         Create on 2017/10/11.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rv;
    private OrderMake order;
    private RelativeLayout payParent;
    private SpannableTextView dues;
    private TextView cancelOrder;
    private TextView toPay;

    private Handler handler;
    private Runnable runnable;
    private OrderDetailAdapter adapter;
    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.cancel_order:
                new AlertDialog.Builder(this, R.style.AlertSelf).setTitle("确认取消订单吗？")
//                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“确认”后的操作
                                APIConfig.getDataIntoView(new Runnable() {
                                    @Override
                                    public void run() {
                                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_CANCEL),
                                                new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                                        .addParam("id", "" + orderId)
                                                        .addParam("reason", "未填写订单取消原因")
                                                        .getParams());
                                        BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                            finish();
                                        }
                                        ToastUtil.toastByCode(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("再想一想", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“返回”后的操作,这里不设置没有任何操作
                            }
                        }).show();


                break;
            case R.id.to_pay:
                Intent intent = new Intent(OrderDetailActivity.this, PayActivity.class);
                intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, orderId);
                startActivity(intent);
                overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
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
        rv = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new OrderDetailAdapter();
        rv.setAdapter(adapter);

        payParent = findViewById(R.id.pay_parent);
        dues = findViewById(R.id.dues);
        cancelOrder = findViewById(R.id.cancel_order);
        toPay = findViewById(R.id.to_pay);
    }

    private void initEvent() {
        cancelOrder.setOnClickListener(this);
        toPay.setOnClickListener(this);
    }

    private void initData() {
        String type;
        String api;
        orderId = getIntent().getStringExtra(Constant.Key.KEY_ORDER_ID_OR_CODE);
        type = getIntent().getStringExtra(Constant.Key.KEY_ORDER_ID_TYPE);
        api = APIConfig.API_ORDER_INFO_BY_SN;
        if (type == null || "".equals(type)) {
            type = "id";
            api = APIConfig.API_ORDER_INFO;
        }
        final String id = type;
        final String url = api;
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(url)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam(id, orderId)
                                .getParams());

                final OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            order = result.getData();
                            orderId = String.valueOf(order.getId());
                            if (order != null) {
                                if (order.getSubOrders().get(0).getStatus() == Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY) {
                                    payParent.setVisibility(View.VISIBLE);
                                    dues.setText(String.valueOf(order.getPayment()));
                                    downTime();
                                } else {
                                    payParent.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else {
                    ToastUtil.toastByCode(result);
                    finish();
                }
                ///////////
            }
        });


    }


    private class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new AddressVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_order_detail_address, parent, false));
            }
            if (viewType == 1) {
                return new CodeVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_order_detail_code, parent, false));
            }
            if (viewType == 2) {
                return new GoodVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_activity_order_make_good, parent, false));
            }
            if (viewType == 3) {
                return new ControlVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_order_detail_control, parent, false));
            }
            if (viewType == 4) {
                return new PriceVH(LayoutInflater.from(getApplicationContext()).inflate(R.layout.vh_activity_order_detail_price, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GoodVH) {
                ((GoodVH) holder).setData(order.getSubOrders().get(0).getGoods().get(position - 2));
            } else if (holder instanceof ControlVH) {
                ((ControlVH) holder).setData(order.getSubOrders().get(0).getStatus());
            } else if (holder instanceof CodeVH) {
                ((CodeVH) holder).setData();
            } else if (holder instanceof AddressVH) {
                OrderAddress ads = order.getAddress();
                ((AddressVH) holder).setData(ads, order.getShipping().getName());
            }
        }

        @Override
        public int getItemCount() {
            if (order == null) {
                return 0;
            }
            return 2 + order.getSubOrders().get(0).getGoods().size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < 2) {
                return position;//地址、编号
            }
            if (position < order.getSubOrders().get(0).getGoods().size() + 2) {
                return 2;//商品
            }
            if (position == order.getSubOrders().get(0).getGoods().size() + 2) {
                return 3;//操作按钮
            } else {
                return 4;//价格服务信息
            }
        }
    }

    private class AddressVH extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView phone;
        private final TextView address;


        AddressVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);

        }

        private void setData(OrderAddress ads, String type) {
            if ("自提".equals(type)) {
                name.setText("自提");
                phone.setVisibility(View.INVISIBLE);
                address.setVisibility(View.INVISIBLE);
            } else {
                name.setText(ads.getPerson());
                phone.setText(ads.getPhone());
                address.setText(ads.getProvince() + ads.getCity() + ads.getDistrict() + ads.getAddress());
            }
        }

    }

    private class CodeVH extends RecyclerView.ViewHolder {

        private final TextView code;
        private final TextView status;

        CodeVH(View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.order_code);
            status = itemView.findViewById(R.id.order_status);
        }

        private void setData() {
            code.setText(order.getSubOrders().get(0).getOrderCode());
            status.setText(StringUtil.getStrByOrderStatus(order.getSubOrders().get(0).getStatus()));
        }
    }

    private class GoodVH extends RecyclerView.ViewHolder {
        private ImageView good_img;
        private TextView good_name;
        private TextView good_intro;
        private SpannableTextView good_price;
        private TextView nums;

        GoodVH(View itemView) {
            super(itemView);
            good_img = itemView.findViewById(R.id.good_img);
            good_name = itemView.findViewById(R.id.good_title);
            good_intro = itemView.findViewById(R.id.good_intro);
            good_price = itemView.findViewById(R.id.good_price);
            nums = itemView.findViewById(R.id.nums);
            nums.setVisibility(View.VISIBLE);
        }

        public void setData(GoodShow gi) {
            GlideUtil.GlideWithPlaceHolder(OrderDetailActivity.this, gi.getImgUrl().get(0)).into(good_img);
            good_name.setText(gi.getName());
            good_intro.setText(gi.getSpec());
            good_price.setText(String.valueOf(gi.getPrice()));
            nums.setText("x" + String.valueOf(gi.getNum()));
        }
    }

    private class ControlVH extends RecyclerView.ViewHolder {
        private final TextView applyAfterSale;
        private final View toLogistics;
        private final View toSure;
        private final View toCommit;

        ControlVH(View itemView) {
            super(itemView);
            applyAfterSale = itemView.findViewById(R.id.apply_after_sale);
            toLogistics = itemView.findViewById(R.id.to_logistics);
            toSure = itemView.findViewById(R.id.to_sure);
            toCommit = itemView.findViewById(R.id.to_commit);

            applyAfterSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AfterSaleActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            toLogistics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("自提".equals(order.getShipping().getName())) {
                        ToastUtil.toast("自提订单，无物流信息");
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, "" + order.getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
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
                                finish();
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
                    Intent intent = new Intent(getApplicationContext(), CommitActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });
        }

        private void setData(int status) {
            switch (status) {
                case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY:
                    toLogistics.setVisibility(View.GONE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    applyAfterSale.setVisibility(View.GONE);
                    setVisibility(false);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_WAITING_SEND:
                    toLogistics.setVisibility(View.GONE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    applyAfterSale.setVisibility(View.GONE);
                    setVisibility(false);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_ALREADY_SEND:
                    toLogistics.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.VISIBLE);
                    toCommit.setVisibility(View.GONE);
                    applyAfterSale.setVisibility(View.GONE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_DONE:
                case Constant.OrderStatus.ORDER_STATUS_INT_IN_AFTER_SALE:
                    toLogistics.setVisibility(View.VISIBLE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    applyAfterSale.setVisibility(View.VISIBLE);
                    break;
                case Constant.OrderStatus.ORDER_STATUS_INT_CANCEL:
                default:
                    toLogistics.setVisibility(View.GONE);
                    toSure.setVisibility(View.GONE);
                    toCommit.setVisibility(View.GONE);
                    applyAfterSale.setVisibility(View.GONE);
                    setVisibility(false);
                    break;
            }
        }

        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

    private class PriceVH extends RecyclerView.ViewHolder {
        private final ImageView receiptChange;
        private final TextView receiptType;
        private final LinearLayout receiptCompanyInfo;
        private final TextView receiptTitle;
        private final TextView receiptNum;
//        private final TextView receiptAddr;
//        private final TextView receiptBank;
//        private final TextView receiptBankNum;
//        private final TextView receiptPhone;

        private final TextView payTime;
        private final TextView payType;
        private final TextView goodCount;
        private final TextView freightCount;
        private final TextView activityCount;
        private final TextView couponCount;
        private final TextView payCount;
        private final TextView payWord;
        private final TextView online_service;

        PriceVH(View itemView) {
            super(itemView);
            receiptChange = itemView.findViewById(R.id.change);
            receiptType = itemView.findViewById(R.id.receipt_type);
            receiptCompanyInfo = itemView.findViewById(R.id.receipt_company_info);
            receiptTitle = itemView.findViewById(R.id.receipt_title);
            receiptNum = itemView.findViewById(R.id.receipt_num);
//            receiptAddr = itemView.findViewById(R.id.receipt_address);
//            receiptBank = itemView.findViewById(R.id.receipt_bank);
//            receiptBankNum = itemView.findViewById(R.id.receipt_bank_num);
//            receiptPhone = itemView.findViewById(R.id.receipt_phone);


            payTime = itemView.findViewById(R.id.pay_time);
            payType = itemView.findViewById(R.id.payment);
            goodCount = itemView.findViewById(R.id.good_count);
            freightCount = itemView.findViewById(R.id.freight_count);
            couponCount = itemView.findViewById(R.id.coupon_count);
            activityCount = itemView.findViewById(R.id.activity_count);
            payCount = itemView.findViewById(R.id.pay_count);
            payWord = itemView.findViewById(R.id.pay_word);
            online_service = itemView.findViewById(R.id.online_service);

            payTime.setText(order.getSubOrders().get(0).getOrderTime());
            payType.setText(order.getSubOrders().get(0).getPayType());
            goodCount.setText(String.valueOf(order.getPrice().getTotal()));
            freightCount.setText(String.valueOf(order.getPrice().getShipping()));
            couponCount.setText(String.valueOf(order.getPrice().getCoupon()));
            activityCount.setText(String.valueOf(order.getPrice().getActivity()));
            payCount.setText(String.valueOf(order.getSubOrders().get(0).getStatus() == Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY ? order.getPayment() : order.getPayment()));
            payWord.setText(order.getSubOrders().get(0).getStatus() == Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY
                    || order.getSubOrders().get(0).getStatus() == Constant.OrderStatus.ORDER_STATUS_INT_CANCEL
                    ? "应付" : "实付");
            online_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OrderDetailActivity.this, CustomerServiceActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            //发票信息
            receiptChange.setVisibility(View.GONE);//不可修改
            int rt;
            if (order.getReceipt() == null) {
                rt = Constant.ReceiptType.NONE;
            } else {
                rt = order.getReceipt().getReceiptType();
            }
            switch (rt){
                case Constant.ReceiptType.NONE:
                    receiptType.setText("不需要发票");
                    receiptCompanyInfo.setVisibility(View.GONE);
                    break;
                case Constant.ReceiptType.PERSON:
                    receiptType.setText("个人");
                    receiptCompanyInfo.setVisibility(View.GONE);
                    break;
                case Constant.ReceiptType.COMPANY:
                    receiptType.setText("公司");
                    receiptCompanyInfo.setVisibility(View.VISIBLE);
                    receiptTitle.setText(order.getReceipt().getTitle());
                    receiptNum.setText(order.getReceipt().getTexNo());
//                receiptAddr.setText(order.getReceipt().getCompanyAddr());
//                receiptBank.setText(order.getReceipt().getCompanyBank());
//                receiptBankNum.setText(order.getReceipt().getCompanyBankNo());
//                receiptPhone.setText(order.getReceipt().getCompanyTel());

                    receiptTitle.setEnabled(false);
                    receiptNum.setEnabled(false);
//                receiptAddr.setEnabled(false);
//                receiptBank.setEnabled(false);
//                receiptBankNum.setEnabled(false);
//                receiptPhone.setEnabled(false);
                    break;
                default:
                    receiptType.setText("");
                    receiptCompanyInfo.setVisibility(View.GONE);
                    break;
            }
        }
    }


    private void downTime() {

        final long orderTime = TimeUtil.YMDHMToMes(order.getSubOrders().get(0).getOrderTime(), "yyyy-MM-dd HH:mm");
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                int goneTime = 0;
                final int between = 60 * 60 * 1000;//一小时内付款

                @Override
                public void run() {
                    goneTime = (int) (System.currentTimeMillis() - orderTime);
                    if (goneTime >= between) {
                        finish();
                        overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                        return;
                    }

                    LogUtil.i("goneTime", goneTime + "");
                    toPay.setText("付款 " + TimeUtil.mesToRx(between - goneTime));
                    if (between - goneTime > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        toPay.setText("付款 00:00");
                        toPay.setClickable(false);
                        runnable = null;
                        handler = null;
                    }
                }
            };
        }
        handler.post(runnable);
    }

}
