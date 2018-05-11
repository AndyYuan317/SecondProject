package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.OrderCnt;

/**
 * 订单计数结果
 * @author leaffun.
 *         Create on 2017/12/14.
 */
public class OrderCntResult extends BaseResult {
    private OrderCnt data;

    public OrderCnt getData() {
        return data;
    }

    public void setData(OrderCnt data) {
        this.data = data;
    }
}
