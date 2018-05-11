package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;

import com.pingplusplus.android.Pingpp;
import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.OrderMake;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.BaseResult;
import com.wingplus.coomohome.web.result.OrderMakeResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 付款
 *
 * @author leaffun.
 *         Create on 2017/10/31.
 */
public class PayActivity extends BaseActivity implements View.OnClickListener {

    private CheckBox wxPay;
    private CheckBox zfbPay;
//    private CheckBox cardPay;

    /**
     * 待支付的订单
     */
    private List<OrderMake> mOrders = new ArrayList<>();
    private String[] ids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        initView();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void initView() {
        wxPay = findViewById(R.id.wx_pay);
        zfbPay = findViewById(R.id.zfb_pay);
//        cardPay = findViewById(R.id.card_pay);

        wxPay.setChecked(true);
    }

    private void initData() {
        final String orderId = getIntent().getStringExtra(Constant.Key.KEY_ORDER_ID_OR_CODE);

        ids = orderId.split(",");
        for (final String id : ids) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_INFO)
                            , new ParamsBuilder()
                                    .addParam("token", RuntimeConfig.user.getToken())
                                    .addParam("id", id)
                                    .getParams());
                    final OrderMakeResult result = GsonUtil.fromJson(rs, OrderMakeResult.class);
                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                        mOrders.add(result.getData());
                        if (result.getData() == null) {
                            ToastUtil.toast("请刷新订单");
                            finish();
                        }
                    } else {
                        ToastUtil.toastByCode(result);
                        finish();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                sureToBack();
                break;
            case R.id.wx_ll:
                wxPay.setChecked(true);
                zfbPay.setChecked(false);
//                cardPay.setChecked(false);
                break;
            case R.id.zfb_ll:
                zfbPay.setChecked(true);
                wxPay.setChecked(false);
//                cardPay.setChecked(false);
                break;
//            case R.id.card_ll:
//                cardPay.setChecked(true);
//                wxPay.setChecked(false);
//                zfbPay.setChecked(false);
//                break;
            case R.id.to_pay:
                if (wxPay.isChecked()) {
                    getChargeFromServer("wx");
                } else if (zfbPay.isChecked()) {
                    getChargeFromServer("alipay");
                } else {
//                    getChargeFromServer("upacp");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           sureToBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sureToBack() {
        new AlertDialog.Builder(this, R.style.AlertSelf).setTitle("确认放弃支付吗？")
//                        .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("放弃支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        finish();
                        overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                    }
                })
                .setNegativeButton("我再想想", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }

    private void donePay() {
        Intent intent = new Intent(PayActivity.this, PayResultActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
    }

    public void getChargeFromServer(final String channel) {
        if (ids.length != mOrders.size()) {
            ToastUtil.toast("请稍后再试");
            finish();
            return;
        }

        double payment = 0d;
        String orderNo = "";
        String subject = "支付";
        String body = "";
        for (OrderMake orderMake : mOrders) {
            orderNo += orderMake.getSubOrders().get(0).getOrderCode() + "A";
            subject += orderMake.getSubOrders().get(0).getOrderCode() + "-";
            body = orderMake.getSubOrders().get(0).getOrderCode()
                    + getIdProductId(orderMake.getSubOrders().get(0).getGoods())
                    + "<" + orderMake.getPayment() + ">"
                    + "<" + orderMake.getSubOrders().get(0).getOrderTime() + ">";
            payment += orderMake.getPayment();
        }
        final String orderNos = orderNo.substring(0, orderNo.length() - 1);
        final String subjects;
        subject = subject.substring(0, subject.length() - 1);
        if (subject.length() > 128) {
            subjects = subject.substring(0, 128);
        }else{
            subjects = subject;
        }
        final String bodys = body;
        final String totalAmount = String.valueOf(payment);

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_PAYMENT_CHARGE)
                        , new ParamsBuilder()
                                .addParam("orderNo", orderNos)
                                .addParam("subject", subjects)
                                .addParam("body", "" + bodys)
                                .addParam("amount", totalAmount)
                                .addParam("channel", channel)
                                .getParams());
                final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = result.getData();
                            if (data.length() == 0) {
                                ToastUtil.toastByCode(result);
                            }
                            Pingpp.createPayment(PayActivity.this, data);
                        }
                    });
                } else {
                    ToastUtil.toastByCode(result);
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
            /* 处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
             */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                ToastUtil.toast("[payResult: " + result + "]=[error:" + errorMsg + "]" + "[extra:" + extraMsg + "]");
                LogUtil.w("payResult===:", "[payResult: " + result + "]=[error:" + errorMsg + "]" + "[extra:" + extraMsg + "]");
                if (result == null || result.length() == 0) {
                    result = "unknown";
                }

                switch (result) {
                    case "success":
                        ToastUtil.toast("支付完成");
                        donePay();
                        break;
                    case "fail":
                        ToastUtil.toast("支付失败");
                        finish();
                        break;
                    case "cancel":
                        ToastUtil.toast("支付取消");
                        finish();
                        break;
                    case "invalid":
                        ToastUtil.toast("支付插件未安装，请选择其他支付方式");
                        finish();
                        break;
                    case "unknown":
                    default:
                        ToastUtil.toast("支付异常，请刷新订单");
                        finish();
                        break;
                }

            }
        }
    }

    private String getIdProductId(List<GoodShow> list) {
        String str = "[";
        if (list != null && list.size() > 0) {
            for (GoodShow gs : list) {
                str += (gs.getProductId()+"-");
            }
        }
        return str.substring(0,str.length() - 1) + "]";
    }
}
