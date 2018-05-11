package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Coupon;

/**
 * @author leaffun.
 *         Create on 2018/1/8.
 */
public class CouponGetResult extends BaseResult {

    private Coupon data;

    public Coupon getData() {
        return data;
    }

    public void setData(Coupon data) {
        this.data = data;
    }
}
