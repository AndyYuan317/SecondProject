package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.CartNum;

/**
 * 购物车数量结果
 *
 * @author leaffun.
 *         Create on 2017/11/9.
 */
public class CartNumResult extends BaseResult {
    private CartNum data;

    public CartNum getData() {
        return data;
    }

    public void setData(CartNum data) {
        this.data = data;
    }
}
