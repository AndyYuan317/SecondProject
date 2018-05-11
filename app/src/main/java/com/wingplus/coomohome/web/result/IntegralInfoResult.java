package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.IntegeralInfo;

/**
 * 积分信息结果
 *
 * @author leaffun.
 *         Create on 2017/10/27.
 */
public class IntegralInfoResult extends BaseResult {

    private IntegeralInfo data;

    public IntegeralInfo getData() {
        return data;
    }

    public void setData(IntegeralInfo data) {
        this.data = data;
    }
}
