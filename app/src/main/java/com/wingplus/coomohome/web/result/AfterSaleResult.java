package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.AfterSaleOrder;

/**
 * 售后单详细
 * @author leaffun.
 *         Create on 2017/11/20.
 */
public class AfterSaleResult extends BaseResult {
    private AfterSaleOrder data;

    public AfterSaleOrder getData() {
        return data;
    }

    public void setData(AfterSaleOrder data) {
        this.data = data;
    }
}
