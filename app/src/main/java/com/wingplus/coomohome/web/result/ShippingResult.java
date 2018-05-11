package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.Shipping;

import java.util.List;

/**
 * 配送方式
 *
 * @author leaffun.
 *         Create on 2017/11/8.
 */
public class ShippingResult extends BaseResult {
    private List<Shipping> data;

    public List<Shipping> getData() {
        return data;
    }

    public void setData(List<Shipping> data) {
        this.data = data;
    }
}
