package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Coupon;

import java.util.List;

/**
 * 优惠券
 * @author leaffun.
 *         Create on 2017/10/24.
 */
public class CouponResult extends BaseResult {
    private List<Coupon> data;

    public List<Coupon> getData() {
        return data;
    }

    public void setData(List<Coupon> data) {
        this.data = data;
    }
}
