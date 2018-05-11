package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.ActivityInfo;

/**
 * 活动信息列表
 * @author leaffun.
 *         Create on 2017/10/23.
 */
public class ActivityInfoResult extends BaseResult {

    private ActivityInfo data;

    public ActivityInfo getData() {
        return data;
    }

    public void setData(ActivityInfo data) {
        this.data = data;
    }
}
