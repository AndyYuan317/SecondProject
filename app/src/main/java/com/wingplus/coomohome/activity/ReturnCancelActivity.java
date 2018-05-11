package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
import com.wingplus.coomohome.web.result.BaseResult;

/**
 * 提交退货申请
 *
 * @author leaffun.
 *         Create on 2017/10/31.
 */
public class ReturnCancelActivity extends BaseActivity implements View.OnClickListener {

    private AfterSaleOrder aso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_cancel);

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
        TextView mRemark = findViewById(R.id.remark);
        TextView mApplyTime = findViewById(R.id.apply_time);
        TextView mAfterSaleCode = findViewById(R.id.after_sale_code);
        TextView mAfterSaleType = findViewById(R.id.after_sale_type);
        TextView mOrderCode = findViewById(R.id.order_code);

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


        mRemark.setText("* 申请已取消");


        mApplyTime.setText(aso.getDate());
        mAfterSaleCode.setText(aso.getCode());
        mAfterSaleType.setText("退货");
        mOrderCode.setText(aso.getOrderCode());

        GlideUtil.GlideWithPlaceHolder(ReturnCancelActivity.this, aso.getGoodsPhoto())
                .apply(new RequestOptions().bitmapTransform(new RoundCornersTransformation(ReturnCancelActivity.this, UIUtils.dip2px(2), RoundCornersTransformation.CornerType.ALL)))
                .into(good_img);
        good_name.setText(aso.getGoodsName());
        good_intro.setText(aso.getGoodsSpec());
        good_price.setText(PriceFixUtil.format(aso.getGoodsPrice()));
        nums.setText(String.valueOf(aso.getGoodsNum()));

        mReasonChoose.setText(aso.getReason());
        mReasonRemark.setText(aso.getDetail());
        imgContent.setAdapter(new GridAdapter(aso.getImgUrl(), ReturnCancelActivity.this));

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
                APIConfig.getDataIntoView(new Runnable() {
                    @Override
                    public void run() {
                        String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_AFTER_SALE_CANCEL)
                                , new ParamsBuilder()
                                        .addParam("token", RuntimeConfig.user.getToken())
                                        .addParam("id", String.valueOf(aso.getId()))
                                        .getParams());
                        BaseResult result = GsonUtil.fromJson(rs, BaseResult.class);
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            ToastUtil.toastByCode(result);
                            finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
