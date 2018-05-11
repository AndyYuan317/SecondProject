package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.AfterSaleOrder;
import com.wingplus.coomohome.web.entity.PageContent;

/**
 * 售后单结果
 *
 * @author leaffun.
 *         Create on 2017/11/19.
 */
public class AfterSaleOrderResult extends BaseResult {
    private PageContent<AfterSaleOrder> data;

    public PageContent<AfterSaleOrder> getData() {
        return data;
    }

    public void setData(PageContent<AfterSaleOrder> data) {
        this.data = data;
    }
}
