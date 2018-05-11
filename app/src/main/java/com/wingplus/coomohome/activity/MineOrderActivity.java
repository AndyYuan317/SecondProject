package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.ToggleViewPager;
import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.fragment.FragmentFactory;
import com.wingplus.coomohome.fragment.order.OrderBaseFragment;
import com.wingplus.coomohome.util.ViewUtils;

import java.util.ArrayList;

/**
 * 我的订单
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class MineOrderActivity extends BaseFragmentActivity implements View.OnClickListener {
    public static final String ORDER_KEY = "order_type";
    public static final int ORDER_ALL = 0;
    public static final int ORDER_WAITING_PAY = 1;
    public static final int ORDER_WAIT_SEND = 2;
    public static final int ORDER_ALREADY_SEND = 3;
    public static final int ORDER_WAIT_COMMIT = 4;


    private ArrayList<String> title = new ArrayList<>();
    private TabLayout mTabs;
    private ToggleViewPager fragPager;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_order);

        initView();
        setCurrentItem(getIntent().getIntExtra(ORDER_KEY, ORDER_ALL));
    }

    @Override
    protected void onDestroy() {
        FragmentFactory.clearOrderFragment();
        super.onDestroy();
    }

    private void initView() {
        mTabs = findViewById(R.id.tabs);
        fragPager = findViewById(R.id.viewpager);
        title.add("全部");
        title.add("待付款");
        title.add("待发货");
        title.add("待收货");
        title.add("待评价");
        for (int i = 0; i < title.size(); i++) {
            mTabs.addTab(mTabs.newTab().setText(title.get(i)));
        }

        adapter = new OrderAdapter(getSupportFragmentManager());
        fragPager.setAdapter(adapter);
        fragPager.setOffscreenPageLimit(4);
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

    public void setCurrentItem(int position) {
        fragPager.setCurrentItem(position, false);
    }


    private class OrderAdapter extends FragmentPagerAdapter {

        public OrderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createOrderFragment(position);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }
    }

    /**
     * 由全部、已收货、待评价页面更新数据
     */
    public void sureGet() {
        for (int i = 0; i < adapter.getCount(); i++) {
            ((OrderBaseFragment) adapter.getItem(i)).sureGet();
        }
    }
}
