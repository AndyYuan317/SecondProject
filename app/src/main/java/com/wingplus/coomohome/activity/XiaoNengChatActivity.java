package com.wingplus.coomohome.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.XiaoNengUtil;

import cn.xiaoneng.activity.ChatActivity;
import cn.xiaoneng.activity.XNExplorerActivity;
import cn.xiaoneng.image.ImageShow;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.uiapi.OnCustomMsgListener;
import cn.xiaoneng.uiapi.OnPlusFunctionClickListener;
import cn.xiaoneng.utils.NtLog;

/**
 * 小能聊天窗口
 * Created by leaffun on 2018/3/26.Ø
 */
public class XiaoNengChatActivity extends ChatActivity implements OnPlusFunctionClickListener, OnCustomMsgListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Ntalker.getExtendInstance().extensionArea().setOnPlusFunctionClickListener(this);

        Ntalker.getExtendInstance().message().setOnCustomMsgListener(1, R.layout.customorder_demo, XiaoNengChatActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPlusFunctionClick(String funcName) {
        if (XiaoNengUtil.FUNC_NAME_ORDER.equals(funcName)) {
            Intent intent = new Intent(XiaoNengChatActivity.this, CustomerServiceOrderActivity.class);
            intent.putExtra("from", "客服");
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String orderCode = data.getExtras().getString(Constant.Key.KEY_ORDER_ID_OR_CODE);
            String orderGoodsNum = data.getExtras().getString("订单商品数");
            String orderTime = data.getExtras().getString("订单时间");
            String orderMoney = data.getExtras().getString("订单金额");
            String orderImg = data.getExtras().getString("订单图片");

            String[] msgs = {orderCode, orderTime, orderGoodsNum, orderMoney, orderImg};

            Ntalker.getExtendInstance().message().sendCustomMsg(1, msgs);
        }


    }

    @Override
    public void setCustomViewFromDB(View view, int msgType, final String[] msg) {
        if (msgType == 1) {
            TextView tv_orderid = (TextView) view.findViewById(R.id.tv_orderid_demo);
            TextView tv_ordernum = (TextView) view.findViewById(R.id.tv_ordernum_demo);
            TextView tv_orderprice = (TextView) view.findViewById(R.id.tv_orderprice_demo);
            TextView tv_ordertime = (TextView) view.findViewById(R.id.tv_ordertime_demo);
            ImageView pic = (ImageView) view.findViewById(R.id.iv_icon_demo);
            RelativeLayout rl_custom = (RelativeLayout) view.findViewById(R.id.rl_custom);
            NtLog.i_logic("custom  接收 " + msg[4]);
            tv_orderid.setText(msg[0]);
            tv_ordernum.setText(msg[1]);
            tv_ordertime.setText(msg[2]);
            tv_orderprice.setText(msg[3]);
            ImageShow.getInstance(view.getContext()).DisplayImage(ImageShow.IMAGE_NORMAL, null, msg[4], pic, null, R.drawable.load_err_empty, R.drawable.load_err_empty, null);

            rl_custom.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(XiaoNengChatActivity.this, OrderDetailActivity.class);
//                    intent.putExtra(Constant.Key.KEY_ORDER_ID_OR_CODE,msg[0]);
//                    intent.putExtra("urlintextmsg", "https://www.wbiao.cn/epos-g53474.html");// 实际打开的链接
//                    startActivity(intent);
//                    Toast.makeText(v.getContext(), "点击跳转到订单", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.close_x_enter,R.anim.close_x_exit);
    }
}
