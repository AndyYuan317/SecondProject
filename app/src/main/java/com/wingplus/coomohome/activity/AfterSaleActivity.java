package com.wingplus.coomohome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.component.ToggleViewPager;
import com.wingplus.coomohome.fragment.FragmentFactory;
import com.wingplus.coomohome.util.UIUtils;
import com.wingplus.coomohome.util.ViewUtils;

import java.util.ArrayList;

/**
 * 售后
 *
 * @author leaffun.
 *         Create on 2017/9/10.
 */
public class AfterSaleActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ArrayList<String> title = new ArrayList<>();
    private TabLayout mTabs;
    private ToggleViewPager fragPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_sale);
        initView();
    }

    @Override
    protected void onDestroy() {
        FragmentFactory.clearAfterSaleFragment();
        super.onDestroy();
    }

    private void initView() {
        mTabs = findViewById(R.id.tabs);
        fragPager = findViewById(R.id.viewpager);
        title.add("记录");
        title.add("退货");
        for (int i = 0; i < title.size(); i++) {
            TabLayout.Tab tab = mTabs.newTab();
            mTabs.addTab(tab);
        }

        AfterSaleAdapter adapter = new AfterSaleAdapter(getSupportFragmentManager());
        fragPager.setAdapter(adapter);
        fragPager.setOffscreenPageLimit(2);
        fragPager.setCanScroll(true);
        mTabs.setupWithViewPager(fragPager);

        for (int i = 0; i < mTabs.getTabCount(); i++) {
            TabLayout.Tab tab = mTabs.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }

        ViewUtils.reflex(mTabs, true);
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

    public void setCurrentItem(int position) {
        fragPager.setCurrentItem(position, false);
    }


    private class AfterSaleAdapter extends FragmentPagerAdapter {

        public AfterSaleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createAfterSaleFragment(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }

        public View getTabView(int position) {
            View view = LayoutInflater.from(AfterSaleActivity.this).inflate(R.layout.item_tab_custom_drawable, null);
            TextView tv = (TextView) view.findViewById(R.id.txt);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            tv.setText(title.get(position));
            if (position == 0) {
                img.setImageDrawable(UIUtils.getDrawable(R.drawable.selector_tab_img_record));
            } else {
                img.setImageDrawable(UIUtils.getDrawable(R.drawable.selector_tab_img_retreat));
            }
            return view;
        }
    }

}
