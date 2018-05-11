package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.DlyCenter;

/**
 * 货仓信息
 * @author leaffun.
 *         Create on 2017/11/30.
 */
public class DlyCenterResult extends BaseResult {
    private DlyCenter data;

    public DlyCenter getData() {
        return data;
    }

    public void setData(DlyCenter data) {
        this.data = data;
    }
}
