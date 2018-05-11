package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.AfterSaleValid;
import com.wingplus.coomohome.web.entity.PageContent;

/**
 * 可退货结果
 *
 * @author leaffun.
 *         Create on 2017/11/19.
 */
public class AfterSaleValidResult extends BaseResult {

    private PageContent<AfterSaleValid> data;

    public PageContent<AfterSaleValid> getData() {
        return data;
    }

    public void setData(PageContent<AfterSaleValid> data) {
        this.data = data;
    }
}
