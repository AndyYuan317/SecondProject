package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.PageContent;
import com.wingplus.coomohome.web.entity.RechargeRecord;

/**
 * 钱包历史充值记录
 *
 * @author leaffun.
 *         Create on 2017/10/24.
 */
public class RechargeRecordResult extends BaseResult {
    private PageContent<RechargeRecord> data;

    public PageContent<RechargeRecord> getData() {
        return data;
    }

    public void setData(PageContent<RechargeRecord> data) {
        this.data = data;
    }
}
