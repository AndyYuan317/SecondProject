package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.OrderMake;

/**
 * 确认订单
 * @author leaffun.
 *         Create on 2017/11/7.
 */
public class OrderMakeResult extends BaseResult {
    private OrderMake data;

    public OrderMake getData() {
        return data;
    }

    public void setData(OrderMake data) {
        this.data = data;
    }
}
