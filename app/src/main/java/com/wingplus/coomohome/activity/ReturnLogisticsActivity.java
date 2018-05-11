package com.wingplus.coomohome.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.bumptech.glide.request.RequestOptions;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.adapter.GridAdapter;
import com.wingplus.coomohome.component.GridViewMeasure;
import com.wingplus.coomohome.component.SpannableTextView;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.expend.RoundCornersTransformation;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.PriceFixUtil;
import com.wingplus.coomohome.util.TimeUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.AfterSaleOrder;
import com.wingplus.coomohome.web.entity.DlyCenter;
import com.wingplus.coomohome.web.entity.Shipping;
import com.wingplus.coomohome.web.entity.Store;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.AfterSaleResult;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.DlyCenterResult;
import com.wingplus.coomohome.web.result.ShippingResult;
import com.wingplus.coomohome.web.result.StoreResult;

import java.util.List;

/**
 * 物流选择
 *
 * @author leaffun.
 *         Create on 2017/11/1.
 */
public class ReturnLogisticsActivity extends BaseActivity implements View.OnClickListener {

    private CheckBox mReturnLogistics;
    private LinearLayout logisticLl;
    private TextView mLogisticsNum;
    private TextView mCompanyChoose;

    private CheckBox mReturnSelf;
    private LinearLayout selfLl;
    private TextView mStoreChoose;
    private TextView mDeliverDate;
    private DatePickerDialog datePickerDialog;

    private List<Shipping> mShippingList;
    private Shipping mShipping = null;
    private List<Store> ss;
    private Store mStore = null;

    private AfterSaleOrder aso;
    private TextView mRemark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return_logistics);

        initData();
    }

    private void initData() {
        final String asoi = getIntent().getStringExtra(Constant.Key.KEY_AFTER_SALE_ORDER_ID);
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_INFO)
                        , new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("id", asoi)
                                .getParams());
                AfterSaleResult result = GsonUtil.parseJson(rs, AfterSaleResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    aso = result.getData();
                    getShippingList();
                    getStoreList();
                    getDlyCenter();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });

                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });
    }

    private void initView() {
        if (aso == null) return;
        mRemark = findViewById(R.id.remark);
        TextView mApplyTime = findViewById(R.id.apply_time);
        TextView mAfterSaleCode = findViewById(R.id.after_sale_code);
        TextView mAfterSaleType = findViewById(R.id.after_sale_type);
        TextView mOrderCode = findViewById(R.id.order_code);

        //物流
        logisticLl = findViewById(R.id.logistics_ll);
        mReturnLogistics = findViewById(R.id.return_logistics);
        mStoreChoose = findViewById(R.id.store_choose);
        mDeliverDate = findViewById(R.id.deliver_date);

        selfLl = findViewById(R.id.self_ll);
        mReturnSelf = findViewById(R.id.return_self);
        mCompanyChoose = findViewById(R.id.company_choose);
        mLogisticsNum = findViewById(R.id.logistics_num);

        //退款
        TextView mDuesRefund = findViewById(R.id.dues_refunds);
        TextView mActualRefund = findViewById(R.id.actual_refunds);

        ImageView good_img = findViewById(R.id.good_img);
        TextView good_name = findViewById(R.id.good_title);
        TextView good_intro = findViewById(R.id.good_intro);
        SpannableTextView good_price = findViewById(R.id.good_price);
        SpannableTextView nums = findViewById(R.id.nums);

        TextView mReasonChoose = findViewById(R.id.reason_choose);
        TextView mReasonRemark = findViewById(R.id.reason_remark);
        GridViewMeasure imgContent = findViewById(R.id.img_content);

        EditText name = findViewById(R.id.name);
        EditText phone = findViewById(R.id.phone);


        mRemark.setText("* 申请已受理，请在14天内寄回商品并提交物流信息，过期将自动取消申请。" +
                "\n收件人 楷模家" +
                "\n地址   上海市徐汇区长乐路100号" +
                "\n电话   400-0000-000");
        mApplyTime.setText(aso.getDate());
        mAfterSaleCode.setText(aso.getCode());
        mAfterSaleType.setText("退货");
        mOrderCode.setText(aso.getOrderCode());

        mDuesRefund.setText(PriceFixUtil.format(aso.getAmount()));
        mActualRefund.setText(PriceFixUtil.format(aso.getAmount()));


        GlideUtil.GlideWithPlaceHolder(ReturnLogisticsActivity.this, aso.getGoodsPhoto())
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(ReturnLogisticsActivity.this, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(good_img);
        good_name.setText(aso.getGoodsName());
        good_intro.setText(aso.getGoodsSpec());
        good_price.setText(PriceFixUtil.format(aso.getGoodsPrice()));
        nums.setText(String.valueOf(aso.getGoodsNum()));

        mReasonChoose.setText(aso.getReason());
        mReasonRemark.setText(aso.getDetail());
        imgContent.setAdapter(new GridAdapter(aso.getImgUrl(), ReturnLogisticsActivity.this));

        name.setEnabled(false);
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
        phone.setEnabled(false);
        phone.setFocusable(false);
        phone.setFocusableInTouchMode(false);
        name.setText(aso.getPerson());
        phone.setText(aso.getPhone());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cancel_apply:
                finish();
                break;
            case R.id.push:
                doPush();
                break;
            case R.id.return_logistics:
                mReturnLogistics.setChecked(true);
                logisticLl.setVisibility(View.VISIBLE);
                mReturnSelf.setChecked(false);
                selfLl.setVisibility(View.GONE);
                break;
            case R.id.return_self:
                mReturnSelf.setChecked(true);
                selfLl.setVisibility(View.VISIBLE);
                mReturnLogistics.setChecked(false);
                logisticLl.setVisibility(View.GONE);
                break;
            case R.id.company_choose:
                if (mShippingList == null || mShippingList.size() == 0) {
                    getShippingList();
                } else {
                    showShippingDialog();
                }
                break;
            case R.id.deliver_date:
                showDatePickDlg();
                break;
            case R.id.store_choose:
                if (ss == null || ss.size() == 0) {
                    getStoreList();
                } else {
                    showStoreDialog();
                }
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

    private void getDlyCenter() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COMMON_DLY_CENTER)
                        , new ParamsBuilder()
                                .addParam("name", "")
                                .getParams());
                DlyCenterResult sr = GsonUtil.fromJson(rs, DlyCenterResult.class);
                if (sr != null && sr.getResult() == APIConfig.CODE_SUCCESS) {
                    DlyCenter dlyCenter = sr.getData();
                    String address = dlyCenter.getProvince() + dlyCenter.getCity() + dlyCenter.getRegion() + dlyCenter.getAddress();
                    String zip = dlyCenter.getZip();
                    String name = dlyCenter.getUname();
                    String phone = dlyCenter.getCellphone();
                    final String longlongtxt = "* 申请已受理，请在14天内寄回商品并提交物流信息，过期将自动取消申请。" +
                            "\n收件人 " + name +
                            "\n地址     " + address +
                            "\n电话     " + phone +
                            "\n邮编     " + zip;
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mRemark.setText(longlongtxt);
                        }
                    });
                } else {
                    ToastUtil.toastByCode(sr);
                }
            }
        });
    }

    private void getShippingList() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COMMON_SHIPPING_COM_LIST)
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
            if (mShipping != null && mShipping.getId() == mShippingList.get(i).getId()) {
                now = i;
            }
        }

        new AlertDialog.Builder(ReturnLogisticsActivity.this)
                .setTitle("选择物流公司")
                .setSingleChoiceItems(name, now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mShipping = mShippingList.get(which);
                        mCompanyChoose.setText(mShipping.getName());
                    }
                }).show();
    }

    private void getStoreList() {
        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_STORE_LIST),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .getParams());

                final StoreResult storeResult = GsonUtil.fromJson(rs, StoreResult.class);

                if (storeResult != null && storeResult.getResult() == APIConfig.CODE_SUCCESS) {
                    ss = storeResult.getData();

                } else {
                    ToastUtil.toastByCode(storeResult);
                }
            }
        });
    }

    private void showStoreDialog() {
        if (ss == null || ss.size() == 0) {
            ToastUtil.toast("系统错误");
            return;
        }

        String[] name = new String[ss.size()];
        final long[] id = new long[ss.size()];
        int now = 0;
        for (int i = 0; i < ss.size(); i++) {
            name[i] = ss.get(i).getName();
            id[i] = ss.get(i).getId();
            if (mStore != null && mStore.getId() == ss.get(i).getId()) {
                now = i;
            }
        }

        new AlertDialog.Builder(ReturnLogisticsActivity.this)
                .setTitle("选择门店")
                .setSingleChoiceItems(name, now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mStore = ss.get(which);
                        mStoreChoose.setText(mStore.getName());
                    }
                }).show();
    }

    protected void showDatePickDlg() {
        String bs = mDeliverDate.getText().toString();
        if (bs.length() == 0) {
            bs = TimeUtil.mesToYMD(System.currentTimeMillis());
        }
        String[] strings = bs.split("-");
        datePickerDialog = new DatePickerDialog(ReturnLogisticsActivity.this, R.style.MyDatePickerStyle, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDeliverDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                datePickerDialog.dismiss();
            }
        }, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
        datePickerDialog.show();
    }

    private void doPush() {
        final String shippingType;
        final String expressName;
        final String express;

        if (mReturnLogistics.isChecked()) {
            if (mShipping == null) {
                ToastUtil.toast("请选择物流公司");
                return;
            }
            if (mLogisticsNum.getText().toString().trim().length() <= 0) {
                ToastUtil.toast("请填写快递单号");
                return;
            }
            shippingType = Constant.ShippingType.DELIVERY;
            express = String.valueOf(mShipping.getId());
            expressName = mShipping.getName();
        } else {
            if (mStore == null) {
                ToastUtil.toast("请选择送达门店");
                return;
            }
            if (mDeliverDate.getText().toString().length() <= 0) {
                ToastUtil.toast("请选择送货日期");
                return;
            }
            shippingType = Constant.ShippingType.PICKSELF;
            express = String.valueOf(mStore.getDepot_id());
            expressName = mStore.getName();
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_SHIPPING),
                        new ParamsBuilder()
                                .addParam("token", RuntimeConfig.user.getToken())
                                .addParam("type", shippingType)
                                .addParam("id", String.valueOf(aso.getId()))
                                .addParam("express", express)
                                .addParam("expressName", expressName)
                                .addParam("expressNo", mLogisticsNum.getText().toString().trim())
                                .addParam("wareId", express)
                                .addParam("deliverDate", mDeliverDate.getText().toString())
                                .getParams());
                BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    ToastUtil.toast("提交物流成功");
                    finish();
                    overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }
}
