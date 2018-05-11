package com.wingplus.coomohome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.OrderUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.Coupon;
import com.wingplus.coomohome.web.entity.Order;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CouponResult;
import com.wingplus.coomohome.web.result.OrderAddressResult;

import java.util.List;

/**
 * 列表选择
 *
 * @author leaffun.
 *         Create on 2017/10/31.
 */
public class ListChooseActivity extends BaseActivity implements View.OnClickListener {

    private String chooseTitle;

    private List listData;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_choose);

        initView();
        initData();
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
        TextView title = findViewById(R.id.title);
        RecyclerView rv = findViewById(R.id.rv);
        chooseTitle = getIntent().getStringExtra(Constant.Key.KEY_LIST_CHOOSE);
        title.setText(chooseTitle);

        LinearLayoutManager m = new LinearLayoutManager(this);
        m.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(m);
        listAdapter = new ListAdapter();
        rv.setAdapter(listAdapter);

    }

    private void initData() {
        if (Constant.ListChooseType.ADDRESS.equals(chooseTitle)) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ADDRESS_LIST)
                            , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                    .getParams());

                    final OrderAddressResult result = GsonUtil.fromJson(rs, OrderAddressResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                listData = result.getData();
                                if (listData != null && listData.size() > 0) {
                                    OrderUtil.orderAddress(listData);
                                    listAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.toast("请先添加收货地址");
                                    Intent intent = new Intent(ListChooseActivity.this, OrderAddressEditActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                                }
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });
                }
            });
        } else if (Constant.ListChooseType.BONUS.equals(chooseTitle)) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_GET_COUPON_LIST)
                            , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                    .getParams());

                    final CouponResult result = GsonUtil.fromJson(rs, CouponResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                listData = result.getData();
                                if (listData != null && listData.size() > 0) {
                                    listAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.toast("无可用优惠券");
                                }
                            } else {
                                ToastUtil.toastByCode(result);
                            }
                        }
                    });
                }
            });
        }

    }

    private class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == Constant.ListChooseType.ADDRESS_INT) {
                return new AddressVH(LayoutInflater.from(ListChooseActivity.this).inflate(R.layout.vh_activity_order_address, parent, false));
            } else if (viewType == Constant.ListChooseType.BONUS_INT) {
                return new CouponVH(LayoutInflater.from(ListChooseActivity.this).inflate(R.layout.vh_fragment_coupon, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int pos = position;
            if (holder instanceof AddressVH) {
                ((AddressVH) holder).setData(pos);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(Constant.Key.KEY_ORDER_ADDRESS, (OrderAddress) listData.get(pos));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            } else if (holder instanceof CouponVH) {
                ((CouponVH) holder).setData((Coupon) listData.get(pos));
            }
        }

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (Constant.ListChooseType.ADDRESS.equals(chooseTitle)) {
                return Constant.ListChooseType.ADDRESS_INT;
            } else if (Constant.ListChooseType.BONUS.equals(chooseTitle)) {
                return Constant.ListChooseType.BONUS_INT;
            }
            return 0;
        }
    }

    private class AddressVH extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView phone;
        private final TextView addressDefault;
        private final TextView addres;
        private final ImageView edit;
        private final ImageView deleteAddress;

        AddressVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.acceptor_name);
            phone = itemView.findViewById(R.id.acceptor_phone);
            addressDefault = itemView.findViewById(R.id.acceptor_address_default);
            addres = itemView.findViewById(R.id.acceptor_address);
            edit = itemView.findViewById(R.id.edit);
            deleteAddress = itemView.findViewById(R.id.delete_address);
        }

        public void setData(final int i) {
            OrderAddress address = (OrderAddress) listData.get(i);
            name.setText(address.getPerson());
            phone.setText(address.getPhone());
            addres.setText(address.getProvince() + address.getCity() + address.getDistrict() + address.getAddress());
            addressDefault.setVisibility(address.getIsDefault() == Constant.OrderAddress.IS_DEFAULT ? View.VISIBLE : View.INVISIBLE);
            edit.setVisibility(View.GONE);
            deleteAddress.setVisibility(View.GONE);
        }
    }

    private class CouponVH extends RecyclerView.ViewHolder {

        private final TextView money;
        private final TextView couponRemark;
        private final TextView validRemark;
        private final TextView timeRemark;
        private final TextView useNow;
        private final ImageView status;

        CouponVH(View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            couponRemark = itemView.findViewById(R.id.coupon_remark);
            validRemark = itemView.findViewById(R.id.valid_remark);
            timeRemark = itemView.findViewById(R.id.time_remark);
            useNow = itemView.findViewById(R.id.use_now);
            status = itemView.findViewById(R.id.status);
        }

        public void setData(final Coupon coupon) {
            String s = coupon.getPrice() + "";
            int v = (int) (coupon.getPrice() * 1000 % 1000);
            final boolean contains = s.contains(".");
            if (contains && v == 0) {
                s = s.substring(0, s.indexOf("."));
            }
            money.setText(s);
            couponRemark.setText(coupon.getName());
            validRemark.setText(coupon.getDescription());
            timeRemark.setText(coupon.getStartDate() + "至" + coupon.getEndDate());

            if (coupon.getState() == Constant.CouponStatus.FRESH) {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back_ac));
                useNow.setTextColor(UIUtils.getColor(R.color.colorPrimary));
                status.setVisibility(View.GONE);
            } else if (coupon.getState() == Constant.CouponStatus.ALREADY) {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back));
                useNow.setTextColor(UIUtils.getColor(R.color.gray_light));
                status.setVisibility(View.VISIBLE);
                GlideUtil.GlideWithPlaceHolder(ListChooseActivity.this, R.drawable.already).into(status);
            } else {
                useNow.setBackgroundColor(UIUtils.getColor(R.color.coupon_use_back));
                useNow.setTextColor(UIUtils.getColor(R.color.gray_light));
                status.setVisibility(View.VISIBLE);
                GlideUtil.GlideWithPlaceHolder(ListChooseActivity.this, R.drawable.invalid).into(status);
            }


            useNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.Key.KEY_ORDER_BONUS, (String.valueOf((coupon.getId()))));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
