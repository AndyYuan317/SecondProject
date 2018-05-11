package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.OrderAddress;

import java.util.List;

/**
 * 地址列表结果
 *
 * @author leaffun.
 *         Create on 2017/11/2.
 */
public class OrderAddressResult extends BaseResult {

    private List<OrderAddress> data;

    public List<OrderAddress> getData() {
        return data;
    }

    public void setData(List<OrderAddress> data) {
        this.data = data;
    }
}
