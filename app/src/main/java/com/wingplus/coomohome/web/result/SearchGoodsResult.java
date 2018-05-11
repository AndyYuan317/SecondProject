package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.PageContent;

/**
 * 搜索商品
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class SearchGoodsResult extends BaseResult {

    private PageContent<GoodShow> data;

    public PageContent<GoodShow> getData() {
        return data;
    }

    public void setData(PageContent<GoodShow> data) {
        this.data = data;
    }
}
