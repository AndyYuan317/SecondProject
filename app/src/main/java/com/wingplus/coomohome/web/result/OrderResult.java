package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Order;

import java.util.List;

/**
 * 订单结果
 * @author leaffun.
 *         Create on 2017/10/25.
 */
public class OrderResult extends BaseResult {
    private List<Order> data;

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
