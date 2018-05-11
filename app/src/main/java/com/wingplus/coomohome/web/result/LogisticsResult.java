package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.LogisticsInfo;

/**
 * 物流信息查询结果
 *
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class LogisticsResult extends BaseResult {
    private LogisticsInfo data;

    public LogisticsInfo getData() {
        return data;
    }

    public void setData(LogisticsInfo data) {
        this.data = data;
    }
}
