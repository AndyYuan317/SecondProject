package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.CartData;

/**
 * 购物车信息结果
 *
 * @author leaffun.
 *         Create on 2017/11/3.
 */
public class CartResult extends BaseResult {
    private CartData data;

    public CartData getData() {
        return data;
    }

    public void setData(CartData data) {
        this.data = data;
    }
}
