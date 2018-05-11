package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.PageContent;
import com.wingplus.coomohome.web.entity.Welfare;

/**
 * 公益活动结果
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class WelfareResult extends BaseResult {

    private PageContent<Welfare> data;

    public PageContent<Welfare> getData() {
        return data;
    }

    public void setData(PageContent<Welfare> data) {
        this.data = data;
    }
}
