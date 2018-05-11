package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.GoodCommit;
import com.wingplus.coomohome.web.entity.PageContent;

/**
 * 商品评价结果
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class GoodCommitResult extends BaseResult {
    public PageContent<GoodCommit> getData() {
        return data;
    }

    public void setData(PageContent<GoodCommit> data) {
        this.data = data;
    }

    private PageContent<GoodCommit> data;


}
