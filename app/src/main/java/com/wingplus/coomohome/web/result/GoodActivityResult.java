package com.wingplus.coomohome.web.result;

import com.wingplus.coomohome.web.entity.GoodActivity;

/**
 * 商品活动结果
 *
 * @author leaffun.
 *         Create on 2017/11/2.
 */
public class GoodActivityResult extends BaseResult {
    private GoodActivity data;

    public GoodActivity getData() {
        return data;
    }

    public void setData(GoodActivity data) {
        this.data = data;
    }
}
