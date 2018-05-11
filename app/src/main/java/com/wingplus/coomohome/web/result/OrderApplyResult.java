package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.OrderApply;

import java.util.List;

/**
 * （提交）确认订单结果
 *
 * @author leaffun.
 *         Create on 2017/11/9.
 */
public class OrderApplyResult extends BaseResult {
    private List<OrderApply> data;

    public List<OrderApply> getData() {
        return data;
    }

    public void setData(List<OrderApply> data) {
        this.data = data;
    }
}
