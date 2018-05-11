package com.wingplus.coomohome.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.activity.AfterSaleActivity;
import com.wingplus.coomohome.activity.CollectActivity;
import com.wingplus.coomohome.activity.CouponActivity;
import com.wingplus.coomohome.activity.CustomerServiceActivity;
import com.wingplus.coomohome.activity.GiftCenterActivity;
import com.wingplus.coomohome.activity.IntegralActivity;
import com.wingplus.coomohome.activity.InviteFriendActivity;
import com.wingplus.coomohome.activity.LoginActivity;
import com.wingplus.coomohome.activity.MineOrderActivity;
import com.wingplus.coomohome.activity.MineProfileActivity;
import com.wingplus.coomohome.activity.MineWalletActivity;
import com.wingplus.coomohome.activity.NewsActivity;
import com.wingplus.coomohome.activity.OrderAddressActivity;
import com.wingplus.coomohome.activity.QRcodeActivity;
import com.wingplus.coomohome.activity.RegisterActivity;
import com.wingplus.coomohome.activity.ScanActivity;
import com.wingplus.coomohome.activity.SettingActivity;
import com.wingplus.coomohome.activity.StoreActivity;
import com.wingplus.coomohome.activity.WelfareActivity;
import com.wingplus.coomohome.component.GridViewMeasure;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.BaseFragment;
import com.wingplus.coomohome.util.GlideUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.TestDataProduceUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.XiaoNengUtil;
import com.wingplus.coomohome.web.Service;
import com.wingplus.coomohome.web.entity.OrderCnt;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.OrderCntResult;
import com.wingplus.coomohome.web.result.StringDataResult;

import java.util.ArrayList;
import java.util.List;

import cn.xiaoneng.uiapi.OnChatmsgListener;
import cn.xiaoneng.uiapi.OnUnreadmsgListener;

/**
 * 我的
 *
 * @author leaffun.
 *         Create on 2017/8/26.
 */
public class MineFragment extends BaseFragment implements OnUnreadmsgListener, OnChatmsgListener{

    private LayoutInflater myInflater;
    private Context context;
    private View rootView;

    private CardView profileImg;
    private ImageView headImage;
    private ImageView scan;
    private ImageView qr;
    private TextView allOrder;
    private GridViewMeasure orders;
    private GridViewMeasure services;
    private TextView loR;
    private TextView userName;
    private TextView userLv;
    private TextView newsNum;
    private TextView customerNum;
    private List<Service> orders1 = new ArrayList<>();
    private GridAdapter adapter;

    public MineFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflater = inflater;
        context = getContext();
        if (rootView == null) {
            rootView = myInflater.inflate(R.layout.fragment_main_mine, container, false);
        }
        initView();
        initEvent();
        List<Service> services = TestDataProduceUtil.getServices();
        this.services.setAdapter(new GridAdapter(services));

        orders1 = TestDataProduceUtil.getOrderItem();
        adapter = new GridAdapter(orders1);
        this.orders.setAdapter(adapter);

        XiaoNengUtil.setUnReadMsgListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RuntimeConfig.userValid()) {
            userName.setVisibility(View.VISIBLE);
            userLv.setVisibility(View.VISIBLE);
            loR.setVisibility(View.GONE);
            initData();
            XiaoNengUtil.setHeadImg(getActivity());
        } else {
            userName.setVisibility(View.GONE);
            userLv.setVisibility(View.GONE);
            loR.setVisibility(View.VISIBLE);
            GlideUtil.GlideInstance(context, R.drawable.icon_unregisteredavatar).into(headImage);
        }
        setNum();
    }

    @Override
    public void setStatusBarColor(Activity activity) {

    }

    private void initView() {
        profileImg = rootView.findViewById(R.id.profile_img);
        headImage = rootView.findViewById(R.id.headImg);
        scan = rootView.findViewById(R.id.profile_scan);
        qr = rootView.findViewById(R.id.profile_qr);
        allOrder = rootView.findViewById(R.id.all_order);


        orders = rootView.findViewById(R.id.order);
        services = rootView.findViewById(R.id.service);

        loR = rootView.findViewById(R.id.loginOrRegister);
        userName = rootView.findViewById(R.id.user_name);
        userLv = rootView.findViewById(R.id.user_lv);
    }

    private void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            private Intent intent = new Intent(getContext(), MineOrderActivity.class);

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.profile_img:
                        if (RuntimeConfig.userValid()) {
                            Intent proInt = new Intent(getContext(), MineProfileActivity.class);
                            startActivity(proInt);
                        } else {
                            gotoLogin();
                        }
                        break;
                    case R.id.profile_scan:
                        Intent scanInt = new Intent(getContext(), ScanActivity.class);
                        startActivity(scanInt);
                        break;
                    case R.id.profile_qr:
                        if (!RuntimeConfig.userValid()) {
                            gotoLogin();
                            return;
                        }
                        Intent qrInt = new Intent(getContext(), InviteFriendActivity.class);
                        startActivity(qrInt);
                        break;
                    case R.id.all_order:
                        if (RuntimeConfig.userValid()) {
                            intent.putExtra(MineOrderActivity.ORDER_KEY, MineOrderActivity.ORDER_ALL);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.loginOrRegister:
                        Intent login = new Intent(getContext(), RegisterActivity.class);
                        startActivity(login);
                        break;
                    default:
                        break;
                }
                getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
            }
        };
        profileImg.setOnClickListener(onClickListener);
        scan.setOnClickListener(onClickListener);
        qr.setOnClickListener(onClickListener);
        allOrder.setOnClickListener(onClickListener);
        loR.setOnClickListener(onClickListener);
    }

    private void initData() {

        userName.setText(RuntimeConfig.user.getUserName());
        userLv.setText(String.valueOf("LV." + RuntimeConfig.user.getLvName()));
        if (RuntimeConfig.user.getHeadImgUrl() != null && RuntimeConfig.user.getHeadImgUrl().length() > 0) {
            GlideUtil.GlideInstance(context, RuntimeConfig.user.getHeadImgUrl()).into(headImage);
        }

        APIConfig.getDataIntoView(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_ORDER_CNT),
                        new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                .getParams());
                final OrderCntResult result = GsonUtil.fromJson(rs, OrderCntResult.class);
                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                            OrderCnt data = result.getData();
                            if (data != null && orders1 != null && orders1.size() > 0) {
                                for (Service service : orders1) {
                                    switch (service.getType()) {
                                        case -1:
                                            service.setNum(data.getNotPayed());
                                            break;
                                        case -2:
                                            service.setNum(data.getPayed());
                                            break;
                                        case -3:
                                            service.setNum(data.getShipped());
                                            break;
                                        case -4:
                                            service.setNum(data.getNotCommented());
                                            break;
                                    }
                                }
                                adapter.setData(orders1);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onUnReadMsg(final String settingid, String username, String msgcontent, final int messagecount) {
        if(customerNum == null){
            return;
        }
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if(messagecount > 0){
                    customerNum.setVisibility(View.VISIBLE);
                    customerNum.setText(String.valueOf(messagecount));
                }else{
                    customerNum.setVisibility(View.GONE);
                }
            }
        });

        LogUtil.i("小能onUn未读消息",messagecount+"");
    }

    @Override
    public void onChatMsg(boolean b, String s, String s1, String s2, long l, boolean b1, int i, String s3) {
        if(customerNum == null){
            return;
        }
        if(i >= 0){
            customerNum.setVisibility(View.VISIBLE);
            customerNum.setText(String.valueOf(i));
        }else{
            customerNum.setVisibility(View.GONE);
        }
        LogUtil.i("小能onChat未读消息",i+"");
    }


    private class GridAdapter extends BaseAdapter {
        private List<Service> sers;

        public GridAdapter(List<Service> services) {
            this.sers = services;
        }

        public void setData(List<Service> services) {
            this.sers = services;
        }

        @Override
        public int getCount() {
            return sers == null ? 0 : sers.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = LinearLayout.inflate(context, R.layout.item_fragment_mine_grid_img, null);
            final ImageView img = inflate.findViewById(R.id.img);
            final Service service = sers.get(i);
            GlideUtil.GlideWithPlaceHolder(context, service.getImg()).into(img);

            TextView name = inflate.findViewById(R.id.name);
            name.setText(service.getName());
            TextView num = inflate.findViewById(R.id.num);
            switch (service.getType()) {
                case 3:
                    setNewsNum(num);
                    break;
                case 9:
                    setCustomerNum(num);
                    break;
            }

            if (service.getNum() != 0) {
                num.setVisibility(View.VISIBLE);
                num.setText(String.valueOf(service.getNum()));
            } else {
                num.setVisibility(View.GONE);
            }


            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call(service.getType());
                }
            });
            return inflate;
        }

        private void setNewsNum(final TextView num) {
            newsNum = num;
            setNum();
        }

        private void setCustomerNum(final TextView num){
            customerNum = num;
        }
    }

    private void setNum() {
        if (newsNum != null) {
            APIConfig.getDataIntoView(new Runnable() {
                @Override
                public void run() {
                    String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_UN_READ_CNT),
                            new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                    .getParams());
                    final StringDataResult result = GsonUtil.fromJson(rs, StringDataResult.class);
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS && !"0".equals(result.getMessage())) {
                                newsNum.setVisibility(View.VISIBLE);
                                newsNum.setText(result.getMessage());
                                RuntimeConfig.setPushNumber(context, Integer.parseInt(result.getMessage()));
                            } else {
                                newsNum.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
    }

    private void call(int type) {
        Intent intent = new Intent(context, MineOrderActivity.class);

        if ((type <= 4 || (type >= 8 && type <= 10))
                && !RuntimeConfig.userValid()) {
            gotoLogin();
            return;
        }

        switch (type) {
            case -1:
                intent.putExtra(MineOrderActivity.ORDER_KEY, MineOrderActivity.ORDER_WAITING_PAY);
                break;
            case -2:
                intent.putExtra(MineOrderActivity.ORDER_KEY, MineOrderActivity.ORDER_WAIT_SEND);
                break;
            case -3:
                intent.putExtra(MineOrderActivity.ORDER_KEY, MineOrderActivity.ORDER_ALREADY_SEND);
                break;
            case -4:
                intent.putExtra(MineOrderActivity.ORDER_KEY, MineOrderActivity.ORDER_WAIT_COMMIT);
                break;
            case -5:
                intent = new Intent(context, AfterSaleActivity.class);
                break;
            case 0:
                intent = new Intent(context, MineWalletActivity.class);
                break;
            case 1:
                intent = new Intent(context, CouponActivity.class);
                break;
            case 2:
                intent = new Intent(context, IntegralActivity.class);
                break;
            case 3:
                intent = new Intent(context, NewsActivity.class);
                break;
            case 4:
                intent = new Intent(context, CollectActivity.class);
                break;
            case 5:
                intent = new Intent(context, StoreActivity.class);
                break;
            case 6:
                intent = new Intent(context, WelfareActivity.class);
                break;
            case 7:
                intent = new Intent(context, GiftCenterActivity.class);
                break;
            case 8:
                intent = new Intent(context, OrderAddressActivity.class);
                break;
            case 9:
                intent = new Intent(context, CustomerServiceActivity.class);
                break;
            case 10:
                intent = new Intent(context, InviteFriendActivity.class);
                break;
            case 11:
                intent = new Intent(context, SettingActivity.class);
                break;
            default:
//                Intent webInt = new Intent(getContext(), WebActivity.class);
//                webInt.putExtra("url", "http://www.jd.com");
//                startActivity(webInt);
                break;
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
    }

    private void gotoLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.open_x_enter, R.anim.open_x_exit);
    }
}
