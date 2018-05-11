package com.wingplus.coomohome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.ToggleViewPager;
import com.wingplus.coomohome.config.APIConfig;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.config.RuntimeConfig;
import com.wingplus.coomohome.fragment.FragmentFactory;
import com.wingplus.coomohome.fragment.coupon.CouponBaseFragment;
import com.wingplus.coomohome.fragment.coupon.CouponFreshFragment;
import com.wingplus.coomohome.util.DisplayUtil;
import com.wingplus.coomohome.util.GsonUtil;
import com.wingplus.coomohome.util.HttpUtil;
import com.wingplus.coomohome.util.ToastUtil;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;
import com.wingplus.coomohome.web.param.ParamsBuilder;
import com.wingplus.coomohome.web.result.CouponGetResult;
import com.wingplus.coomohome.web.result.CouponResult;

import java.util.ArrayList;

/**
 * 我的优惠券
 *
 * @author leaffun.
 *         Create on 2017/10/12.
 */
public class CouponActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ArrayList<String> title = new ArrayList<>();
    private TabLayout mTabs;
    private ToggleViewPager fragPager;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        initView();
        setCurrentItem(getIntent().getIntExtra(Constant.CouponStatus.COUPON_STATUS_KEY, Constant.CouponStatus.FRESH));
    }

    @Override
    protected void onDestroy() {
        FragmentFactory.clearCouponFragment();
        super.onDestroy();
    }

    private void initView() {
        mTabs = findViewById(R.id.tabs);
        fragPager = findViewById(R.id.viewpager);
        title.add("未使用");
        title.add("已使用");
        title.add("已失效");
        for (int i = 0; i < title.size(); i++) {
            mTabs.addTab(mTabs.newTab().setText(title.get(i)));
        }

        adapter = new OrderAdapter(getSupportFragmentManager());
        fragPager.setAdapter(adapter);
        fragPager.setOffscreenPageLimit(2);
        fragPager.setCanScroll(true);
        ViewUtils.reflex(mTabs, false);
        mTabs.setupWithViewPager(fragPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.close_x_enter, R.anim.close_x_exit);
                break;
            case R.id.getCoupon:
                View getCoupon = UIUtils.inflate(R.layout.item_get_coupon);
                final EditText et = getCoupon.findViewById(R.id.code);
                TextView textView = new TextView(this);
                textView.setGravity(Gravity.START);
                textView.setText("领取优惠券");
                textView.setTextColor(UIUtils.getColor(R.color.titleBlack));
                textView.setTextSize(16);
                textView.setPadding(UIUtils.dip2px(20), UIUtils.dip2px(22), UIUtils.dip2px(0), UIUtils.dip2px(10));
                new AlertDialog.Builder(this, R.style.AlertSelf).setCustomTitle(textView)
                        .setView(getCoupon)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String input = et.getText().toString();
                                if (input.equals("")) {
                                    ToastUtil.toast("优惠券号码不可以为空");
                                } else {
                                    APIConfig.getDataIntoView(new Runnable() {
                                        @Override
                                        public void run() {
                                            String rs = HttpUtil.GetDataFromNetByPost(APIConfig.getAPIUrl(APIConfig.API_COUPON_GET)
                                                    , new ParamsBuilder().addParam("token", RuntimeConfig.user.getToken())
                                                            .addParam("couponNo", input)
                                                            .getParams());
                                            CouponGetResult result = GsonUtil.fromJson(rs, CouponGetResult.class);
                                            if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
                                                ToastUtil.toast("领取成功");
                                                UIUtils.runOnUIThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        refreshData();
                                                    }
                                                });
                                            } else {
                                                ToastUtil.toastByCode(result);
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();


                break;
            default:
                break;
        }
    }

    private void refreshData() {
        for (int i = 0; i < adapter.getCount(); i++) {
            CouponBaseFragment item = (CouponBaseFragment) adapter.getItem(i);
            item.loadData();

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

    public void setCurrentItem(int position) {
        fragPager.setCurrentItem(position, false);
    }


    private class OrderAdapter extends FragmentPagerAdapter {

        public OrderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createCouponFragment(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }
    }


}
