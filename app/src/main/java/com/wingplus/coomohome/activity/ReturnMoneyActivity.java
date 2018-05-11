package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.AfterSaleOrder;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.AfterSaleResult;

/**
 * 收货确认
 *
 * @author leaffun.
 *         Create on 2017/11/1.
 */
public class ReturnMoneyActivity extends BaseActivity implements View.OnClickListener {

    private AfterSaleOrder aso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_return_money);

        initData();
    }

    private void initView() {
        TextView mRemark = findViewById(R.id.remark);
        TextView mApplyTime = findViewById(R.id.apply_time);
        TextView mAfterSaleCode = findViewById(R.id.after_sale_code);
        TextView mAfterSaleType = findViewById(R.id.after_sale_type);
        TextView mOrderCode = findViewById(R.id.order_code);

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

        TextView name = findViewById(R.id.name);
        TextView phone = findViewById(R.id.phone);

        TextView mLogisticsCompany = findViewById(R.id.logistics_company);
        TextView mLogisticsNum = findViewById(R.id.logistics_num);
        TextView mCompany = findViewById(R.id.type_company);
        TextView mNum = findViewById(R.id.type_num);


        mRemark.setText("* 退款成功，款项将按原支付方式退回，预计需要1-5个工作日。");
        mApplyTime.setText(aso.getDate());
        mAfterSaleCode.setText(aso.getCode());
        mAfterSaleType.setText("退货");
        mOrderCode.setText(aso.getOrderCode());

        mDuesRefund.setText(PriceFixUtil.format(aso.getAmount()));
        mActualRefund.setText(PriceFixUtil.format(aso.getAmount()));

        GlideUtil.GlideWithPlaceHolder(ReturnMoneyActivity.this, aso.getGoodsPhoto())
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(ReturnMoneyActivity.this, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(good_img);
        good_name.setText(aso.getGoodsName());
        good_intro.setText(aso.getGoodsSpec());
        good_price.setText(PriceFixUtil.format(aso.getGoodsPrice()));
        nums.setText(String.valueOf(aso.getGoodsNum()));

        mReasonChoose.setText(aso.getReason());
        mReasonRemark.setText(aso.getDetail());
        imgContent.setAdapter(new GridAdapter(aso.getImgUrl(), ReturnMoneyActivity.this));

        name.setEnabled(false);
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
        phone.setEnabled(false);
        phone.setFocusable(false);
        phone.setFocusableInTouchMode(false);
        name.setText(aso.getPerson());
        phone.setText(aso.getPhone());

        if(Constant.ShippingType.DELIVERY.equals(""+aso.getShippingType())){
            mLogisticsCompany.setText(aso.getExpressName());
            mLogisticsNum.setText(aso.getExpressNo());
        }else{
            mCompany.setText("物流方式");
            mLogisticsCompany.setText("自送");
            mNum.setText("送达门店");
            mLogisticsNum.setText(aso.getExpressName());
        }
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
}
