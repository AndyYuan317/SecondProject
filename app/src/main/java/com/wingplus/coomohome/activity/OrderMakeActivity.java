package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Coupon;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.entity.OrderApply;
import com.wingplus.coomohome.web.entity.OrderMake;
import com.wingplus.coomohome.web.entity.OrderMakePrice;
import com.wingplus.coomohome.web.entity.Shipping;
import com.wingplus.coomohome.web.entity.SubOrder;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.OrderApplyResult;
import com.wingplus.coomohome.web.result.OrderMakeResult;
import com.wingplus.coomohome.web.result.ShippingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单确认
 *
 * @author leaffun.
 *         Create on 2017/9/30.
 */
public class OrderMakeActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ADDRESS = 0;
    private static final int REQUEST_CODE_BONUS = 1;


    private RecyclerView rv;
    private OrderAdapter adapter;
    private SpannableTextView allTotal;

    private List<Shipping> mShippingList;
    private OrderMake mOrderMake;
    private List<GoodShow> mGoods;
    private OrderAddress mOrderAddress = null;
    private Shipping mShipping = null;
    private Coupon mCoupon = null;

    private int chosenReceipt = Constant.ReceiptType.NONE;
    private String title = "";
    private String texNo = "";
    private String companyAddr = "";
    private String companyBank = "";
    private String companyBankNo = "";
    private String companyTel = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_make);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rv = findViewById(R.id.order_rv);
        allTotal = findViewById(R.id.all_total);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);

        adapter = new OrderAdapter();
        rv.setAdapter(adapter);
    }

    private void initEvent() {

    }

    private void initData() {
        String orderMake = getIntent().getStringExtra(Constant.Key.KEY_ORDER_UNSURE);
        OrderMakeResult result = GsonUtil.fromJson(orderMake, OrderMakeResult.class);
        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
            setResult(result);

            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_GET_SHIPPING_LIST)
                            , new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .getParams());
                    ShippingResult sr = GsonUtil.fromJson(rs, ShippingResult.class);
                    if (sr != null && sr.getResult() == APIConfig.CODE_SUCCESS) {
                        mShippingList = sr.getData();
                    } else {
                        ToastUtil.toastByCode(sr);
                    }
                }
            });
        } else {
            finish();
        }
    }

    private void setResult(final OrderMakeResult result) {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mOrderMake = result.getData();
                mOrderAddress = mOrderMake.getAddress();
                mShipping = mOrderMake.getShipping();
                mCoupon = mOrderMake.getCoupon();
                allTotal.setText(PriceFixUtil.formatPay(mOrderMake.getPayment()));
                if (mOrderMake != null) {
                    mGoods = new ArrayList<>();
                    List<SubOrder> subOrders = mOrderMake.getSubOrders();
                    for (SubOrder so : subOrders) {
                        for (GoodShow gs : so.getGoods()) {
                            mGoods.add(gs);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.make_order:
                if(chosenReceipt == Constant.ReceiptType.COMPANY){
                    if(title.isEmpty() || texNo.isEmpty()){
                        ToastUtil.toast("请完善发票信息");
                        return;
                    }
                }

                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_CONFIRM)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("addressId", "" + mOrderAddress.getId())
                                        .addParam("shippingId", "" + mShipping.getId())
                                        .addParam("bonusId", "" + (mCoupon == null ? 0 : mCoupon.getId()))
                                        .addParam("receiptType", ""+ chosenReceipt)
                                        .addParam("title", title)
                                        .addParam("texNo", texNo)
//                                        .addParam("companyAddr", companyAddr)
//                                        .addParam("companyBank", companyBank)
//                                        .addParam("companyBankNo", companyBankNo)
//                                        .addParam("companyTel", companyTel)
                                        .getParams());
                        OrderApplyResult result = GsonUtil.fromJson(rs, OrderApplyResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            Intent intent = new Intent(OrderMakeActivity.this, PayActivity.class);
                            List<OrderApply> data = result.getData();
                            if (data != null && data.size() > 0) {
                                String ids = "";
                                for (OrderApply orderApply : data) {
                                    ids += orderApply.getOrderId() + ",";
                                }
                                ids.substring(0, ids.length() - 1);
                                intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE, ids);
                            }
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                        } else {
                            ToastUtil.toastByCode(result);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new OrderVH(LayoutInflater.from(OrderMakeActivity.this).inflate(R.layout.item_actitvity_order_make, parent, false));
            }
            return new GoodVH(LayoutInflater.from(OrderMakeActivity.this).inflate(R.layout.item_activity_order_make_good, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof OrderVH) {
                ((OrderVH) holder).setData();
            } else if (holder instanceof GoodVH) {
                ((GoodVH) holder).setData(mGoods.get(position - 1));

            }
        }

        @Override
        public int getItemCount() {
            if (mOrderMake == null) {
                finish();
                return 0;
            } else {
                List<SubOrder> subOrders = mOrderMake.getSubOrders();
                int count = 0;
                for (SubOrder so : subOrders) {
                    count += so.getGoods().size();
                }
                return 1 + count;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }
    }

    /**
     * 计价 ViewHolder
     */
    private class OrderVH extends RecyclerView.ViewHolder {

//        private final RelativeLayout sendRl;
        private final RelativeLayout bonusRl;
        private final TextView sendMode;
        private final LinearLayout addressLl;
        private final TextView name;

        private final TextView phone;
        private final TextView addressNum;
        private final TextView address;
        private final SpannableTextView total;

        private final SpannableTextView shipping;
        private final SpannableTextView activity;
        private final SpannableTextView coupon;
        private final TextView couponDes;

        private final RelativeLayout receiptRl;
        private final TextView receiptType;
        private final LinearLayout receiptCompanyInfo;
        private final TextView receiptTitle;
        private final TextView receiptNum;
//        private final TextView receiptAddr;
//        private final TextView receiptBank;
//        private final TextView receiptBankNum;
//        private final TextView receiptPhone;

        OrderVH(View itemView) {
            super(itemView);
//            sendRl = itemView.findViewById(R.id.send_rl);
            bonusRl = itemView.findViewById(R.id.bonus_rl);
            sendMode = itemView.findViewById(R.id.send_mode);

            addressLl = itemView.findViewById(R.id.address_ll);
            name = itemView.findViewById(R.id.acceptor_name);
            phone = itemView.findViewById(R.id.acceptor_phone);
            addressNum = itemView.findViewById(R.id.acceptor_address_num);
            address = itemView.findViewById(R.id.acceptor_address);

            couponDes = itemView.findViewById(R.id.coupon_name);

            total = itemView.findViewById(R.id.total);
            shipping = itemView.findViewById(R.id.shipping);
            coupon = itemView.findViewById(R.id.coupon);
            activity = itemView.findViewById(R.id.activity);

            receiptRl = itemView.findViewById(R.id.receipt_rl);
            receiptType = itemView.findViewById(R.id.receipt_type);
            receiptCompanyInfo = itemView.findViewById(R.id.receipt_company_info);
            receiptTitle = itemView.findViewById(R.id.receipt_title);
            receiptNum = itemView.findViewById(R.id.receipt_num);
//            receiptAddr = itemView.findViewById(R.id.receipt_address);
//            receiptBank = itemView.findViewById(R.id.receipt_bank);
//            receiptBankNum = itemView.findViewById(R.id.receipt_bank_num);
//            receiptPhone = itemView.findViewById(R.id.receipt_phone);
            receiptTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    title = receiptTitle.getText().toString().trim();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            receiptNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    texNo = receiptNum.getText().toString().trim();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

//            sendRl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mShippingList == null || mShippingList.size() == 0) {
//                        APIConfig.getDataIntoView(new Runnable() {
//                            @Override
//                            public void run() {
//                                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_GET_SHIPPING_LIST)
//                                        , new ParamsBuilder()
//                                                .addParam("token", RuntimeConfig.user.getToken())
//                                                .getParams());
//                                ShippingResult sr = GsonUtil.fromJson(rs, ShippingResult.class);
//                                if (sr != null && sr.getResult() == APIConfig.CODE_SUCCESS) {
//                                    mShippingList = sr.getData();
//                                    showShippingDialog();
//                                } else {
//                                    ToastUtil.toastByCode(sr);
//                                }
//                            }
//                        });
//                    } else {
//                        showShippingDialog();
//                    }
//                }
//            });

            addressLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderMakeActivity.this, OrderAddressActivity.class);
                    intent.putExtra("from", "OrderMakeActivity");
                    startActivityForResult(intent, REQUEST_CODE_ADDRESS);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            bonusRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderMakeActivity.this, ListChooseActivity.class);
                    intent.putExtra(Constant.Key.KEY_LIST_CHOOSE, Constant.ListChooseType.BONUS);
                    startActivityForResult(intent, REQUEST_CODE_BONUS);
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                }
            });

            receiptRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(OrderMakeActivity.this)
                            .setTitle("选择发票类型")
                            .setSingleChoiceItems(Constant.ReceiptType.TYPE_STR, chosenReceipt, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    receiptType.setText(Constant.ReceiptType.TYPE_STR[which]);
                                    chosenReceipt = which;
                                    if(which == Constant.ReceiptType.COMPANY){
                                        receiptCompanyInfo.setVisibility(View.VISIBLE);
                                    }else{
                                        receiptCompanyInfo.setVisibility(View.GONE);
                                    }
                                }
                            }).show();
                }
            });
        }

        private void showShippingDialog() {
            if (mShippingList == null || mShippingList.size() == 0) {
                ToastUtil.toast("系统错误");
                return;
            }

            String[] name = new String[mShippingList.size()];
            final long[] id = new long[mShippingList.size()];
            int now = 0;
            for (int i = 0; i < mShippingList.size(); i++) {
                name[i] = mShippingList.get(i).getName();
                id[i] = mShippingList.get(i).getId();
                if (mShipping.getId() == mShippingList.get(i).getId()) {
                    now = i;
                }
            }

            new AlertDialog.Builder(OrderMakeActivity.this)
                    .setTitle("配送方式")
                    .setSingleChoiceItems(name, now, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            chooseShipping(id[which]);
                            mShipping = mShippingList.get(which);
                            sendMode.setText(mShipping.getName());
                        }
                    }).show();
        }

        public void setData() {
            if (mShipping != null) {
                sendMode.setText(mShipping.getName());
            }

            if (mOrderAddress != null) {
                name.setText(mOrderAddress.getPerson());
                phone.setText(mOrderAddress.getPhone());
                address.setText(mOrderAddress.getProvince() + mOrderAddress.getCity() + mOrderAddress.getDistrict() + mOrderAddress.getAddress());
                addressNum.setVisibility(mOrderAddress.getIsDefault() == Constant.OrderAddress.IS_DEFAULT ? View.VISIBLE : View.INVISIBLE);
            }

            if (mCoupon != null) {
                couponDes.setText(mCoupon.getDescription());
            }


            if (mOrderMake != null) {
                OrderMakePrice price = mOrderMake.getPrice();
                total.setText(PriceFixUtil.formatPay(price.getTotal()));
                shipping.setText(PriceFixUtil.formatPay(price.getShipping()));
                coupon.setText(PriceFixUtil.formatPay(-price.getCoupon()));
                activity.setText(PriceFixUtil.formatPay(price.getActivity()));
            }
        }
    }

    /**
     * 商品 ViewHolder
     */
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

        private void setData(GoodShow gs) {
            GlideUtil.GlideWithPlaceHolder(OrderMakeActivity.this, gs.getImgUrl().get(0)).into(good_img);
            good_name.setText(gs.getName());
            good_intro.setText(gs.getSpec());
            good_price.setText(PriceFixUtil.format(gs.getPrice()));
            nums.setText("x" + String.valueOf(gs.getNum()));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADDRESS) {
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        OrderAddress newAddress = (OrderAddress) data.getSerializableExtra(Constant.Key.KEY_ORDER_ADDRESS);
                        chooseAddress(newAddress);
                    }
                });

            } else if (requestCode == REQUEST_CODE_BONUS) {
                String couponId = data.getStringExtra(Constant.Key.KEY_ORDER_BONUS);
                chooseBonus(couponId);

            }
        }
    }

    /**
     * 选择新收货地址
     *
     * @param newAddress
     */
    private void chooseAddress(final OrderAddress newAddress) {
        if (newAddress == null) {
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_MODIFY_ADDRESS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("addressId", "" + newAddress.getId())
                                .addParam("shippingId", "" + mShipping.getId())
                                .addParam("bonusId", "" + (mCoupon == null ? 0 : mCoupon.getId()))
                                .getParams());
                OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    setResult(result);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }

    /**
     * 选择新优惠券
     *
     * @param newBonusId
     */
    private void chooseBonus(final String newBonusId) {
        if (newBonusId == null || newBonusId.length() == 0) {
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_MODIFY_BONUS),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("addressId", "" + mOrderAddress.getId())
                                .addParam("shippingId", "" + mShipping.getId())
                                .addParam("bonusId", "" + newBonusId)
                                .getParams());
                OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    setResult(result);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }

    /**
     * 选择新配送方式
     *
     * @param newShippingId
     */
    private void chooseShipping(final long newShippingId) {
        if (newShippingId == 0 || mShipping.getId() == newShippingId) {
            return;
        }
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_MODIFY_SHIPPING),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("addressId", "" + mOrderAddress.getId())
                                .addParam("shippingId", "" + newShippingId)
                                .addParam("bonusId", "" + (mCoupon == null ? 0 : mCoupon.getId()))
                                .getParams());
                OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    setResult(result);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }
}
