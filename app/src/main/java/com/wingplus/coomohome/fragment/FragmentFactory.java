package com.wingplus.coomohome.fragment;

import android.util.SparseArray;

import com.wingplus.coomohome.config.LogUtil;
import com.wingplus.coomohome.fragment.aftersale.AfterSaleBaseFragment;
import com.wingplus.coomohome.fragment.aftersale.AfterSaleRecordFragment;
import com.wingplus.coomohome.fragment.aftersale.AfterSaleReturnFragment;
import com.wingplus.coomohome.fragment.coupon.CouponAlreadyFragment;
import com.wingplus.coomohome.fragment.coupon.CouponBaseFragment;
import com.wingplus.coomohome.fragment.coupon.CouponFreshFragment;
import com.wingplus.coomohome.fragment.coupon.CouponInvalidFragment;
import com.wingplus.coomohome.fragment.main.CartFragment;
import com.wingplus.coomohome.fragment.main.CategoryFragment;
import com.wingplus.coomohome.fragment.main.HomeFragment;
import com.wingplus.coomohome.fragment.main.MineFragment;
import com.wingplus.coomohome.fragment.main.PromotionFragment;
import com.wingplus.coomohome.fragment.order.OrderAllFragment;
import com.wingplus.coomohome.fragment.order.OrderAlreadySendFragment;
import com.wingplus.coomohome.fragment.order.OrderBaseFragment;
import com.wingplus.coomohome.fragment.order.OrderWaitingCommitFragment;
import com.wingplus.coomohome.fragment.order.OrderWaitingPayFragment;
import com.wingplus.coomohome.fragment.order.OrderWaitingSendFragment;

/**
 * Fragment单例工厂
 *
 * @author leaffun.
 *         Create on 2017/9/8.
 */
public class FragmentFactory {

    private static SparseArray<BaseFragment> mFragments = new SparseArray<>();
    private static SparseArray<OrderBaseFragment> mOrderFragments = new SparseArray<>();
    private static SparseArray<CouponBaseFragment> mCouponFragments = new SparseArray<>();
    private static SparseArray<AfterSaleBaseFragment> mAfterSaleFragments = new SparseArray<>();


    public static BaseFragment createFragment(int position) {
        BaseFragment fragment;
        fragment = mFragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new CategoryFragment();
                    break;
                case 2:
                    fragment = new PromotionFragment();
                    break;
                case 3:
                    fragment = new CartFragment();
                    break;
                case 4:
                    fragment = new MineFragment();
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                mFragments.put(position, fragment);
            } else {
                LogUtil.e(FragmentFactory.class.getSimpleName(), position + " create Fragment = null");
            }
        }
        return fragment;

    }

    public static void clearFragment() {
        mFragments.clear();
    }


    public static OrderBaseFragment createOrderFragment(int position) {
        OrderBaseFragment fragment;
        fragment = mOrderFragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new OrderAllFragment();
                    break;
                case 1:
                    fragment = new OrderWaitingPayFragment();
                    break;
                case 2:
                    fragment = new OrderWaitingSendFragment();
                    break;
                case 3:
                    fragment = new OrderAlreadySendFragment();
                    break;
                case 4:
                    fragment = new OrderWaitingCommitFragment();
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                mOrderFragments.put(position, fragment);
            } else {
                LogUtil.e(FragmentFactory.class.getSimpleName(), position + " create OrderFragment = null");
            }
        }
        return fragment;

    }

    public static void clearOrderFragment() {
        mOrderFragments.clear();
    }

    public static CouponBaseFragment createCouponFragment(int position) {
        CouponBaseFragment fragment;
        fragment = mCouponFragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new CouponFreshFragment();
                    break;
                case 1:
                    fragment = new CouponAlreadyFragment();
                    break;
                case 2:
                    fragment = new CouponInvalidFragment();
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                mCouponFragments.put(position, fragment);
            } else {
                LogUtil.e(FragmentFactory.class.getSimpleName(), position + " create CouponFragment = null");
            }
        }
        return fragment;

    }

    public static void clearCouponFragment() {
        mCouponFragments.clear();
    }

    public static AfterSaleBaseFragment createAfterSaleFragment(int position) {
        AfterSaleBaseFragment fragment;
        fragment = mAfterSaleFragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new AfterSaleRecordFragment();
                    break;
                case 1:
                    fragment = new AfterSaleReturnFragment();
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                mAfterSaleFragments.put(position, fragment);
            } else {
                LogUtil.e(FragmentFactory.class.getSimpleName(), position + " create AfterSaleFragment = null");
            }
        }
        return fragment;

    }

    public static void clearAfterSaleFragment() {
        mAfterSaleFragments.clear();
    }
}
